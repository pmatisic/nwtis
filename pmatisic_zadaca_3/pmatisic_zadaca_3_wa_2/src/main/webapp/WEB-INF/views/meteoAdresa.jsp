<%@page
	import="org.foi.nwtis.pmatisic.zadaca_3.ws.WsMeteo.endpoint.MeteoPodaci"%>
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
</style>
</head>
<body>
	<div class="container">
		<h1>Pregled meteoroloških podataka za upisanu adresu</h1>
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
		<table id="meteoTable" class="table table-striped">
			<thead>
				<tr>
					<th>Temperatura</th>
					<th>Vlažnost zraka</th>
					<th>Tlak zraka</th>
					<th>Stanje neba</th>
					<th>Postotak oblaka</th>
					<th>Zalazak sunca</th>
					<th>Izlazak sunca</th>
					<th>Vrijeme zadnjeg ažuriranja</th>
				</tr>
			</thead>
			<tbody>
				<%
				MeteoPodaci meteoPodaci = (MeteoPodaci) request.getAttribute("meteoAdresa");

				if (meteoPodaci != null) {
				%>
				<tr>
					<td><%=meteoPodaci.getTemperatureValue()%> <%=meteoPodaci.getTemperatureUnit()%></td>
					<td><%=meteoPodaci.getHumidityValue()%> <%=meteoPodaci.getHumidityUnit()%></td>
					<td><%=meteoPodaci.getPressureValue()%> <%=meteoPodaci.getPressureUnit()%></td>
					<td><%=meteoPodaci.getCloudsName()%></td>
					<td><%=meteoPodaci.getCloudsValue()%>%</td>
					<td><%=meteoPodaci.getSunSet()%></td>
					<td><%=meteoPodaci.getSunRise()%></td>
					<td><%=meteoPodaci.getLastUpdate()%></td>
				</tr>
				<%
				}
				%>
			</tbody>
		</table>
		<div class="d-flex justify-content-center mb-3">
			<a href="<%=request.getContextPath()%>/mvc/aerodromi"
				class="btn btn-primary me-3">Povratak na popis aerodroma</a>
		</div>
		<br>
	</div>
</body>
</html>