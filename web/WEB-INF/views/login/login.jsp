<%--
  Created by IntelliJ IDEA.
  User: bbu0704
  Date: 2022-04-30
  Time: 오후 9:37
  To change this template use File | Settings | File Templates.
--%>
<html>
<head>
    <title>${title}</title>
</head>
<body>
<h1>Login</h1>
<h2>${serverMessage}</h2>
<form action="${pageContext.request.contextPath}/login" method="post">
<%--    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>--%>
    <sec:csrfInput/>
    <div>
        <label>
            ID <input type="text" name="username"/>
        </label>
    </div>
    <div>
        <label>
            PW <input type="password" name="password"/>
        </label>
    </div>
    <div>
        <input type="submit" value="Login"/>
    </div>
</form>
</body>
</html>