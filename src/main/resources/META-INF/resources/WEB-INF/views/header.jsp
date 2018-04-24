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
    var userAuthentication;
    try{
        userAuthentication = new UserAuthentication();
        userAuthentication.context= "${serverDomain.applicationContext}";
        userAuthentication.MODULES= ${serverDomain.modulesJson};
    }catch(e){
        console.error(e);
    }
</script>
<div id="headerHtml" style="display:none;">
    <a href="/"><img src="${serverDomain.applicationContext}${extViewConfig.favicon}" class="logoAdmin"></a>
    <h1>Administraci&oacute;n ${extViewConfig.appName}</h1>
    <sec:authentication var="user" property="principal" />
    <sec:authorize access="isAuthenticated()">
        <a class="logout" onclick="userAuthentication.logout()" href="javascript:void(0)">&nbsp;Cerrar sesi&oacute;n&nbsp;</a>
        <a class="home" href="${serverDomain.applicationContext}/account/home?redirect=user">&nbsp;Inicio&nbsp;</a>
        <p class="userSession"><b>${user.username}</b> - ${user.nombre} ${user.apellidos}</p>
    </sec:authorize>
</div>
