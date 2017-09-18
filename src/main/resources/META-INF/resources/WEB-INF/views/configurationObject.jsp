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
        <title>${viewConfig.mainConfigurationTitle} - Administraci&oacute;n ${extViewConfig.appName}</title>
        <link rel="icon" type="image/icon" href="${extViewConfig.favicon}" /> 
        
        <jsp:include page="extjslib.jsp"></jsp:include>
        
        <style>
            .x-html-editor-input textarea{white-space: pre !important;}
            .x-tree-icon-leaf {background-image: url("http://jsonviewer.stack.hu/blue.gif") !important; }
            .x-tree-icon-parent, .x-tree-icon-parent-expanded {background-image: url("http://jsonviewer.stack.hu/object.gif") !important;background-repeat:no-repeat;}
        </style>
        
        <!-- ############################ IMPORT LAYOUTS ################################ -->
        
        <c:set var="basePath" value="/vista" ></c:set>
        
        <!-- ############################ IMPORT MODELS ################################### -->
        
        <c:forEach var="modelER" items="${modelsEntityRef}">
            <c:import url="${basePath}/${modelER}/ExtModel.htm"/>
        </c:forEach>
        
        <!-- ############################ IMPORT STORES ################################### -->
        
        <c:forEach var="modelER" items="${modelsEntityRef}">
            <c:import url="${basePath}/${modelER}/ExtStore.htm"/>
        </c:forEach>
        
        <!-- ############################ IMPORT VIEWS ################################### -->
        
        <c:import url="${basePath}/${entityRef}/ExtView.htm" />
        
        <!-- ############################ IMPORT CONTROLLERS ################################### -->
        
        <c:import url="${basePath}/${entityRef}/ExtController.htm" />
        
        <!-- ############################ IMPORT BASE ELEMENTES ################################### -->
        
        <c:import url="${basePath}/${entityRef}/ExtViewport.htm"/>
        
        <c:import url="${basePath}/${entityRef}/ExtInit.htm"/>
        
        <!-- ############################ IMPORT COMPONENTS ################################### -->
        
        <jsp:include page="/WEB-INF/views/scripts/components/CommonExtView.jsp" />
        
        <!-- ############################ IMPORT CONFIG ################################### -->
        
        <jsp:include page="/WEB-INF/views/scripts/config/MVCExtController.jsp" />
        
        
        <script src="<%=request.getContextPath()%>/js/util/Util.js"></script>
        <script type="text/javascript" src=http://maps.google.com/maps?file=api&amp;v=3&amp;key=AIzaSyD_IP-Js3_ETbJ9psH605u-4iqZihp_-Jg&sensor=true"></script>
        <script src="<%=request.getContextPath()%>/js/util/GoogleMaps.js"></script>
        <script src="<%=request.getContextPath()%>/js/util/vkbeautify.0.99.00.beta.js"></script>
        
        <link rel="stylesheet" type="text/css" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"/>
        <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/navegador.css">
        <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/gridTemplateStyles.css">
        
    </head>
    <body>
        <jsp:include page="header.jsp"></jsp:include>
    </body>
</html>
