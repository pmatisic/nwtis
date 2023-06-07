<%@page import="java.util.List"%>
<%@page import="org.foi.nwtis.pmatisic.zadaca_3.ws.WsAerodromi.endpoint.UdaljenostAerodromKlasa"%>
<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="author"
	content="<%=request.getAttribute("ime")%> <%=request.getAttribute("prezime")%>">
<meta name="subject" content="<%=request.getAttribute("predmet")%>">
<meta name="year" content="<%=request.getAttribute("godina")%>">
<meta name="version" content="<%=request.getAttribute("verzija")%>">
<title>Pregled aerodroma</title>
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

.pagination-container {
	display: flex;
	justify-content: center;
	margin-top: 1rem;
}

.pagination-btns {
	display: flex;
	justify-content: center;
	gap: 0.5rem;
}
</style>
</head>
<body>
	<div class="container">
		<h1>Pregled udaljenosti svih aerodroma od odabranog aerodroma</h1>
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
				class="btn btn-secondary">Početna stranica</a> <a
				href="<%=request.getContextPath()%>/mvc/aerodromi"
				class="btn btn-secondary">Povratak na popis aerodroma</a>
		</div>
		<table id="udaljenostiAerodromaTable" class="table table-striped">
			<thead>
				<tr>
					<th>ICAO</th>
					<th>Udaljenost (km)</th>
				</tr>
			</thead>
			<tbody>
				<%
				List<UdaljenostAerodromKlasa> udaljenosti =
				    (List<UdaljenostAerodromKlasa>) request.getAttribute("udaljenosti");
				Integer odBroja = (Integer) request.getAttribute("odBroja");
				Integer broj = (Integer) request.getAttribute("broj");

				if (udaljenosti != null && !udaljenosti.isEmpty()) {
				  for (UdaljenostAerodromKlasa udaljenost : udaljenosti) {
				%>
				<tr>
					<td><%=udaljenost.getIcao()%></td>
					<td><%=udaljenost.getKm()%></td>
				</tr>
				<%
				}
				} else {
				%>
				<tr>
					<td colspan="2" class="text-center">Nema podataka za prikaz</td>
				</tr>
				<%
				}
				%>
			</tbody>
		</table>
		<div class="pagination-container">
			<div class="pagination-btns">
				<a href="<%=request.getContextPath()%>/mvc/aerodromi/<%=request.getAttribute("icao")%>/udaljenosti"
					class="btn btn-primary">Početak</a>
				<a href="<%=request.getContextPath()%>/mvc/aerodromi/<%=request.getAttribute("icao")%>/udaljenosti?odBroja=<%=odBroja <= 1 ? 1 : odBroja - 1%>"
					class="btn btn-primary <%=odBroja <= 1 ? "disabled" : ""%>">Prethodna stranica</a>
				<a href="<%=request.getContextPath()%>/mvc/aerodromi/<%=request.getAttribute("icao")%>/udaljenosti?odBroja=<%=odBroja + 1%>"
					class="btn btn-primary">Sljedeća stranica</a>
			</div>
		</div>
		<br>
	</div>
</body>
</html>