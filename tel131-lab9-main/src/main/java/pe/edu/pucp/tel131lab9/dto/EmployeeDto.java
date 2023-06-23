package pe.edu.pucp.tel131lab9.dto;

import pe.edu.pucp.tel131lab9.bean.Employee;

public class EmployeeDto extends Employee {
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
