<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

<h1>파일 업로드</h1>
<h3>COS Fileupload</h3>
<hr>

<form action="<%=request.getContextPath() %>/cos/fileupload" method="post" enctype="multipart/form-data">

<label>제목<input type="text" name="title"></label><br>
<label>이름<input type="text" name="username"></label><br>
<label>과일<input type="text" name="fruit"></label><br>

<label>파일<input type="file" name="upfile"></label><br><br>

<button>전송</button>

</form>

</body>
</html>