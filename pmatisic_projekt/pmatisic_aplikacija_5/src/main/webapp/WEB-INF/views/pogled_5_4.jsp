<%@page import="java.util.List"%>
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
<title>Pogled 5.4</title>
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
		<h1>Pregled primljenih JMS poruka</h1>
		<div class="author-info">
		   <p><strong>Autor: </strong><%=request.getAttribute("ime")%> <%=request.getAttribute("prezime")%></p>
		   <p><strong>Predmet: </strong><%=request.getAttribute("predmet")%></p>
		   <p><strong>Godina: </strong><%=request.getAttribute("godina")%></p>
		   <p><strong>Verzija aplikacije: </strong><%=request.getAttribute("verzija")%></p>
		</div>
		<div class="d-flex justify-content-between mb-3">
		    <div>
		        <a href="<%=request.getContextPath()%>/index.jsp" class="btn btn-secondary">Povratak na početnu stranicu</a>
		        <button onclick="obrisiPoruke()" class="btn btn-danger">Obriši</button>
		    </div>
		</div>
		<table id="porukeTable" class="table table-striped">
			<thead>
				<tr>
					<th>Poruka</th>
				</tr>
			</thead>
			<tbody>
				<%
				List<String> poruke = (List<String>) request.getAttribute("poruke");
				Integer odBroja = (Integer) request.getAttribute("odBroja");
				Integer broj = (Integer) request.getAttribute("broj");

				if (poruke != null) {
					for (String poruka : poruke) {
					%>
					<tr>
						<td><%=poruka%></td>
					</tr>
					<%
					}
				}
				%>
			</tbody>
		</table>
		<div class="pagination-container">
			<div class="pagination-btns">
				<a href="<%=request.getContextPath()%>/mvc/letovi/poruke"
					class="btn btn-primary">Početak</a> 
				<a href="<%=request.getContextPath()%>/mvc/letovi/poruke?odBroja=<%=odBroja <= 1 ? 1 : odBroja - 1%>"
				    class="btn btn-primary <%=odBroja <= 1 ? "disabled" : ""%>">Prethodna stranica</a>
				<a href="<%=request.getContextPath()%>/mvc/letovi/poruke?odBroja=<%=odBroja + 1%>"
				    class="btn btn-primary">Sljedeća stranica</a>
			</div>
		</div>
		<br>
	</div>
	<script>
	    function obrisiPoruke() {
	        var xhr = new XMLHttpRequest();
	        xhr.open("DELETE", "<%=request.getContextPath()%>/mvc/letovi/por", true);
	        xhr.onreadystatechange = function () {
	            if (xhr.readyState === 4 && xhr.status === 204) {
	                alert("Poruke su uspješno obrisane!");
	                window.location.reload();
	            }
	        };
	        xhr.send();
	    }
	</script>
</body>
</html>