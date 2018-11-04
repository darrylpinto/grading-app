<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Homework ${homework.id}: Questions</title>
</head>

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

<body>
	<h2>Add Questions for Homework ${homework.id}</h2>

	<form:form modelAttribute="question" enctype="multipart/form-data"
		method="post">

		<br>Question number: ${currentQuestion}
		<br>
		<br>Problem name:
				<form:input path="problemName" placeholder="Enter problem Name" />
		<br>
		<form:errors path="problemName" cssClass="error" />
		<br>

		<br>Description:
				<form:input path="questionDescription"
			placeholder="Enter Description" />
		<br>

		<br> Test Case Files:
				<form:input path="testCases" type="file" multiple="multiple" />
		<br>
		<form:errors path="testCases" cssClass="error" />
		<br>

		<br> Output Files:
				<form:input path="outputTestCases" type="file" multiple="multiple" />
		<br>
		<form:errors path="outputTestCases" cssClass="error" />
		<br>
		<input type="submit" value="SUBMIT" />
	</form:form>

</body>
</html>