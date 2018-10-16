<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
<title>Upload Page</title>
</head>
<body>
	<form:form modelAttribute="formUpload" method="post" action="upload"
		enctype="multipart/form-data">
		
  Upload Code: (*Max 2 MB combined)
		<br>
		<br>
		<form:input path="files" type="file" multiple="multiple" />
		<form:errors path="files" cssStyle="color: red" />
		<br>
		<br>
		<button type="submit">Upload</button>
	</form:form>
</body>
</html>