<%@page import="dto.UploadFile"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<% 
	List<UploadFile> uploadList = (List<UploadFile>)request.getAttribute("list");


%>


<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>

<style type="text/css">

table {
	border: 1px solid steelblue;
	border-collapse: collapse;
	
	width: 1100px;
	margin: 0 auto;
}

td {

	border-top: 1px solid #ccc;
	text-align: center;
	padding: 5px 10px;

}

tr:hover {

	background-color: lightblue;

}

td:hover {
	
	background-color: steelblue;

}

img {
	width: 100px;
	hwight: 100px;

}

img:hover {
	width: 300px;
	height: 400px;

}

button {
	margin: 0 auto;
}

</style>

</head>
<body>

<h1>업로드 된 파일 목록</h1>
<hr>

<a href="<%=request.getContextPath() %>/commons/fileupload"><button>COMMONS</button></a>
<a href="<%=request.getContextPath() %>/cos/fileupload"><button>COS</button></a>

<table>
<tr>
	<th>파일no.</th>
	<th>파일 이름</th>
	<th>저장된 이름</th>
	<th>미리보기 이미지</th>
	<th></th>
</tr>
<% for(int i=0; i<uploadList.size(); i++) { %>
<tr>
	<td><%=uploadList.get(i).getFileno() %></td>
	<td>
		<a href="<%=request.getContextPath() %>/upload/<%=uploadList.get(i).getOriginName() %>" target="_blank"><%=uploadList.get(i).getOriginName() %></a>
	</td>
	<td><%=uploadList.get(i).getStoredName() %></td>
	<td><img alt="no image" src="<%=request.getContextPath() %>/upload/<%=uploadList.get(i).getStoredName() %>"></td>
	<td>
		<a href="<%=request.getContextPath() %>/upload/<%=uploadList.get(i).getStoredName() %>" download="<%=uploadList.get(i).getOriginName() %>">다운받기</a>
	</td>
</tr>
<% } %>

<!-- http://localhost:8088/upload/20230405120959294 -->

</table>


</body>
</html>