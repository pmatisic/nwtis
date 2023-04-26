<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Ispis udaljenosti</title>
</head>
<body>
	<%
	var greska = request.getAttribute("greska");
	if (greska != null) {
	%>
	Desila se greška:
	<%=greska%>
	<%
	}
	var podaci =
	    (java.util.List<org.foi.nwtis.pmatisic.vjezba_06.Udaljenost>) request
	    .getAttribute("podaci");
	%>
	<table border="1">
		<tr>
			<th>Država</th>
			<th>Udaljenost (km)</th>
		</tr>
		<%
		float ukupno = 0;
		for (org.foi.nwtis.pmatisic.vjezba_06.Udaljenost u : podaci) {
			ukupno += u.km();
		%>
		<tr>
			<td><%= u.drzava() %></td>
			<td><%= u.km() %></td>
		</tr>
		<%
		}
		%>
		<tr>
			<td>Ukupno</td>
			<td><%= ukupno %></td>
		</tr>
	</table>
</body>
</html>