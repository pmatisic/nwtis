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
<title>Pogled 5.3</title>
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
.buttons-container {
    text-align: center;
    margin-bottom: 1.5rem;
}

.buttons-container button {
    margin: 0.25rem;
}

.response-card {
    margin-top: 2rem;
}
</style>
</head>
<body>
	<div class="container">
		<h1>Upravljanje poslužiteljem</h1>
		<div class="author-info">
		   <p><strong>Autor: </strong><%=request.getAttribute("ime")%> <%=request.getAttribute("prezime")%></p>
		   <p><strong>Predmet: </strong><%=request.getAttribute("predmet")%></p>
		   <p><strong>Godina: </strong><%=request.getAttribute("godina")%></p>
		   <p><strong>Verzija aplikacije: </strong><%=request.getAttribute("verzija")%></p>
		</div>
		<div class="buttons-container">
			<button onclick="posaljiKomandu('STATUS')" class="btn btn-primary">STATUS</button>
			<button onclick="posaljiKomandu('KRAJ')" class="btn btn-primary">KRAJ</button>
			<button onclick="posaljiKomandu('INIT')" class="btn btn-primary">INIT</button>
			<button onclick="posaljiKomandu('PAUZA')" class="btn btn-primary">PAUZA</button>
			<button onclick="posaljiKomandu('INFO DA')" class="btn btn-primary">INFO/DA</button>
			<button onclick="posaljiKomandu('INFO NE')" class="btn btn-primary">INFO/NE</button>
		</div>
		<div id="odgovorContainer" class="card response-card">
			<div class="card-body">
				<p class="card-text"></p>
			</div>
		</div>
		<br>
		<div class="d-flex justify-content-between mb-3">
			<a href="<%=request.getContextPath()%>/index.jsp"
				class="btn btn-secondary">Povratak na početnu stranicu</a>
		</div>
	</div>
	<script>
		function posaljiKomandu(komanda) {
		    const podatak = JSON.stringify({ komanda });
	
		    fetch("http://localhost:8080/pmatisic_aplikacija_5/mvc/nadzor", {
		        method: "POST",
		        headers: {
		            "Content-Type": "application/json"
		        },
		        body: podatak
		    })
		    .then(response => {
		        if (response.status === 200) {
		            document.querySelector('#odgovorContainer .card-text').innerHTML = 'Komanda uspjela';
		        } else if (response.status === 400) {
		            document.querySelector('#odgovorContainer .card-text').innerHTML = 'Komanda nije uspjela';
		        } else {
		            throw new Error('Mrežna greška');
		        }
		    })
		    .catch(error => {
		        console.error('Pojavila se greška:', error);
		        document.querySelector('#odgovorContainer .card-text').innerHTML = `Odgovor: ${error.message}`;
		        alert("Došlo je do greške. Molimo pokušajte ponovno.");
		    });
		}
	</script>
</body>
</html>