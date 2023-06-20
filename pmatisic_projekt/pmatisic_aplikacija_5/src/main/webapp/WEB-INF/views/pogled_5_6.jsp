<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="author" content="<%=request.getAttribute("ime")%> <%=request.getAttribute("prezime")%>">
<meta name="subject" content="<%=request.getAttribute("predmet")%>">
<meta name="year" content="<%=request.getAttribute("godina")%>">
<meta name="version" content="<%=request.getAttribute("verzija")%>">
<title>Pogled 5.6</title>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</head>
<body>
	<div class="container">
		<h1 class="mt-5 mb-4">Izbornik</h1>
		<div class="author-info">
			<p>
				<strong>Autor: </strong><%=request.getAttribute("ime")%> <%=request.getAttribute("prezime")%></p>
			<p>
				<strong>Predmet: </strong><%=request.getAttribute("predmet")%></p>
			<p>
				<strong>Godina: </strong><%=request.getAttribute("godina")%></p>
			<p>
				<strong>Verzija aplikacije: </strong><%=request.getAttribute("verzija")%></p>
		</div>
		<ul class="list-group">
			<li class="list-group-item"><a href="#" onclick="openModalForma1()">Pregled spremljenih letova s određenog aerodroma u zadanom intervalu</a></li>
			<li class="list-group-item"><a href="#" onclick="openModalForma2()">Pregled spremljenih letova s određenog aerodroma na zadani datum</a></li>
			<li class="list-group-item"><a href="#" onclick="openModalForma3()">Pregled letova s određenog aerodroma na zadani datum</a></li>
		</ul>
		<br>
		<div class="d-flex justify-content-between mb-3">
			<a href="<%=request.getContextPath()%>/index.jsp"
				class="btn btn-secondary">Povratak na početnu stranicu</a>
		</div>
	</div>
	<!-- Modal Forma 1 -->
	<div class="modal fade" id="modalForma1" tabindex="-1" role="dialog"
		aria-hidden="true">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title">Dohvati Letove Prema Intervalima</h5>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
					<form onsubmit="event.preventDefault(); submitFormForma1();">
						<div class="form-group">
							<label for="icao1">ICAO:</label> <input type="text"
								class="form-control" id="icao1" name="icao">
						</div>
						<div class="form-group">
						    <label for="datumOd">Datum Od (dd.MM.yyyy):</label>
						    <input type="text" class="form-control" id="datumOd" name="datumOd" pattern="\d{2}\.\d{2}\.\d{4}">
						</div>
						<div class="form-group">
						    <label for="datumDo">Datum Do (dd.MM.yyyy):</label>
						    <input type="text" class="form-control" id="datumDo" name="datumDo" pattern="\d{2}\.\d{2}\.\d{4}">
						</div>
						<button type="submit" class="btn btn-primary">Dohvati</button>
					</form>
				</div>
			</div>
		</div>
	</div>

	<!-- Modal Forma 2 -->
	<div class="modal fade" id="modalForma2" tabindex="-1" role="dialog"
		aria-hidden="true">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title">Dohvati Spremljene Letove Na Datum</h5>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
					<form onsubmit="event.preventDefault(); submitFormForma2();">
						<div class="form-group">
							<label for="icao2">ICAO:</label> <input type="text"
								class="form-control" id="icao2" name="icao">
						</div>
						<div class="form-group">
						    <label for="datum">Datum (dd.MM.yyyy):</label>
						    <input type="text" class="form-control" id="datum" name="datum" pattern="\d{2}\.\d{2}\.\d{4}">
						</div>
						<button type="submit" class="btn btn-primary">Dohvati</button>
					</form>
				</div>
			</div>
		</div>
	</div>

	<!-- Modal Forma 3 -->
	<div class="modal fade" id="modalForma3" tabindex="-1" role="dialog"
		aria-hidden="true">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title">Dohvati Sve Letove Na Datum</h5>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
					<form onsubmit="event.preventDefault(); submitFormForma3();">
						<div class="form-group">
							<label for="icao3">ICAO:</label> <input type="text"
								class="form-control" id="icao3" name="icao">
						</div>
						<div class="form-group">
						    <label for="datum3">Datum (dd.MM.yyyy):</label>
						    <input type="text" class="form-control" id="datum3" name="datum" pattern="\d{2}\.\d{2}\.\d{4}">
						</div>
						<button type="submit" class="btn btn-primary">Dohvati</button>
					</form>
				</div>
			</div>
		</div>
	</div>

	<script>
    function openModalForma1() {
        $("#modalForma1").modal("show");
    }

    function submitFormForma1() {
        var icao = document.getElementById("icao1").value;
        var datumOd = document.getElementById("datumOd").value;
        var datumDo = document.getElementById("datumDo").value;
        if (icao && datumOd && datumDo) {
            var contextPath = '<%=request.getContextPath()%>';
            var url = contextPath + "/mvc/letovi/interval?icao=" + encodeURIComponent(icao) + "&datumOd=" + encodeURIComponent(datumOd) + "&datumDo=" + encodeURIComponent(datumDo);
            window.location.href = url;
        }
    }

    function openModalForma2() {
        $("#modalForma2").modal("show");
    }

    function submitFormForma2() {
        var icao = document.getElementById("icao2").value;
        var datum = document.getElementById("datum").value;
        if (icao && datum) {
            var contextPath = '<%=request.getContextPath()%>';
            var url = contextPath + "/mvc/letovi/spremljeni?icao=" + encodeURIComponent(icao) + "&datum=" + encodeURIComponent(datum);
            window.location.href = url;
        }
    }

    function openModalForma3() {
        $("#modalForma3").modal("show");
    }

    function submitFormForma3() {
        var icao = document.getElementById("icao3").value;
        var datum = document.getElementById("datum3").value;
        if (icao && datum) {
            var contextPath = '<%=request.getContextPath()%>';
				var url = contextPath + "/mvc/letovi/datum?icao="
						+ encodeURIComponent(icao) + "&datum="
						+ encodeURIComponent(datum);
				window.location.href = url;
			}
		}
	</script>
</body>
</html>