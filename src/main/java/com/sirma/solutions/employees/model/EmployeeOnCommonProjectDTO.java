package com.sirma.solutions.employees.model;

import java.util.Objects;

public class EmployeeOnCommonProjectDTO {

    private String employeeOneId;
    private String employeeTwoId;
    private String projectId;
    private Long numberOfDaysWorkedTogether;

    public EmployeeOnCommonProjectDTO() {
    }

    public EmployeeOnCommonProjectDTO(String employeeOneId, String employeeTwoId, String projectId, Long numberOfDaysWorkedTogether) {
        this.employeeOneId = employeeOneId;
        this.employeeTwoId = employeeTwoId;
        this.projectId = projectId;
        this.numberOfDaysWorkedTogether = numberOfDaysWorkedTogether;
    }

    public String getEmployeeOneId() {
        return employeeOneId;
    }

    public void setEmployeeOneId(String employeeOneId) {
        this.employeeOneId = employeeOneId;
    }

    public String getEmployeeTwoId() {
        return employeeTwoId;
    }

    public void setEmployeeTwoId(String employeeTwoId) {
        this.employeeTwoId = employeeTwoId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public Long getNumberOfDaysWorkedTogether() {
        return numberOfDaysWorkedTogether;
    }

    public void setNumberOfDaysWorkedTogether(Long numberOfDaysWorkedTogether) {
        this.numberOfDaysWorkedTogether = numberOfDaysWorkedTogether;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeOnCommonProjectDTO that = (EmployeeOnCommonProjectDTO) o;
        return ((Objects.equals(employeeOneId, that.employeeOneId) && Objects.equals(employeeTwoId, that.employeeTwoId)) || (Objects.equals(employeeOneId, that.employeeTwoId) && Objects.equals(employeeTwoId, that.employeeOneId))) &&
                Objects.equals(projectId, that.projectId) &&
                Objects.equals(numberOfDaysWorkedTogether, that.numberOfDaysWorkedTogether);
    }
}
