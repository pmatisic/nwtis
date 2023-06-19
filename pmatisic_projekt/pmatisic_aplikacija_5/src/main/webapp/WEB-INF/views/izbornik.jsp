<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="author" content="<%=request.getAttribute("ime")%> <%=request.getAttribute("prezime")%>">
	<meta name="subject" content="<%=request.getAttribute("predmet")%>">
	<meta name="year" content="<%=request.getAttribute("godina")%>">
	<meta name="version" content="<%=request.getAttribute("verzija")%>">
    <title>Pogled 5.1</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</head>
<body>
    <div class="container">
        <h1 class="mt-5 mb-4">Izbornik</h1>
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
        <ul class="list-group">
            <li class="list-group-item"><a href="${pageContext.servletContext.contextPath}/mvc/izbornik/korisnici">Aktivnosti vezane uz korisnike</a></li>
			<li class="list-group-item"><a href="${pageContext.servletContext.contextPath}/mvc/izbornik/posluzitelj">Upravljanje poslužiteljem</a></li>
			<li class="list-group-item"><a href="${pageContext.servletContext.contextPath}/mvc/izbornik/poruke">Pregled svih primljenih JMS poruka</a></li>
			<li class="list-group-item"><a href="${pageContext.servletContext.contextPath}/mvc/izbornik/aerodromi">Aktivnosti vezane uz aerodrome</a></li>
            <li class="list-group-item"><a href="${pageContext.servletContext.contextPath}/mvc/izbornik/letovi">Aktivnosti vezane uz letove</a></li>
			<li class="list-group-item"><a href="${pageContext.servletContext.contextPath}/mvc/izbornik/dnevnik">Pregled zapisa dnevnika</a></li>
        </ul>
    </div>
</body>
</html>