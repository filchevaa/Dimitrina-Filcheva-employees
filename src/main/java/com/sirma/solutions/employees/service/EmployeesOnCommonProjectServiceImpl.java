package com.sirma.solutions.employees.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.sirma.solutions.employees.exception.ExecutorTimeoutException;
import com.sirma.solutions.employees.exception.GenericException;
import com.sirma.solutions.employees.model.EmployeeOnCommonProjectDTO;
import com.sirma.solutions.employees.model.EmployeeOnCommonProjectCsvDataRowDTO;
import com.sirma.solutions.employees.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Component
public class EmployeesOnCommonProjectServiceImpl implements EmployeesOnCommonProjectService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeesOnCommonProjectServiceImpl.class);
    private static final CsvMapper CSV_MAPPER = CsvMapper.builder().disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY).build();
    private static final CsvSchema CSV_SCHEMA = CSV_MAPPER.schemaFor(EmployeeOnCommonProjectCsvDataRowDTO.class).withColumnSeparator(',');

    private final long timeoutSeconds;

    @Autowired
    public EmployeesOnCommonProjectServiceImpl(
            @Value("${file.upload.timeout.seconds:60}") long timeoutSeconds
    ) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public List<EmployeeOnCommonProjectDTO> getEmployeesOnCommonProject(MultipartFile csvFile) throws IOException {
        try {
            List<EmployeeOnCommonProjectCsvDataRowDTO> csvDataRowDTOS = readCsvFile(csvFile);
            return getEmployeeOnCommonProjectDTOS(csvDataRowDTOS);
        } catch (ExecutorTimeoutException e) {
            UUID errorId = UUID.randomUUID();
            LOGGER.info(String.format("Error id: %s, %s", errorId, e.getMessage()));
            throw new GenericException(String.format("Internal Server error. Please contact customer support and provide this code %s", errorId));
        } catch (Exception e) {
            UUID errorId = UUID.randomUUID();
            LOGGER.error(String.format("Error id: %s, %s", errorId, e.getMessage()), e);
            throw new GenericException(String.format("Internal Server error. Please contact customer support and provide this code: %s", errorId));
        }
    }


    private List<EmployeeOnCommonProjectDTO> getEmployeeOnCommonProjectDTOS(List<EmployeeOnCommonProjectCsvDataRowDTO> csvDataRowDTOS) {
        List<EmployeeOnCommonProjectDTO> result = new ArrayList<>();

        Map<String, List<EmployeeOnCommonProjectCsvDataRowDTO>> projectEmployeesMap = csvDataRowDTOS.stream().collect(Collectors.groupingBy(EmployeeOnCommonProjectCsvDataRowDTO::getProjectId));
        projectEmployeesMap.forEach((projectId, employees) -> {

            Set<EmployeeOnCommonProjectDTO> employeePairsOnCurrentProject = new HashSet<>();

            List<EmployeeOnCommonProjectCsvDataRowDTO> employeesList = new CopyOnWriteArrayList<>(employees);
            employeesList.forEach(employeeOne -> {
                // can be achieved with object mutation but won`t be as readable
                // the performance boost would be questionable, so we go for simplicity and code readability
                employeesList.remove(employeeOne);
                employeesList.forEach(employeeTwo -> {
                    employeePairsOnCurrentProject.add(creeateEmployeeOnCommonProjectDTO(employeeOne, employeeTwo));
                });
            });
            Optional<EmployeeOnCommonProjectDTO> employeeThatWorkedTheLongestTogether = employeePairsOnCurrentProject.stream().
                    filter(o -> o.getNumberOfDaysWorkedTogether() > 0)
                    .max(Comparator.comparingLong(EmployeeOnCommonProjectDTO::getNumberOfDaysWorkedTogether));
            employeeThatWorkedTheLongestTogether.ifPresent(result::add);
        });

        return result;
    }

    private EmployeeOnCommonProjectDTO creeateEmployeeOnCommonProjectDTO(
            EmployeeOnCommonProjectCsvDataRowDTO employeeOne,
            EmployeeOnCommonProjectCsvDataRowDTO employeeTwo
    ) {

        Long employeesCommonTime = getAmountOfDaysWorkedOnTheSameProject(employeeOne, employeeTwo);
        EmployeeOnCommonProjectDTO result = new EmployeeOnCommonProjectDTO();

        result.setProjectId(employeeOne.getProjectId());
        result.setEmployeeOneId(employeeOne.getEmployeeId());
        result.setEmployeeTwoId(employeeTwo.getEmployeeId());
        result.setNumberOfDaysWorkedTogether(employeesCommonTime);

        return result;
    }

    private Long getAmountOfDaysWorkedOnTheSameProject(EmployeeOnCommonProjectCsvDataRowDTO employeeOne, EmployeeOnCommonProjectCsvDataRowDTO employeeTwo) {
        Instant employeeOneStartDate = DateUtil.parseDate(employeeOne.getDateFrom());
        Instant employeeOneEndDate = employeeOne.getDateTo() != null && !employeeOne.getDateTo().equals("NULL") ? DateUtil.parseDate(employeeOne.getDateTo()) : Instant.now();

        Instant employeeTwoStartDate = DateUtil.parseDate(employeeTwo.getDateFrom());
        Instant employeeTwoEndDate = employeeTwo.getDateTo() != null && !employeeTwo.getDateTo().equals("NULL") ? DateUtil.parseDate(employeeTwo.getDateTo()) : Instant.now();

        Instant interceptionStartDate = DateUtil.getMaxInstant(employeeOneStartDate, employeeTwoStartDate);
        Instant interceptionEndDate = DateUtil.getMinInstant(employeeOneEndDate, employeeTwoEndDate);
        Duration calculatedTime = Duration.between(interceptionStartDate, interceptionEndDate);

        return calculatedTime.toDays();
    }

    private List<EmployeeOnCommonProjectCsvDataRowDTO> readCsvFile(MultipartFile file) throws IOException, ExecutionException, InterruptedException {
        return readCsvFile(file, true);
    }

    private List<EmployeeOnCommonProjectCsvDataRowDTO> readCsvFile(MultipartFile file, boolean removeHeader) throws IOException, ExecutionException, InterruptedException {
        List<EmployeeOnCommonProjectCsvDataRowDTO> result = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        try (InputStream inputStream = file.getInputStream(); BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            List<Future<EmployeeOnCommonProjectCsvDataRowDTO>> futures = new ArrayList<>();

            if (removeHeader) {
                reader.readLine();
            }

            reader.lines().forEach(line -> {
                Future<EmployeeOnCommonProjectCsvDataRowDTO> future = executor.submit(() -> createEmployeeOnCommonProjectCsvDataRowDTO(line));
                futures.add(future);
            });

            executor.shutdown();
            boolean terminated = executor.awaitTermination(timeoutSeconds, TimeUnit.SECONDS);
            if (!terminated) {
                throw new ExecutorTimeoutException("Processing timeout reached. Please retry your file or increase processing time timeout.");
            }

            for (Future<EmployeeOnCommonProjectCsvDataRowDTO> future : futures) {
                result.add(future.get());
            }
        }

        return result;
    }

    private EmployeeOnCommonProjectCsvDataRowDTO createEmployeeOnCommonProjectCsvDataRowDTO(String line) throws JsonProcessingException {
        return CSV_MAPPER.readerFor(EmployeeOnCommonProjectCsvDataRowDTO.class).with(CSV_SCHEMA).readValue(line);
    }

}
