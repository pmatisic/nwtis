<%@page import="java.util.List"%>
<%@page import="org.foi.nwtis.rest.podaci.LetAvionaID"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="author" content="<%=request.getAttribute("ime")%> <%=request.getAttribute("prezime")%>">
<meta name="subject" content="<%=request.getAttribute("predmet")%>">
<meta name="year" content="<%=request.getAttribute("godina")%>">
<meta name="version" content="<%=request.getAttribute("verzija")%>">
<title>Pregled letova</title>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.0/dist/css/bootstrap.min.css">
<style>
body {
	background-color: #f8f9fa;
}

.container {
	max-width: 1500px;
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
<script>
function obrisiLet(letId) {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 204) {
            alert("Let je uspješno obrisan.");
            location.reload();
        } else if (this.readyState == 4) {
            alert("Došlo je do pogreške prilikom brisanja leta.");
        }
    };
    xhttp.open("DELETE", "<%=request.getContextPath()%>/mvc/letovi/" + letId, true);
    xhttp.send();
}
</script>
</head>
<body>
    <div class="container">
        <h1>Pregled spremljenih letova</h1>
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
        <table id="letoviTable" class="table table-striped">
            <thead>
                <tr>
                    <th>Obriši let</th>
                    <th>ID</th>
					<th>ICAO24</th>
					<th>Prvi put viđen</th>
					<th>Aerodrom polaska</th>
					<th>Zadnji put viđen</th>
					<th>Aerodrom dolaska</th>
					<th>Pozivni znak</th>
					<th>Horizontalna udaljenost aerodroma polaska</th>
					<th>Vertikalna udaljenost aerodroma polaska</th>
					<th>Horizontalna udaljenost aerodroma dolaska</th>
					<th>Vertikalna udaljenost aerodroma dolaska</th>
					<th>Broj kandidata za aerodrom polaska</th>
					<th>Broj kandidata za aerodrom dolaska</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<LetAvionaID> letovi = (List<LetAvionaID>) request.getAttribute("spremljeniLetovi");

                if (letovi != null) {
                  for (LetAvionaID let : letovi) {
                %>
                <tr>
                    <td>
                        <button type="button" class="btn link-styled" onclick="obrisiLet('<%=let.getId()%>')">Obriši</button>
                    </td>
                    <td><%=let.getId()%></td>
					<td><%=let.getIcao24()%></td>
					<td><%=let.getFirstSeen()%></td>
					<td><%=let.getEstDepartureAirport()%></td>
					<td><%=let.getLastSeen()%></td>
					<td><%=let.getEstArrivalAirport()%></td>
					<td><%=let.getCallsign()%></td>
					<td><%=let.getEstDepartureAirportHorizDistance()%></td>
					<td><%=let.getEstDepartureAirportVertDistance()%></td>
					<td><%=let.getEstArrivalAirportHorizDistance()%></td>
					<td><%=let.getEstArrivalAirportVertDistance()%></td>
					<td><%=let.getDepartureAirportCandidatesCount()%></td>
					<td><%=let.getArrivalAirportCandidatesCount()%></td>
                </tr>
                <%
                }
                } else {
                %>
                <tr>
                    <td colspan="13" class="text-center">Nema podataka za prikaz</td>
                </tr>
                <%
                }
                %>
            </tbody>
        </table>
    </div>
</body>
</html>