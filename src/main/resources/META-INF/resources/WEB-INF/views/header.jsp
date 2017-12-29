<%-- 
    Document   : header.jsp
    Created on : 13/08/2017, 11:59:56 AM
    Author     : lacastrillov
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<script type="text/javascript">
    var navegadorExtInit= new EntityExtInit();
    var userAuthentication = new UserAuthentication();
</script>
<div id="headerHtml" style="display:none;">
    <a href="/"><img src="<%=request.getContextPath()%>${extViewConfig.favicon}" class="logoAdmin"></a>
    <h1>Administraci&oacute;n ${extViewConfig.appName}</h1>
    <sec:authentication var="user" property="principal" />
    <sec:authorize access="isAuthenticated()">
        <a class="logout" onclick="userAuthentication.logout()" href="javascript:void(0)">&nbsp;Cerrar sesi&oacute;n&nbsp;</a>
        <a class="home" href="/account/home?redirect=user">&nbsp;Inicio&nbsp;</a>
        <p class="userSession"><b>${user.username}</b> - ${user.nombre} ${user.apellidos}</p>
    </sec:authorize>
</div>
