<%@page import="java.util.ArrayList" %>
<%@ page import="pe.edu.pucp.tel131lab9.bean.Post" %>
<%@ page import="pe.edu.pucp.tel131lab9.bean.Comment" %>
<jsp:useBean id="post" type="pe.edu.pucp.tel131lab9.bean.Post" scope="request"/>
<jsp:useBean id="comments" type="java.util.ArrayList<pe.edu.pucp.tel131lab9.bean.Comment>" scope="request"/>
<jsp:useBean id="comment" scope="request" type="java.lang.String" class="java.lang.String"/>

<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>View Post</title>
    <jsp:include page="../includes/headCss.jsp"></jsp:include>
</head>
<body>
<div class='container'>
    <jsp:include page="../includes/navbar.jsp">
        <jsp:param name="currentPage" value="viewPost"/>
    </jsp:include>
    <div class="row mb-5 mt-4">
        <div class="col-md-7">
        </div>
    </div>
    <div class="card">
        <div class="card-body">
            <h5 class="card-title h1"><%= post.getTitle()%></h5>
            <p class="card-text"><%= post.getContent()%></p>
        </div>
        <ul class="list-group list-group-flush">
            <%for (Comment c : comments) {%>
            <li class="list-group-item"><%=c.getComment() + " - " + c.getDatetime().toString()%></li>
            <%}%>
        </ul>
        <div class="card-body">
            <form method="post" action="<%=request.getContextPath()%>/PostServlet?action=comment">
                <div class="input-group mb-3">
                    <input type="text" class="form-control" placeholder="Write a comment" name="comment"
                           value="<%=comment%>"/>
                    <button class="input-group-text" type="submit">
                        <i class="bi bi-chat"></i>
                    </button>
                </div>
            </form>
        </div>
    </div>
    <jsp:include page="../includes/footer.jsp"/>
</div>
</body>
</html>
