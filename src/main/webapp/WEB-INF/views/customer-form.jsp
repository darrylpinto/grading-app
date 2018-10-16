<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
<title>Customer Registration Form</title>

<style>
.error {color: red}
</style>
</head>

<body>
	Fill out: Asterisk (*) are required fields
	<form:form action="processForm" modelAttribute="customer">
	
		First Name: <form:input path="firstName" />

		<br>
		<br>
		
		Last Name (*): <form:input path="lastName" />
		<form:errors path="lastName" cssClass="error" />

		<br>
		<br>
		
		Free Pass (0-10): <form:input path="freePasses" />
		<form:errors path="freePasses" cssClass="error" />

		<br>
		<br>

		<input type="submit" value="Click Me" />


	</form:form>




</body>


</html>








