package pe.edu.pucp.tel131lab9.dao;


import pe.edu.pucp.tel131lab9.bean.Department;
import pe.edu.pucp.tel131lab9.bean.Employee;
import pe.edu.pucp.tel131lab9.bean.Job;
import pe.edu.pucp.tel131lab9.dto.EmployeeDto;

import java.sql.*;
import java.util.ArrayList;

public class EmployeeDao extends DaoBase {

    public ArrayList<Employee> listEmployee() {
        ArrayList<Employee> employees = new ArrayList<>();

        String sql = "SELECT * FROM employees e \n"
                + "left join jobs j on (j.job_id = e.job_id) \n"
                + "left join departments d on (d.department_id = e.department_id)\n"
                + "left  join employees m on (e.manager_id = m.employee_id)";
        try (Connection conn = this.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Employee employee = new Employee();
                fetchEmployeeData(employee, rs);

                employees.add(employee);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return employees;
    }

    public Employee getEmployee(int employeeId) {

        Employee employee = null;

        String sql = "SELECT * FROM employees e \n"
                + "left join jobs j ON (j.job_id = e.job_id) \n"
                + "left join departments d ON (d.department_id = e.department_id)\n"
                + "left  join employees m ON (e.manager_id = m.employee_id)\n"
                + "WHERE e.employee_id = ?";

        try (Connection conn = this.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setInt(1, employeeId);

            try (ResultSet rs = statement.executeQuery()) {

                if (rs.next()) {
                    employee = new Employee();
                    fetchEmployeeData(employee, rs);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return employee;
    }

    public void saveEmployee(Employee employee) throws SQLException {

        String sql = "INSERT INTO employees (first_name, last_name, email, phone_number, hire_date, job_id, salary, commission_pct, manager_id, department_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = this.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            setEmployeeParams(statement, employee);
            statement.executeUpdate();
        }
    }

    public void updateEmployee(Employee employee) throws SQLException {

        String sql = "UPDATE employees SET first_name = ?, last_name = ?, email = ?, phone_number = ?, "
                + "hire_date = ?, job_id = ?, salary = ?, commission_pct = ?, "
                + "manager_id = ?, department_id = ? WHERE employee_id = ?";

        try (Connection conn = this.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            setEmployeeParams(statement, employee);
            statement.setInt(11, employee.getEmployeeId());
            statement.executeUpdate();
        }
    }

    public void deleteEmployee(int employeeId) throws SQLException {
        String sql = "DELETE FROM employees WHERE employee_id = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, employeeId);
            statement.executeUpdate();
        }
    }

    public ArrayList<Employee> findEmployeeByName(String name) {

        ArrayList<Employee> employees = new ArrayList<>();

        String sql = "SELECT * FROM employees e \n"
                + "left join jobs j ON (j.job_id = e.job_id) \n"
                + "left join departments d ON (d.department_id = e.department_id)\n"
                + "left  join employees m ON (e.manager_id = m.employee_id)\n"
                + "WHERE e.first_name = ? OR e.last_name = ?";

        try (Connection conn = this.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, name);
            statement.setString(2, name);

            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    Employee employee = new Employee();
                    fetchEmployeeData(employee, rs);

                    employees.add(employee);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return employees;
    }

    private void setEmployeeParams(PreparedStatement statement, Employee employee) throws SQLException {
        statement.setString(1, employee.getFirstName());
        statement.setString(2, employee.getLastName());
        statement.setString(3, employee.getEmail());
        statement.setString(4, employee.getPhoneNumber());
        statement.setString(5, employee.getHireDate());
        statement.setString(6, employee.getJob().getJobId());
        statement.setBigDecimal(7, employee.getSalary());
        if (employee.getCommissionPct() == null) {
            statement.setNull(8, Types.DECIMAL);
        } else {
            statement.setBigDecimal(8, employee.getCommissionPct());
        }
        if (employee.getManager() == null) {
            statement.setNull(9, Types.INTEGER);
        } else {
            statement.setInt(9, employee.getManager().getEmployeeId());
        }
        statement.setInt(10, employee.getDepartment().getDepartmentId());
    }


    private void fetchEmployeeData(Employee employee, ResultSet rs) throws SQLException {
        employee.setEmployeeId(rs.getInt(1));
        employee.setFirstName(rs.getString(2));
        employee.setLastName(rs.getString(3));
        employee.setEmail(rs.getString(4));
        employee.setPhoneNumber(rs.getString(5));
        employee.setHireDate(rs.getString(6));

        Job job = new Job();
        job.setJobId(rs.getString(7));
        job.setJobTitle(rs.getString("job_title"));
        employee.setJob(job);

        employee.setSalary(rs.getBigDecimal(8));
        employee.setCommissionPct(rs.getBigDecimal(9));

        Employee manager = new Employee();
        manager.setEmployeeId(rs.getInt("e.manager_id"));
        manager.setFirstName(rs.getString("m.first_name"));
        manager.setLastName(rs.getString("m.last_name"));
        employee.setManager(manager);

        Department department = new Department();
        department.setDepartmentId(rs.getInt(11));
        department.setDepartmentName(rs.getString("d.department_name"));
        employee.setDepartment(department);
    }

    public EmployeeDto validateUsernameAndPassword(String username, String password) {

        EmployeeDto employee = null;

        String sql = "SELECT * FROM employees e \n" +
                "inner join employees_credentials ec on ec.employee_id = e.employee_id \n" +
                "where ec.email = ? and ec.password_hashed = sha2(?,256)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, password);

            try (ResultSet rs = statement.executeQuery()) {

                if (rs.next()) {
                    employee = getEmployeeDto(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return employee;
    }

    public EmployeeDto getEmployeeDto(int employeeId) {
        EmployeeDto employee = null;

        String sql = "SELECT * FROM employees e \n"
                + "left join jobs j ON (j.job_id = e.job_id) \n"
                + "left join departments d ON (d.department_id = e.department_id)\n"
                + "left  join employees m ON (e.manager_id = m.employee_id)\n"
                + "left join employees_credentials ec on ec.employee_id = e.employee_id \n"
                + "WHERE e.employee_id = ?";

        try (Connection conn = this.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setInt(1, employeeId);

            try (ResultSet rs = statement.executeQuery()) {

                if (rs.next()) {
                    employee = new EmployeeDto();
                    fetchEmployeeData(employee, rs);
                    employee.setRole(rs.getString("ec.role"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return employee;
    }

}