<%@page import="java.util.List"%>
<%@page import="org.foi.nwtis.pmatisic.projekt.podatak.Dnevnik"%>
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
<title>Pogled 5.7</title>
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
		<h1>Pregled zapisa dnevnika</h1>
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
		<div class="row mb-3">
			<div class="col-md-4">
				<a href="<%=request.getContextPath()%>/index.jsp"
					class="btn btn-secondary">Povratak na početnu stranicu</a>
			</div>
			<div class="col-md-4">
				<select id="filtrirajVrstu" onchange="filtrirajZapise();"
					class="form-select float-end">
					<option value="AP2">AP2</option>
					<option value="AP4">AP4</option>
					<option value="AP5">AP5</option>
				</select>
			</div>
		</div>
		<table id="dnevnikTable" class="table table-striped">
			<thead>
				<tr>
					<th>Vrsta</th>
					<th>Vrijeme pristupa</th>
					<th>Putanja</th>
					<th>IP adresa</th>
					<th>Korisnik</th>
				</tr>
			</thead>
			<tbody>
				<%
				List<Dnevnik> zapisi = (List<Dnevnik>) request.getAttribute("zapisi");
				Integer odBroja = (Integer) request.getAttribute("odBroja");
				Integer broj = (Integer) request.getAttribute("broj");

				if (zapisi != null) {
					for (Dnevnik zapis : zapisi) {
				%>
				<tr>
					<td><%=zapis.vrsta()%></td>
					<td><%=zapis.vrijemePristupa()%></td>
					<td><%=zapis.putanja()%></td>
					<td><%=zapis.ipAdresa()%></td>
					<td><%=zapis.korisnik()%></td>
				</tr>
				<%
				}
				}
				%>
			</tbody>
		</table>
		<div class="pagination-container">
			<div class="pagination-btns">
				<a class="btn btn-primary">Početak</a> <a
					class="btn btn-primary <%=odBroja <= 1 ? "disabled" : ""%>">Prethodna
					stranica</a> <a class="btn btn-primary">Sljedeća stranica</a>
			</div>
		</div>
		<br>
	</div>
	<script>
	    function filtrirajZapise() {
	        var traziVrstu = document.getElementById("filtrirajVrstu").value;
	        localStorage.setItem('vrsta', traziVrstu);
	        var contextPath = '<%=request.getContextPath()%>';
	        var url = contextPath + "/mvc/dnevnik?vrsta=" + traziVrstu;
	        window.location.href = url;
	    }
	    
	    function generirajURL(odBroja) {
	        var vrsta = localStorage.getItem('vrsta') || '';
	        var contextPath = '<%=request.getContextPath()%>';
	        return contextPath + "/mvc/dnevnik?vrsta=" + vrsta + "&odBroja=" + odBroja;
	    }
	    
	    window.onload = function() {
	        var vrsta = localStorage.getItem('vrsta');
	        if(vrsta) {
	            document.getElementById('filtrirajVrstu').value = vrsta;
	        }
	        
	        document.querySelector('.pagination-btns .btn-primary').href = generirajURL(1);
	        document.querySelectorAll('.pagination-btns .btn-primary')[1].href = generirajURL('<%=odBroja <= 1 ? 1 : odBroja - 1%>');
	        document.querySelectorAll('.pagination-btns .btn-primary')[2].href = generirajURL('<%=odBroja + 1%>');
		}
	</script>
</body>
</html>