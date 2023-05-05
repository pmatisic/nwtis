<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Zadaća 2</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</head>
<body>
    <div class="container">
        <h1 class="mt-5 mb-4">Zadaća 2</h1>
        <ul class="list-group">
            <li class="list-group-item"><a href="${pageContext.servletContext.contextPath}/mvc/aerodromi">Pregled svih aerodroma</a></li>
            <li class="list-group-item"><a href="${pageContext.servletContext.contextPath}/mvc/aerodromi/">Pregled jednog aerodroma</a></li>
            <li class="list-group-item"><a href="${pageContext.servletContext.contextPath}/mvc/aerodromi/">Pregled udaljenosti po državama između dva aerodroma i ukupne udaljenosti</a></li>
            <li class="list-group-item"><a href="${pageContext.servletContext.contextPath}/mvc/aerodromi/">Pregled udaljenosti svih aerodroma od odabranog aerodroma</a></li>
            <li class="list-group-item"><a href="${pageContext.servletContext.contextPath}/mvc/aerodromi/">Pregled najdužeg puta unutar države s pregledom aerodroma od odabranog aerodroma</a></li>
            <li class="list-group-item"><a href="${pageContext.servletContext.contextPath}/mvc/letovi/LSZH?dan=24.04.2023">Pregled polazaka/letova s jednog aerodroma na određeni dan</a></li>
            <li class="list-group-item"><a href="${pageContext.servletContext.contextPath}/mvc/letovi/LSZH/EDDF?dan=24.04.2023">Pregled polazaka/letova s jednog aerodroma na drugi aerodrom na određeni dan</a></li>
            <li class="list-group-item"><a href="${pageContext.servletContext.contextPath}/mvc/letovi/spremljeni">Pregled spremljenih letova</a></li>
        </ul>
    </div>
</body>
</html>