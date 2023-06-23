package pe.edu.pucp.tel131lab9.controller;


import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import pe.edu.pucp.tel131lab9.bean.Employee;
import pe.edu.pucp.tel131lab9.dao.EmployeeDao;
import pe.edu.pucp.tel131lab9.dto.EmployeeDto;

import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private static final String SESSION_NAME = "userSession";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String action = req.getParameter("action") != null ? req.getParameter("action") : "login";

        if (action.equals("login")) {

            HttpSession session = req.getSession();

            if(session != null && session.getAttribute(SESSION_NAME) != null){

                Employee employee = (Employee) session.getAttribute(SESSION_NAME);

                if(employee.getEmployeeId()>0){
                    resp.sendRedirect(req.getContextPath() + "/EmployeeServlet");
                }else{
                    RequestDispatcher dispatcher = req.getRequestDispatcher("loginPage.jsp");
                    dispatcher.forward(req, resp);
                }
            }
        }else{
            req.getSession().invalidate();
            resp.sendRedirect(req.getContextPath());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("inputEmail");
        String pass = req.getParameter("inputPassword");

        EmployeeDao employeeDao = new EmployeeDao();

        EmployeeDto employee = employeeDao.validateUsernameAndPassword(email, pass);


        if (employee != null) {
            HttpSession session = req.getSession();
            session.setAttribute(SESSION_NAME, employee);

            session.setMaxInactiveInterval(300);

            switch (Optional.ofNullable(employee.getRole()).orElse("")) {
                case "ADMIN":
                    resp.sendRedirect(req.getContextPath() + "/EmployeeServlet");
                    break;
                default:
                    resp.sendRedirect(req.getContextPath() + "/JobServlet");
            }
        } else {
            req.setAttribute("error", "Wrong credentials");
            req.getRequestDispatcher("loginPage.jsp").forward(req, resp);
        }
    }
}