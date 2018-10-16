<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html>
<html>
<head>
<title>Student Submission</title>
</head>
<body>

	<form:form action="processForm" modelAttribute="submission">
		<!-- CHANGE model attribute to SUBMISSION -->

	RIT CS User-Name: <form:input path="studentUsername" />
		<br>
		<br>

	Select Question: <form:select path="country">

			<!-- <form:option value="N/A" label="Select Country"  /> 	-->
			<!-- <form:option value="IND" label="India" /> 				-->
			<!-- <form:option value="USA" label="United States" /> 		-->
			<!--  <form:option value="DEU" label="Germany" />			-->

			<form:options items="${submission.hwOptions}" />

		</form:select>
		<br>
		<br>
	
	


		<input type="submit" value="SUBMIT" />
	</form:form>

</body>
</html>