<%@page import="java.util.List"%>
<%@page import="org.foi.nwtis.podaci.Aerodrom"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="author" content="<%=request.getAttribute("ime")%> <%=request.getAttribute("prezime")%>">
<meta name="subject" content="<%=request.getAttribute("predmet")%>">
<meta name="year" content="<%=request.getAttribute("godina")%>">
<meta name="version" content="<%=request.getAttribute("verzija")%>">
<title>Pogled 5.5.1</title>
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

.link-styled {
	color: #007bff;
	text-decoration: none; &: hover { color : #0056b3;
	text-decoration: underline;
}
}
</style>
</head>
<body>
	<div class="container">
		<h1>Pregled svih aerodroma</h1>
		<h3>Informacije s WebSocket-a</h3>
		<div id="websocketInfo">
		    <p id="vrijeme"></p>
		    <p id="ukupnoKorisnika"></p>
		    <p id="ukupnoAerodroma"></p>
		</div>
		<div class="d-flex align-items-center mb-3">
		    <a href="<%=request.getContextPath()%>/mvc/aerodromi" class="btn btn-secondary me-3">Povratak</a>
		    <div class="d-flex align-items-center me-3">
		        <input type="text" id="traziNaziv" placeholder="Filtriraj po nazivu aerodroma" class="form-control me-2">
		        <input type="text" id="traziDrzavu" placeholder="Filtriraj po državi" class="form-control me-2">
		        <button onclick="submitFilter()" class="btn btn-primary">Filtriraj</button>
		    </div>
		</div>
		<table id="aerodromiTable" class="table table-striped">
			<thead>
				<tr>
					<th>ICAO</th>
					<th>Naziv</th>
					<th>Država</th>
					<th>Koordinate</th>
					<th>Preuzimanje</th>
				</tr>
			</thead>
			<tbody>
				<%
				List<Aerodrom> aerodromi = (List<Aerodrom>) request.getAttribute("aerodromi");
				Integer odBroja = (Integer) request.getAttribute("odBroja");
				Integer broj = (Integer) request.getAttribute("broj");
				boolean dodan = (boolean) request.getAttribute("dodan");
				
				if (aerodromi != null || !aerodromi.isEmpty()) {
					for (Aerodrom aerodrom : aerodromi) {
					%>
					<tr>
						<td><a
							href="<%=request.getContextPath()%>/mvc/aerodromi/<%=aerodrom.getIcao()%>"
							class="link-styled"> <%=aerodrom.getIcao()%>
						</a></td>
						<td><%=aerodrom.getNaziv()%></td>
						<td><%=aerodrom.getDrzava()%></td>
						<td><%=aerodrom.getLokacija().getLatitude() + ", " + aerodrom.getLokacija().getLongitude()%></td>
						<td><a href="<%=request.getContextPath()%>/mvc/aerodromi/svi?icao=<%=aerodrom.getIcao()%>" class="link-styled">Dodaj</a></td>
					</tr>
					<%
					}
				} else {
				%>
				<tr>
					<td colspan="5" class="text-center">Nema podataka za prikaz</td>
				</tr>
				<%
				}
				%>
			</tbody>
		</table>
		<div class="pagination-container">
			<div class="pagination-btns">
				<a href="<%=request.getContextPath()%>/mvc/aerodromi/svi"
					class="btn btn-primary">Početak</a> 
				<%
				String traziNaziv = request.getParameter("traziNaziv") != null ? request.getParameter("traziNaziv") : "";
				String traziDrzavu = request.getParameter("traziDrzavu") != null ? request.getParameter("traziDrzavu") : "";
				%>			
				<a href="<%=request.getContextPath()%>/mvc/aerodromi/svi?odBroja=<%=odBroja <= 1 ? 1 : odBroja - 1%>&traziNaziv=<%=traziNaziv%>&traziDrzavu=<%=traziDrzavu%>"
				    class="btn btn-primary <%=odBroja <= 1 ? "disabled" : ""%>">Prethodna stranica</a>
				
				<a href="<%=request.getContextPath()%>/mvc/aerodromi/svi?odBroja=<%=odBroja + 1%>&traziNaziv=<%=traziNaziv%>&traziDrzavu=<%=traziDrzavu%>"
				    class="btn btn-primary">Sljedeća stranica</a>
			</div>
		</div>
		<br>
	</div>
    <script>
        function submitFilter() {
            var traziNaziv = document.getElementById("traziNaziv").value;
            var traziDrzavu = document.getElementById("traziDrzavu").value;
            var contextPath = '<%=request.getContextPath()%>';
            var url = contextPath + "/mvc/aerodromi/svi?traziNaziv=" + traziNaziv + "&traziDrzavu=" + traziDrzavu;
            window.location.href = url;
        }
        
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