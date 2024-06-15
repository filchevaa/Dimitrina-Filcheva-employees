package com.sirma.solutions.employees.service;

import com.sirma.solutions.employees.model.EmployeeOnCommonProjectDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface EmployeesOnCommonProjectService {
    List<EmployeeOnCommonProjectDTO> getEmployeesOnCommonProject(MultipartFile csvFile) throws IOException;
}
