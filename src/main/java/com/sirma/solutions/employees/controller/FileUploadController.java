package com.sirma.solutions.employees.controller;

import com.sirma.solutions.employees.exception.GenericException;
import com.sirma.solutions.employees.model.EmployeeOnCommonProjectDTO;
import com.sirma.solutions.employees.service.EmployeesOnCommonProjectServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
public class FileUploadController {

    private final EmployeesOnCommonProjectServiceImpl employeesOnCommonProjectService;

    @Autowired
    public FileUploadController(EmployeesOnCommonProjectServiceImpl employeesOnCommonProjectService) {
        this.employeesOnCommonProjectService = employeesOnCommonProjectService;
    }

    @GetMapping
    public String showForm() {
        return "upload-form";
    }

    @PostMapping
    public ModelAndView handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {

        List<EmployeeOnCommonProjectDTO> employeeOnCommonProjectsDTOList = employeesOnCommonProjectService.getEmployeesOnCommonProject(
                file
        );
        ModelAndView modelAndView = new ModelAndView("employee-view");
        modelAndView.addObject("employeeOnCommonProjectsDTOList", employeeOnCommonProjectsDTOList);
        return modelAndView;
    }

    @ExceptionHandler(value = {GenericException.class})
    public ModelAndView handleGenericException(GenericException ex) {
        ModelAndView modelAndView = new ModelAndView("upload-form");
        Map<String, String> errorsMap = new HashMap<>();
        errorsMap.put("errorMessage", ex.getMessage());
        modelAndView.addObject("errorsMap", errorsMap);
        return modelAndView;
    }
}
