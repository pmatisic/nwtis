<%@page import="java.util.List"%>
<%@page import="org.foi.nwtis.pmatisic.projekt.servis.WsAerodromi.endpoint.AerodromSaStatusom"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="author" content="<%=request.getAttribute("ime")%> <%=request.getAttribute("prezime")%>">
<meta name="subject" content="<%=request.getAttribute("predmet")%>">
<meta name="year" content="<%=request.getAttribute("godina")%>">
<meta name="version" content="<%=request.getAttribute("verzija")%>">
<title>Pogled 5.5.3</title>
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
		<h1>Pregled aerodroma za koje se preuzimaju podaci o polascima</h1>
		<h3>Informacije s WebSocket-a</h3>
		<div id="websocketInfo">
		    <p id="vrijeme"></p>
		    <p id="ukupnoKorisnika"></p>
		    <p id="ukupnoAerodroma"></p>
		</div>
		<table id="aerodromiTable" class="table table-striped">
			<thead>
				<tr>
					<th>ICAO</th>
					<th>Naziv</th>
					<th>Država</th>
					<th>Koordinate</th>
					<th>Status</th>
					<th>Promjena statusa</th>
				</tr>
			</thead>
			<tbody>
				<%
				List<AerodromSaStatusom> aerodromi = (List<AerodromSaStatusom>) request.getAttribute("podatci");

				if (aerodromi != null || !aerodromi.isEmpty()) {
					for (AerodromSaStatusom aerodrom : aerodromi) {
					%>
					<tr>
						<td><a
							href="<%=request.getContextPath()%>/mvc/aerodromi/<%=aerodrom.getIcao()%>"
							class="link-styled"> <%=aerodrom.getIcao()%>
						</a></td>
						<td><%=aerodrom.getNaziv()%></td>
						<td><%=aerodrom.getDrzava()%></td>
						<td><%=aerodrom.getLokacija().getLatitude() + ", " + aerodrom.getLokacija().getLongitude()%></td>
						<td><%=aerodrom.isPreuzimanjeAktivno()%></td>
						<td>
						    <% if(aerodrom.isPreuzimanjeAktivno()) { %>
						        <a href="<%=request.getContextPath()%>/mvc/aerodromi/polasci?icao=<%=aerodrom.getIcao()%>&action=deactivate" class="link-styled">Deaktiviraj</a>
						    <% } else { %>
						        <a href="<%=request.getContextPath()%>/mvc/aerodromi/polasci?icao=<%=aerodrom.getIcao()%>&action=activate" class="link-styled">Aktiviraj</a>
						    <% } %>
						</td>
					</tr>
					<% 
					}
				} else {
				%>
				<tr>
					<td colspan="6" class="text-center">Nema podataka za prikaz</td>
				</tr>
				<%
				}
				%>
			</tbody>
		</table>
		<div class="d-flex align-items-center mb-3">
			<a href="<%=request.getContextPath()%>/mvc/aerodromi"
				class="btn btn-secondary me-3">Povratak</a>
		</div>
		<br>
	</div>
    <script>    
		var socket = new WebSocket('ws://localhost:8080/pmatisic_aplikacija_4/info');
		socket.onopen = function() {
		    console.log('WebSocket veza uspostavljena.');
		    socket.send('dajMeteo');
		};
		socket.onmessage = function(event) {
		    var receivedData = JSON.parse(event.data);
		    console.log(receivedData);
	
		    var vrijemeElement = document.getElementById('vrijeme');
		    var ukupnoKorisnikaElement = document.getElementById('ukupnoKorisnika');
		    var ukupnoAerodromaElement = document.getElementById('ukupnoAerodroma');
		    
		    if (vrijemeElement && ukupnoKorisnikaElement && ukupnoAerodromaElement) {
		        vrijemeElement.innerText = "Vrijeme: " + receivedData.vrijeme;
		        ukupnoKorisnikaElement.innerText = "Ukupno korisnika: " + receivedData.ukupnoKorisnika;
		        ukupnoAerodromaElement.innerText = "Ukupno aerodroma: " + receivedData.ukupnoAerodroma;
		    }
		};
		socket.onclose = function() {
		    console.log('WebSocket veza zatvorena.');
		};
    </script>
</body>
</html>