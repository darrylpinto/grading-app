<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Grade Student</title>
</head>
<body>

	<h3>Enter marks!</h3>

	Homework: ${gradeHomework.homework}

	<br> Student Name:
	<b>${studentName}</b>

	<br>
	<br> Question:
	<b>${questionName}</b>

	<br>
	<br> Submitted Files:
	<br>

	<c:forEach items="${submittedFiles}" var="fileName">
		<a
			href="questionStudent?studentName=${studentName}&questionName=${questionName}&file=${fileName}"
			target="_blank">${fileName} </a>
		<br>
	</c:forEach>
	<br>

	<br> Test Case Results:
	<br>

	<c:forEach items="${codeOutput}" var="_code_output_" varStatus="loop">
		${loop.index + 1}.
		
		&nbsp;&nbsp;&nbsp;&nbsp;	&nbsp;&nbsp;&nbsp;&nbsp;
  		Student Output : <b>${_code_output_}</b>
		
		&nbsp;&nbsp;&nbsp;&nbsp;	&nbsp;&nbsp;&nbsp;&nbsp;
		Expected Output:  ${expectedOutput[loop.index]}
		
		&nbsp;&nbsp;&nbsp;&nbsp;	&nbsp;&nbsp;&nbsp;&nbsp;	
		Test Case <b>${testcaseResult[loop.index]}</b>
		<br>
	</c:forEach>

	<br>
	<br>
</body>
</html>