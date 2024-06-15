package com.sirma.solutions.employees;

import com.sirma.solutions.employees.model.EmployeeOnCommonProjectDTO;
import com.sirma.solutions.employees.service.EmployeesOnCommonProjectServiceImpl;
import com.sirma.solutions.employees.util.DateUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;

@SpringBootTest
class SirmaSolutionsTaskApplicationTests {
    @Value("classpath:employee_list.csv")
    private Resource csvResource;

    @Autowired
    private EmployeesOnCommonProjectServiceImpl commonEmployeeService;


    @Test
    void testDateFormatService() throws IOException {

        String dateString = "2012-05-16";
        Assertions.assertDoesNotThrow(
                () -> DateUtil.parseDate(dateString),
                String.format("Unsupported format: %s", dateString)
        );
    }

    @Test
    void testCommonEmployeeService() throws IOException, ExecutionException, InterruptedException {
        List<EmployeeOnCommonProjectDTO> result = commonEmployeeService.getEmployeesOnCommonProject(
                getMultipartFile()
        );
        Assertions.assertEquals(result.size(),2);
    }

    private MultipartFile getMultipartFile() throws IOException {
        Path path = Paths.get(csvResource.getFile().getAbsolutePath());
        byte[] content = Files.readAllBytes(path);

        return new MockMultipartFile("file", path.getFileName().toString(), "text/plain", content);
    }


}
