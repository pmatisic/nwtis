<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Traženje aerodroma</title>
</head>
<body>
	<form method="GET" action="Vjezba_06_4">
		<label for="icaoFrom">Aerodrom od:</label>
		<input type="text" name="icaoFrom"><br>
		<label for="icaoTo">Aerodrom do:</label>
		<input type="text" name="icaoTo"><br>
		<input type="submit" name="Traži udaljenosti">
	</form>
</body>
</html>