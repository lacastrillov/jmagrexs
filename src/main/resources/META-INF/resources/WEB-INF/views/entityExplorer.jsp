<%-- 
    Document   : navegador
    Created on : 21/11/2013, 12:06:14 AM
    Author     : lacastrillov
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>${viewConfig.pluralEntityTitle} - Administraci&oacute;n ${extViewConfig.appName}</title>
        <link rel="icon" type="image/icon" href="${serverDomain.applicationContext}${extViewConfig.favicon}" /> 
        
        <jsp:include page="statics.jsp"></jsp:include>
        
        <jsp:include page="extjslib.jsp"></jsp:include>
        
        <style>
            .x-html-editor-input textarea{white-space: pre !important;}
        </style>
        
        <!-- ############################ IMPORT LAYOUTS ################################ -->
        
        
        <!-- ############################ IMPORT MODELS ################################### -->
        
        <c:forEach var="modelER" items="${modelsEntityRef}">
            <c:import url="${serverDomain.adminContext}/${modelER}/ExtModel.htm"/>
        </c:forEach>
        
        <!-- ############################ IMPORT STORES ################################### -->
        
        <c:forEach var="modelER" items="${modelsEntityRef}">
            <c:import url="${serverDomain.adminContext}/${modelER}/ExtStore.htm"/>
        </c:forEach>
        
        <!-- ############################ IMPORT VIEWS ################################### -->
        
        <c:import url="${serverDomain.adminContext}/${entityRef}/ExtView.htm">
             <c:param name="typeView" value="Parent"/>
        </c:import>
        <c:forEach var="viewsChildER" items="${viewsChildEntityRef}">
            <c:import url="${serverDomain.adminContext}/${viewsChildER}/ExtView.htm">
                <c:param name="typeView" value="Child"/>
            </c:import>
        </c:forEach>
        
        <!-- ############################ IMPORT CONTROLLERS ################################### -->
        
        <c:import url="${serverDomain.adminContext}/${entityRef}/ExtController.htm">
            <c:param name="typeController" value="Parent"/>
        </c:import>
        <c:forEach var="controllerChildER" items="${viewsChildEntityRef}">
            <c:import url="${serverDomain.adminContext}/${controllerChildER}/ExtController.htm">
                <c:param name="typeController" value="Child"/>
            </c:import>
        </c:forEach>
        
        <!-- ############################ IMPORT INTERFACES ################################### -->
        
        <c:forEach var="interfacesER" items="${interfacesEntityRef}">
            <c:import url="${serverDomain.adminContext}/${interfacesER}/ExtInterfaces.htm"/>
        </c:forEach>
        
        <c:forEach var="interfacesChildER" items="${interfacesChildEntityRef}">
            <c:import url="${serverDomain.adminContext}/${interfacesChildER}/ExtInterfaces.htm"/>
        </c:forEach>
        
        <!-- ############################ IMPORT BASE ELEMENTES ################################### -->
        
        <c:import url="${serverDomain.adminContext}/${entityRef}/ExtViewport.htm"/>
        
        <c:import url="${serverDomain.adminContext}/${entityRef}/ExtInit.htm"/>
        
        <!-- ############################ IMPORT COMPONENTS ################################### -->
        
        <jsp:include page="/WEB-INF/views/scripts/components/CommonExtView.jsp" />
        
        <!-- ############################ IMPORT CONFIG ################################### -->
        
        <jsp:include page="/WEB-INF/views/scripts/config/MVCExtController.jsp" />
        
    </head>
    <body>
        <jsp:include page="header.jsp"></jsp:include>
    </body>
</html>