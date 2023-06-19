<%@page language="java" contentType="text/html; charset=UTF-8"
        pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Pogled 5.2.2</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.0/dist/css/bootstrap.min.css">
</head>
<body>
<div class="container">
    <h1 class="mt-4">Prijava korisnika</h1>
    <form id="loginForm" class="mt-3">
        <div class="mb-3">
            <label for="korime" class="form-label">Korisničko ime:</label>
            <input type="text" class="form-control" id="korime" required>
        </div>
        <div class="mb-3">
            <label for="lozinka" class="form-label">Lozinka:</label>
            <input type="password" class="form-control" id="lozinka" required>
        </div>
        <button type="button" class="btn btn-primary" onclick="submitForm()">Prijavi se</button>
    </form>
</div>
<script>
    function submitForm() {
        var korisnik = {
            korime: document.getElementById("korime").value,
            lozinka: document.getElementById("lozinka").value
        };

        var xhr = new XMLHttpRequest();
        xhr.open("POST", "http://localhost:8080/pmatisic_aplikacija_5/mvc/korisnici/pri", true);
        xhr.setRequestHeader("Content-Type", "application/json");

        xhr.onreadystatechange = function() {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                var status = xhr.status;
                if (status === 200) {
                    alert("Uspješno ste prijavljeni!");
                    window.location.href = "http://localhost:8080/pmatisic_aplikacija_5/mvc/korisnici";
                } else if (status === 401) {
                    alert("Neuspješna prijava. Pogrešno korisničko ime ili lozinka.");
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