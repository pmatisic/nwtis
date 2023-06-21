<%@page import="java.util.List"%>
<%@page import="org.foi.nwtis.pmatisic.projekt.podatak.Udaljenost"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="author" content="<%=request.getAttribute("ime")%> <%=request.getAttribute("prezime")%>">
<meta name="subject" content="<%=request.getAttribute("predmet")%>">
<meta name="year" content="<%=request.getAttribute("godina")%>">
<meta name="version" content="<%=request.getAttribute("verzija")%>">
<title>Pogled 5.5.5</title>
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.0/dist/css/bootstrap.min.css">
<style>
body {
	background-color: #f8f9fa;
}

.container {
	max-width: 960px;
}

h1 {
	color: #343a40;
	font-weight: bold;
	margin-top: 1.5rem;
}

th {
	color: #495057;
}

thead {
	background-color: #e9ecef;
}

.btn-primary {
	background-color: #007bff;
	border-color: #007bff; &: hover { background-color : #0056b3;
	border-color: #0056b3;
}

}
.btn-secondary {
	background-color: #6c757d;
	border-color: #6c757d; &: hover { background-color : #5a6268;
	border-color: #5a6268;
}

}
.author-info {
	font-size: 0.9rem;
	margin-top: 1rem;
	margin-bottom: 1rem;
}
</style>
</head>
<body>
    <div class="container">
        <h1>Pregled udaljenosti između dva aerodroma</h1>
        <br>
        <table id="udaljenost" class="table table-striped">
            <thead>
                <tr>
                    <th>Ishodišni aerodrom</th>
                    <th>Odredišni aerodrom</th>
                    <th>Udaljenost (km)</th>
                </tr>
            </thead>
            <tbody>
                <%
                String icaoOd = (String) request.getAttribute("icaoOd");
                String icaoDo = (String) request.getAttribute("icaoDo");
                String udaljenost = (String) request.getAttribute("udaljenost");
                
                if (udaljenost != null && !udaljenost.isEmpty()) {
                %>
                    <tr>
                        <td><%=icaoOd%></td>
                        <td><%=icaoDo%></td>
                        <td><%=udaljenost%></td>
                    </tr>
                <% 
                } else {
                %>
                    <tr>
                        <td colspan="3" class="text-center">Nema podataka za prikaz</td>
                    </tr>
                <%
                }
                %>
            </tbody>
        </table>
        <div class="d-flex align-items-center mb-3">
            <a href="<%=request.getContextPath()%>/mvc/aerodromi" class="btn btn-secondary">Povratak</a>
        </div>
    </div>
</body>
</html>