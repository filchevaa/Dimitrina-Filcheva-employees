package com.sirma.solutions.employees.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"employeeId", "projectId", "dateFrom", "dateTo"})
public class EmployeeOnCommonProjectCsvDataRowDTO {
    private String employeeId;
    private String projectId;
    private String dateFrom;
    private String dateTo;

    public EmployeeOnCommonProjectCsvDataRowDTO() {
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }
}
