<%@page import="java.util.List"%>
<%@page import="org.foi.nwtis.podaci.Udaljenost"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Pregled aerodroma</title>
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.0/dist/css/bootstrap.min.css">
<style>
/* SCSS stilovi */
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
		<h1>Pregled udaljenosti po državama između dva aerodroma i ukupne
			udaljenosti</h1>
		<div class="author-info">
			<p>
				<strong>Autor:</strong>
				<%=request.getAttribute("ime")%>
				<%=request.getAttribute("prezime")%></p>
			<p>
				<strong>Predmet:</strong>
				<%=request.getAttribute("predmet")%></p>
			<p>
				<strong>Godina:</strong>
				<%=request.getAttribute("godina")%></p>
			<p>
				<strong>Verzija aplikacije:</strong>
				<%=request.getAttribute("verzija")%></p>
		</div>
		<div class="d-flex justify-content-between mb-3">
			<a href="<%=request.getContextPath()%>/index.jsp"
				class="btn btn-secondary">Početna stranica</a>
		</div>
		<table id="udaljenostiAerodromaTable" class="table table-striped">
			<thead>
				<tr>
					<th>Država</th>
					<th>Udaljenost (km)</th>
				</tr>
			</thead>
			<tbody>
				<%
				List<Udaljenost> udaljenosti = (List<Udaljenost>) request.getAttribute("udaljenostiAerodroma");
				double ukupnaUdaljenost = 0;
				if (udaljenosti != null) {
				  for (Udaljenost udaljenost : udaljenosti) {
				    ukupnaUdaljenost += udaljenost.km();
				%>

				<tr>
					<td><%=udaljenost.drzava()%></td>
					<td><%=udaljenost.km()%></td>
				</tr>
				<%
				}
				}
				%>
				<tr>
					<td><strong>Ukupna udaljenost:</strong></td>
					<td><strong><%=ukupnaUdaljenost%> km</strong></td>
				</tr>
			</tbody>
		</table>
		<div class="d-flex justify-content-between mb-3">
			<a href="<%=request.getContextPath()%>/mvc/aerodromi"
				class="btn btn-primary">Povratak na popis aerodroma</a>
		</div>
		<br>
	</div>
	<script
		src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.3/dist/umd/popper.min.js"></script>
</body>
</html>