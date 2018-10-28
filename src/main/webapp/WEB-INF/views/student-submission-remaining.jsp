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
</style>

</head>
<body>
	<h3>ALGORITHMS-GRADING</h3>
	<p>All the fields are required</p>
	<form:form modelAttribute="submission" method="post"
		enctype="multipart/form-data">
		
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
	Code for the Question: <small>(*Max 2 MB combined)</small>
		<br>
		<!-- TODO READ FROM FILE AND ADD ALLOWED TYPES -->
		<br>
		<form:input path="codeFiles" type="file" multiple="multiple" />
		<form:errors path="codeFiles" cssClass="error" />
		<br>
		<br>
	Language:<form:radiobuttons path="language"
			items="${hw.languageOptions}" />
		<form:errors path="language" cssClass="error" />
		<br>
		<br>

	Write-Up for the Question: 	<br>
		<!-- TODO READ FROM FILE AND ADD ALLOWED TYPES -->
		<br>
		<form:input path="writeupFiles" type="file" />
		<form:errors path="writeupFiles" cssClass="error" />
		<br>
		<br>

		<input type="submit" value="SUBMIT" />
	</form:form>
	<br>
	<br>
	<small>** IF VALIDATION ERROR OCCURS CODE AND WRITE-UP FIELDS
		NEED TO BE UPLOADED AGAIN</small>
	<br>
	<br>
	<br> Part 2/2
</body>
</html>