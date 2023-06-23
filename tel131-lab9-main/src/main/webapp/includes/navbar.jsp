<% String currentPage = request.getParameter("currentPage"); %>
<jsp:useBean id="userSession" scope="session" type="pe.edu.pucp.tel131lab9.dto.EmployeeDto"
             class="pe.edu.pucp.tel131lab9.dto.EmployeeDto"/>

<nav class="navbar navbar-expand-md navbar-light bg-light">
    <div class="container-fluid">
        <a class="navbar-brand" href="<%=request.getContextPath()%>">HR Management</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent"
                aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse justify-content-end" id="navbarSupportedContent">
            <ul class="navbar-nav">
                <li class="nav-item">
                    <a class="nav-link <%=currentPage.equals("home") ? "active" : ""%>"
                       href="<%=request.getContextPath()%>/HomeServlet">
                        Home
                    </a>
                </li>
                <% if (userSession.getEmployeeId() > 0 && userSession.getRole()!=null && userSession.getRole().equals("ADMIN")) { //esto logueado %>
                <li class="nav-item">
                    <a class="nav-link <%=currentPage.equals("emp") ? "active" : ""%>"
                       href="<%=request.getContextPath()%>/EmployeeServlet">
                        Employees
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link <%=currentPage.equals("job") ? "active" : ""%>"
                       href="<%=request.getContextPath()%>/JobServlet">
                        Jobs
                    </a>
                </li>
                <% } %>
                <div class="form-inline font-italic my-2 my-lg-0">
                    <% if (userSession.getEmployeeId() > 0) { //esto logueado %>
                    <span><%=userSession.getFirstName() + " " + userSession.getLastName()%></span><a
                        href="<%=request.getContextPath()%>/login?action=logout">(Log out)</a>
                    <% } else { //no estoy loggedIn %>
                    <a class="nav-link" style="color: #007bff;" href="<%=request.getContextPath()%>/login">
                        (Log in)
                    </a>
                    <% } %>
                </div>
            </ul>
        </div>
    </div>
</nav>
