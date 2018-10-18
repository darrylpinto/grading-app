<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Success</title>
</head>
<body>
++++++++++++ <br>
	<c:forEach items="${codeFileNames}" var="fileName">
  File <b>${fileName}</b> uploaded sucessfully<br />
	</c:forEach>
++++++++++++ <br>
	<br>
	<br>
++++++++++++ <br>
	<c:forEach items="${writeupFileNames}" var="fileName">
  File <b>${fileName}</b> uploaded sucessfully<br />
	</c:forEach>
++++++++++++ <br>
</body>
</html>

