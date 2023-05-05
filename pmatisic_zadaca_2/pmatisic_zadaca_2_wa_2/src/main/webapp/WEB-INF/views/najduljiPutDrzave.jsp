<%@page import="java.util.List"%>
<%@page import="org.foi.nwtis.podaci.UdaljenostAerodromDrzava"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Pregled najdužeg puta unutar države s pregledom aerodroma
	od odabranog aerodroma</title>
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
		<h1>Pregled najdužeg puta unutar države s pregledom aerodroma od
			odabranog aerodroma</h1>
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
		<table id="najduljiPutTable" class="table table-striped">
			<thead>
				<tr>
					<th>ICAO</th>
					<th>Država</th>
					<th>Udaljenost (km)</th>
				</tr>
			</thead>
			<tbody>
				<%
				UdaljenostAerodromDrzava najduljiPut =
				    (UdaljenostAerodromDrzava) request.getAttribute("najduljiPut");
				if (najduljiPut != null) {
				%>
				<tr>
					<td><%=najduljiPut.icao()%></td>
					<td><%=najduljiPut.drzava()%></td>
					<td><%=najduljiPut.km()%></td>
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
	</div>
	<script
		src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.3/dist/umd/popper.min.js"></script>
</body>
</html>