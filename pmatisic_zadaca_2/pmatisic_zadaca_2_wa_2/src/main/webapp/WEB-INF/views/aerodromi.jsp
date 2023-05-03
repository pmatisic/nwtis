<%@page import="java.util.List"%>
<%@page import="org.foi.nwtis.podaci.Aerodrom"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Pregled svih aerodroma</title>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.0/dist/css/bootstrap.min.css">
<style>
/* Dodajte vaše SCSS stilove ovdje */
</style>
</head>
<body>
  <div class="container">
    <h1>Pregled svih aerodroma</h1>
    <table id="aerodromiTable" class="table table-striped">
      <thead>
        <tr>
          <th>ICAO</th>
          <th>Naziv</th>
          <th>Država</th>
          <th>Koordinate</th>
        </tr>
      </thead>
      <tbody>
        <% 
        List<Aerodrom> aerodromi = (List<Aerodrom>) request.getAttribute("aerodromi");
        if (aerodromi != null) {
          for (Aerodrom aerodrom : aerodromi) {
        %>
        <tr>
          <td><%= aerodrom.getIcao() %></td>
          <td><%= aerodrom.getNaziv() %></td>
          <td><%= aerodrom.getDrzava() %></td>
          <td><%= aerodrom.getLokacija().getLatitude() + ", " + aerodrom.getLokacija().getLongitude() %></td>
        </tr>
        <%
          }
        }
        %>
      </tbody>
    </table>
  </div>
</body>
</html>
