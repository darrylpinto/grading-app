<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html>
<html>
<head>
<title>Student Submission</title>

<style>
.error {
	color: red
}

html {
	display: table;
	margin: auto;
}

body {
	display: table-cell;
	vertical-align: middle;
}
</style>

</head>
<body>
	<h3>ALGORITHMS-GRADING</h3>
	<p>All the fields are required</p>
	<form:form modelAttribute="submission" method="post">
		
 	RIT CS Username: <form:input path="username" />
		<form:errors path="username" cssClass="error" />
		<br>
		<br>
 	 Homework: <form:radiobuttons path="homework"
			items="${delete.options}" />
		<form:errors path="homework" cssClass="error" />


		<br>
		<br>

		<input type="submit" value="SUBMIT" />
	</form:form>

	Part 1/2
</body>
</html>