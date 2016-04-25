<html>
<head><title><%=title%></title></head>
	<body>
		<table border="1">
			<tr><td>姓名</td><td>性别</td></tr>
			<%
					users.each{user->
						  out. println "<tr><td>${user.name}</td><td>${user.gender}</td></tr>"
					}
			%>
		</table>
	${footer}
	</body>
</html>