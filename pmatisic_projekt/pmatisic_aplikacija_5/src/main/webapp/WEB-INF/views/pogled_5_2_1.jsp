<%@page language="java" contentType="text/html; charset=UTF-8"
        pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Pogled 5.2.1</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.0/dist/css/bootstrap.min.css">
</head>
<body>
<div class="container">
    <h1 class="mt-4">Registracija korisnika</h1>
    <form id="registrationForm" class="mt-3">
        <div class="mb-3">
            <label for="ime" class="form-label">Ime:</label>
            <input type="text" class="form-control" id="ime" required>
        </div>
        <div class="mb-3">
            <label for="prezime" class="form-label">Prezime:</label>
            <input type="text" class="form-control" id="prezime" required>
        </div>
        <div class="mb-3">
            <label for="korime" class="form-label">Korisničko ime:</label>
            <input type="text" class="form-control" id="korime" required>
        </div>
        <div class="mb-3">
            <label for="lozinka" class="form-label">Lozinka:</label>
            <input type="password" class="form-control" id="lozinka" required>
        </div>
        <button type="button" class="btn btn-primary" onclick="submitForm()">Registriraj se</button>
    </form>
</div>
<script>
    function submitForm() {
        var korisnik = {
            ime: document.getElementById("ime").value,
            prezime: document.getElementById("prezime").value,
            korime: document.getElementById("korime").value,
            lozinka: document.getElementById("lozinka").value
        };
        
        var xhr = new XMLHttpRequest();
        xhr.open("POST", "http://localhost:8080/pmatisic_aplikacija_5/mvc/korisnici/reg", true);
        xhr.setRequestHeader("Content-Type", "application/json");
        
        xhr.onreadystatechange = function() {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                var status = xhr.status;
                if (status === 201) {
                    alert("Registracija uspješna!");
                    window.location.href = "http://localhost:8080/pmatisic_aplikacija_5/mvc/korisnici/prijava";
                } else if (status === 400) {
                    alert("Neuspješna registracija. Molimo pokušajte ponovno.");
                } else {
                    alert("Došlo je do greške. Molimo pokušajte ponovno.");
                }
            }
        };

        xhr.send(JSON.stringify(korisnik));
		console.log(JSON.stringify(korisnik));
    }
</script>
</body>
</html>