<%@page import="java.util.List"%>
<%@page import="org.foi.nwtis.rest.podaci.LetAviona"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Pregled letova</title>
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
		<h1>Pregled polazaka/letova s jednog aerodroma na određeni dan</h1>
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
					<th>ICAO24</th>
					<th>First Seen</th>
					<th>Est. Departure Airport</th>
					<th>Last Seen</th>
					<th>Est. Arrival Airport</th>
					<th>Callsign</th>
					<th>Est. Departure Airport Horiz. Distance</th>
					<th>Est. Departure Airport Vert. Distance</th>
					<th>Est. Arrival Airport Horiz. Distance</th>
					<th>Est. Arrival Airport Vert. Distance</th>
					<th>Departure Airport Candidates Count</th>
					<th>Arrival Airport Candidates Count</th>
				</tr>
			</thead>
			<tbody>
				<%
				List<LetAviona> letovi = (List<LetAviona>) request.getAttribute("letovi");
				Integer odBroja = (Integer) request.getAttribute("odBroja");
				Integer broj = (Integer) request.getAttribute("broj");

				if (letovi != null) {
				  for (LetAviona let : letovi) {
				%>
				<tr>
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
					<td colspan="12" class="text-center">Nema podataka za prikaz</td>
				</tr>
				<%
				}
				%>
			</tbody>
		</table>
		<div class="pagination-container">
			<div class="pagination-btns">
				<a
					href="<%=request.getContextPath()%>/mvc/letovi/<%=request.getAttribute("icao")%>?dan=<%=request.getAttribute("dan")%>"
					class="btn btn-primary">Početak</a> <a
					href="<%=request.getContextPath()%>/mvc/letovi/<%=request.getAttribute("icao")%>?dan=<%=request.getAttribute("dan")%>&odBroja=<%=odBroja <= 1 ? 1 : odBroja - 1%>"
					class="btn btn-primary <%=odBroja <= 1 ? "disabled" : ""%>">Prethodna
					stranica</a> <a
					href="<%=request.getContextPath()%>/mvc/letovi/<%=request.getAttribute("icao")%>?dan=<%=request.getAttribute("dan")%>&odBroja=<%=odBroja + 1%>"
					class="btn btn-primary">Sljedeća stranica</a>
			</div>
		</div>
	</div>
	<br>
	</div>
	<script
		src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.3/dist/umd/popper.min.js"></script>
</body>
</html>