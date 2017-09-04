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
        <link rel="icon" type="image/icon" href="${extViewConfig.favicon}" /> 
        
        <jsp:include page="extjslib.jsp"></jsp:include>
        
        <style>
            .x-html-editor-input textarea{white-space: pre !important;}
        </style>
        
        <!-- ############################ IMPORT LAYOUTS ################################ -->
        
        <c:set var="basePath" value="/vista" ></c:set>
        
        <!-- ############################ IMPORT MODELS ################################### -->
        
        <c:import url="${basePath}/${viewConfig.pathRef}/entityExtModel.htm"/>
        <c:forEach var="modelER" items="${modelsEntityRef}">
            <c:import url="${basePath}/${modelER}/entityExtModel.htm"/>
        </c:forEach>
        
        <!-- ############################ IMPORT STORES ################################### -->
        
        <c:import url="${basePath}/${viewConfig.pathRef}/entityExtStore.htm"/>
        <c:forEach var="modelER" items="${modelsEntityRef}">
            <c:import url="${basePath}/${modelER}/entityExtStore.htm">
                <c:param name="restSession" value="${viewConfig.restSession}"/>
            </c:import>
        </c:forEach>
        
        <!-- ############################ IMPORT VIEWS ################################### -->
        
        <c:import url="${basePath}/${viewConfig.pathRef}/entityExtView.htm">
             <c:param name="typeView" value="Parent"/>
        </c:import>
        <c:forEach var="viewsChildER" items="${viewsChildEntityRef}">
            <c:import url="${basePath}/${viewsChildER}/entityExtView.htm">
                <c:param name="typeView" value="Child"/>
            </c:import>
        </c:forEach>
        
        <!-- ############################ IMPORT CONTROLLERS ################################### -->
        
        <c:import url="${basePath}/${viewConfig.pathRef}/entityExtController.htm">
            <c:param name="typeController" value="Parent"/>
        </c:import>
        <c:forEach var="controllerChildER" items="${viewsChildEntityRef}">
            <c:import url="${basePath}/${controllerChildER}/entityExtController.htm">
                <c:param name="typeController" value="Child"/>
            </c:import>
        </c:forEach>
        
        <!-- ############################ IMPORT INTERFACES ################################### -->
        
        <c:forEach var="interfacesER" items="${interfacesEntityRef}">
            <c:import url="${basePath}/${interfacesER}/entityExtInterfaces.htm"/>
        </c:forEach>
        
        <c:forEach var="interfacesChildER" items="${interfacesChildEntityRef}">
            <c:import url="${basePath}/${interfacesChildER}/entityExtInterfaces.htm"/>
        </c:forEach>
        
        <!-- ############################ IMPORT BASE ELEMENTES ################################### -->
        
        <c:import url="${basePath}/${viewConfig.pathRef}/entityViewportExtView.htm"/>
        
        <c:import url="${basePath}/${viewConfig.pathRef}/entityExtInit.htm"/>
        
        <!-- ############################ IMPORT COMPONENTS ################################### -->
        
        <jsp:include page="/WEB-INF/views/scripts/components/CommonExtView.jsp" />
        
        <!-- ############################ IMPORT CONFIG ################################### -->
        
        <jsp:include page="/WEB-INF/views/scripts/config/MVCExtController.jsp" />
        
        
        <script src="<%=request.getContextPath()%>/js/util/Util.js"></script>
        <script type="text/javascript" src="http://maps.google.com/maps?file=api&amp;v=3&amp;key=AIzaSyD_IP-Js3_ETbJ9psH605u-4iqZihp_-Jg&sensor=true"></script>
        <script src="<%=request.getContextPath()%>/js/util/GoogleMaps.js"></script>
        
        <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/bootstrap.min2.css"/>
        <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/navegador.css">
        <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/gridTemplateStyles.css">
        
    </head>
    <body>
        <jsp:include page="header.jsp"></jsp:include>
    </body>
</html>
