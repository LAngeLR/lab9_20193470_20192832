package pe.edu.pucp.tel131lab9.controller.admin;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.pucp.tel131lab9.bean.Department;
import pe.edu.pucp.tel131lab9.bean.Employee;
import pe.edu.pucp.tel131lab9.bean.Job;
import pe.edu.pucp.tel131lab9.dao.DepartmentDao;
import pe.edu.pucp.tel131lab9.dao.EmployeeDao;
import pe.edu.pucp.tel131lab9.dao.JobDao;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet(name = "EmployeeServlet", urlPatterns = {"/EmployeeServlet"})
public class EmployeeServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action") == null ? "lista" : request.getParameter("action");

        RequestDispatcher view;
        EmployeeDao employeeDao = new EmployeeDao();
        JobDao jobDao = new JobDao();
        DepartmentDao departmentDao = new DepartmentDao();

        switch (action) {
            case "lista":
                request.setAttribute("listaEmpleados", employeeDao.listEmployee());
                view = request.getRequestDispatcher("employees/listEmployees.jsp");
                view.forward(request, response);
                break;
            case "agregar":
                request.setAttribute("listaTrabajos", jobDao.listJobs());
                request.setAttribute("listaDepartamentos", departmentDao.listDepartments());
                request.setAttribute("listaJefes", employeeDao.listEmployee());

                view = request.getRequestDispatcher("employees/formularioNuevo.jsp");
                view.forward(request, response);
                break;
            case "editar":
                if (request.getParameter("id") != null) {
                    String employeeIdString = request.getParameter("id");
                    int employeeId = 0;
                    try {
                        employeeId = Integer.parseInt(employeeIdString);
                    } catch (NumberFormatException ex) {
                        response.sendRedirect("EmployeeServlet");
                    }

                    Employee emp = employeeDao.getEmployee(employeeId);

                    if (emp != null) {
                        request.setAttribute("empleado", emp);
                        request.setAttribute("listaTrabajos", jobDao.listJobs());
                        request.setAttribute("listaDepartamentos", departmentDao.listDepartments());
                        request.setAttribute("listaJefes", employeeDao.listEmployee());
                        view = request.getRequestDispatcher("employees/formularioEditar.jsp");
                        view.forward(request, response);
                    } else {
                        response.sendRedirect("EmployeeServlet");
                    }

                } else {
                    response.sendRedirect("EmployeeServlet");
                }

                break;
            case "borrar":
                if (request.getParameter("id") != null) {
                    String employeeIdString = request.getParameter("id");
                    int employeeId = 0;
                    try {
                        employeeId = Integer.parseInt(employeeIdString);
                    } catch (NumberFormatException ex) {
                        response.sendRedirect("EmployeeServlet?err=Error al borrar el empleado");
                    }

                    Employee emp = employeeDao.getEmployee(employeeId);

                    if (emp != null) {
                        try {
                            employeeDao.deleteEmployee(employeeId);
                            response.sendRedirect("EmployeeServlet?msg=Empleado borrado exitosamente");
                        } catch (SQLException e) {
                            response.sendRedirect("EmployeeServlet?err=Error al borrar el empleado");
                        }
                    }
                } else {
                    response.sendRedirect("EmployeeServlet?err=Error al borrar el empleado");
                }
                break;
            default:
                response.sendRedirect("EmployeeServlet");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String action = request.getParameter("action") == null ? "guardar" : request.getParameter("action");
        EmployeeDao employeeDao = new EmployeeDao();

        switch (action) {
            case "guardar":
                Employee e = new Employee();
                e.setFirstName(request.getParameter("first_name"));
                e.setLastName(request.getParameter("last_name"));
                e.setEmail(request.getParameter("email"));
                e.setPhoneNumber(request.getParameter("phone"));
                e.setHireDate(request.getParameter("hire_date"));
                e.setSalary(new BigDecimal(request.getParameter("salary")));
                e.setCommissionPct(request.getParameter("commission").equals("") ? null : new BigDecimal(request.getParameter("commission")));

                String jobId = request.getParameter("job_id");
                Job job = new Job(jobId);
                e.setJob(job);

                String managerId = request.getParameter("manager_id");
                if (!managerId.equals("sin-jefe")) {
                    Employee manager = new Employee(Integer.parseInt(managerId));
                    e.setManager(manager);
                }

                String departmentId = request.getParameter("department_id");
                Department department = new Department(Integer.parseInt(departmentId));
                e.setDepartment(department);


                if (request.getParameter("employee_id") == null) {
                    try {
                        employeeDao.saveEmployee(e);
                        response.sendRedirect("EmployeeServlet?msg=Empleado creado exitosamente");
                    } catch (SQLException ex) {
                        response.sendRedirect("EmployeeServlet?err=Error al crear empleado");
                    }
                } else {
                    e.setEmployeeId(Integer.parseInt(request.getParameter("employee_id")));
                    try {
                        employeeDao.updateEmployee(e);
                        response.sendRedirect("EmployeeServlet?msg=Empleado actualizado exitosamente");
                    } catch (SQLException ex) {
                        response.sendRedirect("EmployeeServlet?err=Error al actualizar empleado");
                    }
                }
                break;
            case "buscar":
                String textoBuscar = request.getParameter("textoBuscar");
                if (textoBuscar == null) {
                    response.sendRedirect("EmployeeServlet");
                } else {
                    request.setAttribute("textoBusqueda", textoBuscar);
                    request.setAttribute("listaEmpleados", employeeDao.findEmployeeByName(textoBuscar));
                    RequestDispatcher view = request.getRequestDispatcher("employees/listEmployees.jsp");
                    view.forward(request, response);
                }
                break;
        }
    }

}