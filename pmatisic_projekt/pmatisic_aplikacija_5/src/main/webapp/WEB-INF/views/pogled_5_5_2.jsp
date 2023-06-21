<%@page import="org.foi.nwtis.podaci.Aerodrom"%>
<%@page
	import="org.foi.nwtis.pmatisic.projekt.servis.WsMeteo.endpoint.MeteoPodaci"%>
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
<title>Pogled 5.5.2</title>
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.0/dist/css/bootstrap.min.css">
<style>
body {
	background-color: #f8f9fa;
}

.container {
	max-width: 1200px;
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
	border-color: #007bff;
}

.btn-secondary {
	background-color: #6c757d;
	border-color: #6c757d;
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

.link-styled {
	color: #007bff;
	text-decoration: none;
}

.table-col-width-1 {
	width: 12.5%;
}

.table-col-width-2 {
	width: 25%;
}
</style>
</head>
<body>
	<div class="container">
		<h1>Pregled podataka izabranog aerodroma</h1>
		<br>
		<div class="table-responsive">
			<table id="aerodromiTable" class="table table-striped">
				<thead>
					<tr>
						<th class="table-col-width-2">ICAO</th>
						<th class="table-col-width-2">Naziv</th>
						<th class="table-col-width-2">Država</th>
						<th class="table-col-width-2">Koordinate</th>
					</tr>
				</thead>
				<tbody>
					<%
					Aerodrom aerodrom = (Aerodrom) request.getAttribute("aerodrom");
					if (aerodrom != null) {
					%>
					<tr>
						<td><%=aerodrom.getIcao()%></td>
						<td><%=aerodrom.getNaziv()%></td>
						<td><%=aerodrom.getDrzava()%></td>
						<td><%=aerodrom.getLokacija().getLatitude() + ", " + aerodrom.getLokacija().getLongitude()%></td>
					</tr>
					<%
					} else {
					%>
					<tr>
						<td colspan="4" class="text-center">Nema podataka za prikaz</td>
					</tr>
					<%
					}
					%>
				</tbody>
			</table>
		</div>
		<br>
		<h3>Meteo informacije</h3>
		<div class="table-responsive">
			<table id="meteoTable" class="table table-striped">
				<thead>
					<tr>
						<th class="table-col-width-1">Temperatura</th>
						<th class="table-col-width-1">Vlažnost zraka</th>
						<th class="table-col-width-1">Tlak zraka</th>
						<th class="table-col-width-1">Stanje neba</th>
						<th class="table-col-width-1">Postotak oblaka</th>
						<th class="table-col-width-1">Zalazak sunca</th>
						<th class="table-col-width-1">Izlazak sunca</th>
						<th class="table-col-width-1">Vrijeme zadnjeg ažuriranja</th>
					</tr>
				</thead>
				<tbody>
					<%
					MeteoPodaci meteoPodaci = (MeteoPodaci) request.getAttribute("meteo");
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
					} else {
					%>
					<tr>
						<td colspan="8" class="text-center">Nema podataka za prikaz</td>
					</tr>
					<%
					}
					%>
				</tbody>
			</table>
			<%
			String trenutniIcao = "";
			if (aerodrom != null) {
			  trenutniIcao = aerodrom.getIcao();
			}
			%>
		</div>
		<br>
		<div class="mb-3">
			<h3>Pregled udaljenosti između dva aerodroma unutar država preko kojih se leti te ukupna udaljenost</h3>
			<div class="input-group">
				<input type="text" class="form-control" id="icaoDo"
					placeholder="Unesite odredišni ICAO" required>
				<button type="button" class="btn btn-primary" onclick="submitForm()">Potvrdi</button>
			</div>
		</div>
		<div class="d-flex align-items-center mb-3">
			<a href="<%=request.getContextPath()%>/mvc/aerodromi"
				class="btn btn-secondary me-3">Povratak</a>
		</div>
		<br>
	</div>
	<script>
		function submitForm() {
		    var icaoDo = document.getElementById("icaoDo").value;
		    var currentICAO = '<%=trenutniIcao%>';
		    var contextPath = '<%=request.getContextPath()%>';
		    var url = contextPath + "/mvc/aerodromi/" + currentICAO + "/" + icaoDo;
		    window.location.href = url;
		}
	</script>
</body>
</html>