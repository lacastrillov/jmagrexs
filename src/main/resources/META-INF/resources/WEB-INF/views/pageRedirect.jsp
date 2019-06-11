<%-- 
    Document   : redirect
    Created on : 27/02/2018, 11:19:39 PM
    Author     : lacastrillov
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Redirigiendo...</title>
        <script type="text/javascript" src="<%=request.getContextPath()%>/libjs/jquery/jquery-3.1.0.min.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/libjs/util/Util.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/account/UserAuthentication"></script>
        <script>
            var userAuthentication = new UserAuthentication();
            userAuthentication.replicateAuthentication(function(){
                location.href="${page}";
            });
        </script>
    </head>
    <body>
        <p>Redirigiendo...</p>
    </body>
</html>
