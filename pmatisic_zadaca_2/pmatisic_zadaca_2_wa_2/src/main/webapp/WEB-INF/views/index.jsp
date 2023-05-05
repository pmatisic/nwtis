<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Zadaća 2</title>
</head>
<body>
<a href="${pageContext.servletContext.contextPath}/mvc/aerodromi">Pregled svih aerodroma</a><br/>
<a href="${pageContext.servletContext.contextPath}/mvc/aerodromi/LSZH">Pregled jednog aerodroma</a><br/>
<a href="${pageContext.servletContext.contextPath}/mvc/aerodromi/LSZH/EDDF">Pregled udaljenosti po državama između dva aerodroma</a><br/>
<a href="${pageContext.servletContext.contextPath}/mvc/aerodromi/LSZH/udaljenosti">Pregled udaljenosti svih aerodroma od odabranog aerodroma</a><br/>
<a href="${pageContext.servletContext.contextPath}/mvc/aerodromi/LSZH/najduljiPutDrzave">Pregled najdužeg puta unutar države s pregledom aerodroma od odabranog aerodroma</a><br/>
<%-- <a href="${pageContext.servletContext.contextPath}/mvc/letovi/{icao}?dan=">Pregled polazaka s jednog aerodroma na određeni dan</a><br/> --%>
<%-- <a href="${pageContext.servletContext.contextPath}/mvc/letovi/{icaoOd}/{icaoDo}?dan=">Pregled polazaka s jednog aerodroma na drugi aerodrom na određeni dan</a><br/> --%>
<%-- <a href="${pageContext.servletContext.contextPath}/mvc/letovi/spremljeni">Pregled spremljenih letova</a><br/> --%>
</body>
</html>