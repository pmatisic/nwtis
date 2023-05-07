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
            <li class="list-group-item"><a href="#" onclick="openModalAerodrom()">Pregled jednog aerodroma</a></li>
            <li class="list-group-item"><a href="#" onclick="openModalUdaljenost()">Pregled udaljenosti po državama između dva aerodroma i ukupne udaljenosti</a></li>
            <li class="list-group-item"><a href="#" onclick="openModalUdaljenostiSvihAerodroma()">Pregled udaljenosti svih aerodroma od odabranog aerodroma</a></li>
            <li class="list-group-item"><a href="#" onclick="openModalNajduziPutDrzave()">Pregled najdužeg puta unutar države s pregledom aerodroma od odabranog aerodroma</a></li>
            <li class="list-group-item"><a href="#" onclick="openModalLetovi()">Pregled polazaka/letova s jednog aerodroma na određeni dan</a></li>
            <li class="list-group-item"><a href="#" onclick="openModalLetoviDvaAerodroma()">Pregled polazaka/letova s jednog aerodroma na drugi aerodrom na određeni dan</a></li>
            <li class="list-group-item"><a href="${pageContext.servletContext.contextPath}/mvc/letovi/spremljeni">Pregled spremljenih letova</a></li>
        </ul>
    </div>
	<div class="modal" tabindex="-1" role="dialog" id="icaoModalAerodrom">
	    <div class="modal-dialog" role="document">
	        <div class="modal-content">
	            <div class="modal-header">
	                <h5 class="modal-title">Unesite ICAO oznaku aerodroma</h5>
	                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
	                    <span aria-hidden="true">&times;</span>
	                </button>
	            </div>
	            <div class="modal-body">
	                <div class="form-group">
	                    <label for="icaoAerodrom">ICAO:</label>
	                    <input type="text" class="form-control" id="icaoAerodrom" required>
	                </div>
	            </div>
	            <div class="modal-footer">
	                <button type="button" class="btn btn-secondary" data-dismiss="modal">Zatvori</button>
	                <button type="button" class="btn btn-primary" onclick="submitFormAerodrom()">Potvrdi</button>
	            </div>
	        </div>
	    </div>
	</div>
	<div class="modal" tabindex="-1" role="dialog" id="icaoModalUdaljenost">
	    <div class="modal-dialog" role="document">
	        <div class="modal-content">
	            <div class="modal-header">
	                <h5 class="modal-title">Unesite ICAO oznaku za oba aerodroma</h5>
	                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
	                    <span aria-hidden="true">&times;</span>
	                </button>
	            </div>
	            <div class="modal-body">
	                <div class="form-group">
	                    <label for="icaoOd">ICAO Od:</label>
	                    <input type="text" class="form-control" id="icaoOd" required>
	                </div>
	                <div class="form-group">
	                    <label for="icaoDo">ICAO Do:</label>
	                    <input type="text" class="form-control" id="icaoDo" required>
	                </div>
	            </div>
	            <div class="modal-footer">
	                <button type="button" class="btn btn-secondary" data-dismiss="modal">Zatvori</button>
	                <button type="button" class="btn btn-primary" onclick="submitFormUdaljenost()">Potvrdi</button>
	            </div>
	        </div>
	    </div>
	</div>
	<div class="modal" tabindex="-1" role="dialog" id="icaoModalUdaljenostiSvihAerodroma">
	    <div class="modal-dialog" role="document">
	        <div class="modal-content">
	            <div class="modal-header">
	                <h5 class="modal-title">Unesite ICAO oznaku aerodroma</h5>
	                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
	                    <span aria-hidden="true">&times;</span>
	                </button>
	            </div>
	            <div class="modal-body">
	                <div class="form-group">
	                    <label for="icaoUdaljenosti">ICAO:</label>
	                    <input type="text" class="form-control" id="icaoUdaljenosti" required>
	                </div>
	            </div>
	            <div class="modal-footer">
	                <button type="button" class="btn btn-secondary" data-dismiss="modal">Zatvori</button>
	                <button type="button" class="btn btn-primary" onclick="submitFormUdaljenostiSvihAerodroma()">Potvrdi</button>
	            </div>
	        </div>
	    </div>
	</div>
	<div class="modal" tabindex="-1" role="dialog" id="icaoModalNajduziPutDrzave">
	    <div class="modal-dialog" role="document">
	        <div class="modal-content">
	            <div class="modal-header">
	                <h5 class="modal-title">Unesite ICAO oznaku aerodroma</h5>
	                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
	                    <span aria-hidden="true">&times;</span>
	                </button>
	            </div>
	            <div class="modal-body">
	                <div class="form-group">
	                    <label for="icaoNajduziPut">ICAO:</label>
	                    <input type="text" class="form-control" id="icaoNajduziPut" required>
	                </div>
	            </div>
	            <div class="modal-footer">
	                <button type="button" class="btn btn-secondary" data-dismiss="modal">Zatvori</button>
	                <button type="button" class="btn btn-primary" onclick="submitFormNajduziPutDrzave()">Potvrdi</button>
	            </div>
	        </div>
	    </div>
	</div>
     <div class="modal" tabindex="-1" role="dialog" id="icaoDateModal">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Unesite ICAO oznaku aerodroma i datum</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <div class="form-group">
                        <label for="icao">ICAO:</label>
                        <input type="text" class="form-control" id="icao" required>
                    </div>
					<div class="form-group">
					    <label for="date">Datum:</label>
					    <input type="text" class="form-control" id="date" pattern="\d{2}\.\d{2}\.\d{4}" placeholder="dd.MM.yyyy" required>
					</div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Zatvori</button>
                    <button type="button" class="btn btn-primary" onclick="submitFormLetovi()">Potvrdi</button>
                </div>
            </div>
        </div>
    </div>
	<div class="modal" tabindex="-1" role="dialog" id="icaoDateModalDvaAerodroma">
	    <div class="modal-dialog" role="document">
	        <div class="modal-content">
	            <div class="modal-header">
	                <h5 class="modal-title">Unesite ICAO oznaku za oba aerodroma i datum</h5>
	                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
	                    <span aria-hidden="true">&times;</span>
	                </button>
	            </div>
	            <div class="modal-body">
	                <div class="form-group">
	                    <label for="icaoOd">ICAO polazni aerodrom:</label>
	                    <input type="text" class="form-control" id="icaoOd" required>
	                </div>
	                <div class="form-group">
	                    <label for="icaoDo">ICAO dolazni aerodrom:</label>
	                    <input type="text" class="form-control" id="icaoDo" required>
	                </div>
	                <div class="form-group">
	                    <label for="dateDvaAerodroma">Datum:</label>
	                    <input type="text" class="form-control" id="dateDvaAerodroma" placeholder="dd.MM.yyyy" required>
	                </div>
	            </div>
	            <div class="modal-footer">
	                <button type="button" class="btn btn-secondary" data-dismiss="modal">Zatvori</button>
	                <button type="button" class="btn btn-primary" onclick="submitFormLetoviDvaAerodroma()">Potvrdi</button>
	            </div>
	        </div>
	    </div>
	</div>
    <script>
	    function openModalAerodrom() {
	        $("#icaoModalAerodrom").modal("show");
	    }
	
	    function submitFormAerodrom() {
	        var icao = document.getElementById("icaoAerodrom").value;
	        if (icao) {
	            var contextPath = '<%=request.getContextPath()%>';
	            var url = contextPath + "/mvc/aerodromi/" + icao;
	            window.location.href = url;
	        }
	    }
	    
	    function openModalUdaljenost() {
	        $("#icaoModalUdaljenost").modal("show");
	    }

	    function submitFormUdaljenost() {
	        var icaoOd = document.getElementById("icaoOd").value;
	        var icaoDo = document.getElementById("icaoDo").value;
	        if (icaoOd && icaoDo) {
	            var contextPath = '<%=request.getContextPath()%>';
	            var url = contextPath + "/mvc/aerodromi/" + icaoOd + "/" + icaoDo;
	            window.location.href = url;
	        }
	    }
	    
	    function openModalUdaljenostiSvihAerodroma() {
	        $("#icaoModalUdaljenostiSvihAerodroma").modal("show");
	    }

	    function submitFormUdaljenostiSvihAerodroma() {
	        var icao = document.getElementById("icaoUdaljenosti").value;
	        if (icao) {
	            var contextPath = '<%=request.getContextPath()%>';
	            var url = contextPath + "/mvc/aerodromi/" + icao + "/udaljenosti";
	            window.location.href = url;
	        }
	    }
	    
	    function openModalNajduziPutDrzave() {
	        $("#icaoModalNajduziPutDrzave").modal("show");
	    }

	    function submitFormNajduziPutDrzave() {
	        var icao = document.getElementById("icaoNajduziPut").value;
	        if (icao) {
	            var contextPath = '<%=request.getContextPath()%>';
	            var url = contextPath + "/mvc/aerodromi/" + icao + "/najduljiPutDrzave";
	            window.location.href = url;
	        }
	    }
	    
	    function openModalLetovi() {
	        $("#icaoDateModal").modal("show");
	    }
	
	    function submitFormLetovi() {
	        var icao = document.getElementById("icao").value;
	        var date = document.getElementById("date").value;
	        var datePattern = /^\d{2}\.\d{2}\.\d{4}$/;

	        if (icao && date && datePattern.test(date)) {
	            var contextPath = '<%=request.getContextPath()%>';
	            var url = contextPath + "/mvc/letovi/" + icao + "?dan=" + date;
	            window.location.href = url;
	        } else {
	            if (!datePattern.test(date)) {
	                alert("Unesite datum u formatu dd.MM.yyyy");
	            }
	        }
	    }
	    
	    function openModalLetoviDvaAerodroma() {
	        $("#icaoDateModalDvaAerodroma").modal("show");
	    }

	    function submitFormLetoviDvaAerodroma() {
	        var icaoOd = document.getElementById("icaoOd").value;
	        var icaoDo = document.getElementById("icaoDo").value;
	        var date = document.getElementById("dateDvaAerodroma").value;
	        var datePattern = /^\d{2}\.\d{2}\.\d{4}$/;

	        if (icaoOd && icaoDo && date && datePattern.test(date)) {
	            var contextPath = '<%=request.getContextPath()%>';
	            var url = contextPath + "/mvc/letovi/" + icaoOd + "/" + icaoDo + "?dan=" + date;
	            window.location.href = url;
	        } else {
	            if (!datePattern.test(date)) {
	                alert("Unesite datum u formatu dd.MM.yyyy");
	            }
	        }
	    }
    </script>
</body>
</html>