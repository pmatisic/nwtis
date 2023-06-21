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
    <title>Pogled 5.5</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</head>
<body>
    <div class="container">
        <h1 class="mt-5 mb-4">Izbornik</h1>
        <div class="author-info">
            <p><strong>Autor: </strong><%=request.getAttribute("ime")%> <%=request.getAttribute("prezime")%></p>
            <p><strong>Predmet: </strong><%=request.getAttribute("predmet")%></p>
            <p><strong>Godina: </strong><%=request.getAttribute("godina")%></p>
            <p><strong>Verzija aplikacije: </strong><%=request.getAttribute("verzija")%></p>
        </div>
        <ul class="list-group">
            <li class="list-group-item"><a href="${pageContext.servletContext.contextPath}/mvc/aerodromi/svi">Pregled svih aerodroma</a></li>
            <li class="list-group-item"><a href="#" onclick="openModalForma1()">Pregled podataka izabranog aerodroma</a></li>
            <li class="list-group-item"><a href="${pageContext.servletContext.contextPath}/mvc/aerodromi/polasci">Pregled aerodroma za koje se preuzimaju podaci o polascima</a></li>
            <li class="list-group-item"><a href="#" onclick="openModalForma2()">Pregled udaljenosti između dva aerodroma unutar država preko kojih se leti</a></li>
           	<li class="list-group-item"><a href="#" onclick="openModalForma3()">Pregled udaljenosti između dva aerodroma</a></li>
            <li class="list-group-item"><a href="#" onclick="openModalForma4()">Pregled aerodroma i udaljenosti do polaznog aerodroma unutar države odredišnog aerodroma</a></li>
            <li class="list-group-item"><a href="#" onclick="openModalForma5()">Pregled aerodroma i udaljenosti do polaznog aerodroma unutar zadane države</a></li>
        </ul>
        <br>
        <div class="d-flex justify-content-between mb-3">
            <a href="<%=request.getContextPath()%>/index.jsp" class="btn btn-secondary">Povratak na početnu stranicu</a>
        </div>
    </div>

	<!-- Modal Forma 1 -->
	<div class="modal fade" id="modalForma1" tabindex="-1" role="dialog"
		aria-hidden="true">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title">Pregled podataka izabranog aerodroma</h5>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
					<form onsubmit="event.preventDefault(); submitFormForma1();">
						<div class="form-group">
							<label for="icao1">Unesite ICAO aerodroma:</label> <input type="text"
								class="form-control" id="icao1" name="icao">
						</div>
						<button type="submit" class="btn btn-primary">Prikaži</button>
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
					<h5 class="modal-title">Pregled udaljenosti između dva aerodroma unutar država preko kojih se leti</h5>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
					<form onsubmit="event.preventDefault(); submitFormForma2();">
						<div class="form-group">
							<label for="icao2">Unesite ICAO ishodišnog aerodroma:</label> <input type="text"
								class="form-control" id="icao2" name="icao">
						</div>
						<div class="form-group">
							<label for="icao3">Unesite ICAO odredišnog aerodroma:</label> <input type="text"
								class="form-control" id="icao3" name="icao">
						</div>
						<button type="submit" class="btn btn-primary">Prikaži</button>
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
					<h5 class="modal-title">Pregled udaljenosti između dva aerodroma</h5>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
					<form onsubmit="event.preventDefault(); submitFormForma3();">
						<div class="form-group">
							<label for="icao4">Unesite ICAO ishodišnog aerodroma:</label> <input type="text"
								class="form-control" id="icao4" name="icao">
						</div>
						<div class="form-group">
							<label for="icao5">Unesite ICAO odredišnog aerodroma:</label> <input type="text"
								class="form-control" id="icao5" name="icao">
						</div>
						<button type="submit" class="btn btn-primary">Dohvati</button>
					</form>
				</div>
			</div>
		</div>
	</div>

	<!-- Modal Forma 4 -->
	<div class="modal fade" id="modalForma4" tabindex="-1" role="dialog"
		aria-hidden="true">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title">Pregled aerodroma i udaljenosti do polaznog aerodroma unutar države odredišnog aerodroma</h5>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
					<form onsubmit="event.preventDefault(); submitFormForma4();">
						<div class="form-group">
							<label for="icao6">Unesite ICAO ishodišnog aerodroma:</label> <input type="text"
								class="form-control" id="icao6" name="icao">
						</div>
						<div class="form-group">
							<label for="icao7">Unesite ICAO odredišnog aerodroma:</label> <input type="text"
								class="form-control" id="icao7" name="icao">
						</div>
						<button type="submit" class="btn btn-primary">Dohvati</button>
					</form>
				</div>
			</div>
		</div>
	</div>

	<!-- Modal Forma 5 -->
	<div class="modal fade" id="modalForma5" tabindex="-1" role="dialog"
		aria-hidden="true">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title">Pregled aerodroma i udaljenosti do polaznog aerodroma unutar zadane države</h5>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
					<form onsubmit="event.preventDefault(); submitFormForma5();">
						<div class="form-group">
							<label for="icao8">Unesite ICAO aerodroma:</label> <input type="text"
								class="form-control" id="icao8" name="icao">
						</div>
						<div class="form-group">
							<label for="drzava">Unesite oznaku države:</label> <input type="text"
								class="form-control" id="drzava" name="drzava">
						</div>
						<div class="form-group">
							<label for="km">Unesite broj kilometara:</label> <input type="text"
								class="form-control" id="km" name="km">
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
	        var icao1 = document.getElementById("icao1").value;
	        if (icao1) {
	            var contextPath = '<%=request.getContextPath()%>';
	            var url = contextPath + "/mvc/aerodromi/" + icao1;
	            window.location.href = url;
	        }
	    }
	
	    function openModalForma2() {
	        $("#modalForma2").modal("show");
	    }
	
	    function submitFormForma2() {
	        var icao2 = document.getElementById("icao2").value;
	        var icao3 = document.getElementById("icao3").value;
	        if (icao2 && icao3) {
	            var contextPath = '<%=request.getContextPath()%>';
	            var url = contextPath + "/mvc/aerodromi/" + icao2 + "/" + icao3;
	            window.location.href = url;
	        }
	    }
	
	    function openModalForma3() {
	        $("#modalForma3").modal("show");
	    }
	    
	    function submitFormForma3() {
	        var icao4 = document.getElementById("icao4").value;
	        var icao5 = document.getElementById("icao5").value;
	        if (icao4 && icao5) {
	            var contextPath = '<%=request.getContextPath()%>';
	            var url = contextPath + "/mvc/aerodromi/" + icao4 + "/izracun/" + icao5;
	            window.location.href = url;
	        }
	    }
	    
	    function openModalForma4() {
	        $("#modalForma4").modal("show");
	    }
	    
	    function submitFormForma4() {
	        var icao6 = document.getElementById("icao6").value;
	        var icao7 = document.getElementById("icao7").value;
	        if (icao6 && icao7) {
	            var contextPath = '<%=request.getContextPath()%>';
	            var url = contextPath + "/mvc/aerodromi/" + icao6 + "/udaljenost1/" + icao7;
	            window.location.href = url;
	        }
	    }
	    
	    function openModalForma5() {
	        $("#modalForma5").modal("show");
	    }
	    
	    function submitFormForma5() {
	        var icao = document.getElementById("icao8").value;
	        var drzava = document.getElementById("drzava").value;
	        var km = document.getElementById("km").value;
	        if (icao && drzava && km) {
	            var contextPath = '<%=request.getContextPath()%>';
					var url = contextPath + "/mvc/aerodromi/" + icao + "/udaljenost2?drzava=" + drzava + "&km=" + km;
					window.location.href = url;
			}
		}
    </script>
</body>
</html>