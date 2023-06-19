<%@page import="java.util.List"%>
<%@page
	import="org.foi.nwtis.pmatisic.projekt.servis.WsKorisnici.endpoint.Korisnik"%>
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
<title>Pogled 5.2.3</title>
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
		<h1>Pregled korisnika</h1>
		<h3>Informacije s WebSocket-a</h3>
		<div id="websocketInfo">
		    <p id="vrijeme"></p>
		    <p id="ukupnoKorisnika"></p>
		    <p id="ukupnoAerodroma"></p>
		</div>
		<table id="korisniciTable" class="table table-striped">
			<thead>
				<tr>
					<th>Korisničko ime</th>
					<th>Ime</th>
					<th>Prezime</th>
				</tr>
			</thead>
			<tbody>
				<%
				List<Korisnik> filtriraniKorisnici = (List<Korisnik>) request.getAttribute("filtriraniKorisnici");
				if (filtriraniKorisnici != null && !filtriraniKorisnici.isEmpty()) {
					for (Korisnik korisnik : filtriraniKorisnici) {
				%>
				<tr>
					<td><%=korisnik.getKorime()%></td>
					<td><%=korisnik.getIme()%></td>
					<td><%=korisnik.getPrezime()%></td>
				</tr>
				<%
				}
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
		<br>
		<div class="mb-3">
			<h3>Filtriraj korisnike</h3>
			<div class="input-group">
				<input type="text" class="form-control" id="traziImeKorisnika"
					placeholder="Unesite ime korisnika"> <input type="text"
					class="form-control" id="traziPrezimeKorisnika"
					placeholder="Unesite prezime korisnika">
				<button type="button" class="btn btn-primary"
					onclick="filtrirajKorisnike()">Filtriraj</button>
			</div>
		</div>
		<div class="d-flex justify-content-between mb-3">
			<a href="<%=request.getContextPath()%>/mvc/korisnici"
				class="btn btn-secondary">Povratak</a>
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

		function filtrirajKorisnike() {
		    var traziImeKorisnika = document.getElementById("traziImeKorisnika").value;
		    var traziPrezimeKorisnika = document.getElementById("traziPrezimeKorisnika").value;
		    var contextPath = '<%=request.getContextPath()%>';
		    var url = contextPath + "/mvc/korisnici/pregled?traziImeKorisnika=" + traziImeKorisnika + "&traziPrezimeKorisnika=" + traziPrezimeKorisnika;
		    
		    window.location.href = url;
		}
	</script>
</body>
</html>