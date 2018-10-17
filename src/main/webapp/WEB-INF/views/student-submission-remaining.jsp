<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
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
		
	RIT CS Username: ${submission.username}
		<br>
		<br>
 	 Homework: ${submission.homework}
		<br>
		<br>
	
 	  Question: <form:radiobuttons path="question"
			items="${hw.questionOptions}" />
		<form:errors path="question" cssClass="error" />

		<br>
		<br>

		<input type="submit" value="SUBMIT" />
	</form:form>

	Part 1/2
</body>
</html>