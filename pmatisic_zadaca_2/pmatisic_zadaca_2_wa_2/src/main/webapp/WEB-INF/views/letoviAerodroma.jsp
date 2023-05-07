<%@page import="java.util.List"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="org.foi.nwtis.rest.podaci.LetAviona"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="author" content="<%=request.getAttribute("ime")%> <%=request.getAttribute("prezime")%>">
<meta name="subject" content="<%=request.getAttribute("predmet")%>">
<meta name="year" content="<%=request.getAttribute("godina")%>">
<meta name="version" content="<%=request.getAttribute("verzija")%>">
<title>Pregled letova aerodroma</title>
<link rel="stylesheet"
    href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.0/dist/css/bootstrap.min.css">
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
function spremiLet(buttonElement) {
  let letJson = buttonElement.getAttribute('data-let');
  const xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState === 4) {
      const poruka = document.getElementById("poruka");
      if (this.status === 201) {
        poruka.innerHTML = "Let uspješno spremljen!";
        poruka.style.color = "green";
      } else {
        poruka.innerHTML = "Spremanje leta nije uspjelo!";
        poruka.style.color = "red";
      }
      setTimeout(() => {
        poruka.innerHTML = "";
      }, 3000);
    }
  };
  xhttp.open("POST", "<%=request.getContextPath()%>/mvc/letovi", true);
  xhttp.setRequestHeader("Content-Type", "application/json");
  xhttp.send(JSON.stringify({ let: letJson }));
}
</script>
</head>
<body>
    <div class="container">
        <h1>Pregled polazaka/letova s jednog aerodroma na drugi aerodrom na određeni dan</h1>
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
                    <th>Spremi let</th>
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
                List<LetAviona> letovi = (List<LetAviona>) request.getAttribute("letovi");
                String icaoOd = (String) request.getAttribute("icaoOd");
                String icaoDo = (String) request.getAttribute("icaoDo");
                String dan = (String) request.getAttribute("dan");

                if (letovi != null) {
                  for (LetAviona let : letovi) {
                %>
                <tr>
                    <td>
                        <button type="button" class="btn link-styled" data-let='<%=new Gson().toJson(let)%>' onclick="spremiLet(this)">Spremi</button>
                    </td>
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
        <span id="poruka"></span>
        <div class="d-flex justify-content-between mb-3">
            <a href="<%=request.getContextPath()%>/mvc/letoviAerodroma/<%=icaoOd%>/<%=icaoDo%>?dan=<%=dan%>"
                class="btn btn-primary">Pretraži ponovno</a>
        </div>
    </div>
</body>
</html>