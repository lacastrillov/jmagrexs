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
        <title>${reportConfig.pluralReportTitle} - Administraci&oacute;n ${extViewConfig.appName}</title>
        <link rel="icon" type="image/icon" href="${serverDomain.applicationContext}${extViewConfig.favicon}" /> 
        
        <jsp:include page="statics.jsp"></jsp:include>
        
        <jsp:include page="extjslib.jsp"></jsp:include>
        
        <!-- ############################ IMPORT LAYOUTS ################################ -->
        
        
        <!-- ############################ IMPORT MODELS ################################### -->
        <!--${serverDomain.adminPath}/${entityRef}/reportExtModel/${reportName}.htm-->
        
        <c:import url="${serverDomain.adminPath}/${entityRef}/reportExtModel/${reportName}.htm"/>
        <c:forEach var="childExtReport" items="${reportConfig.childExtReports}">
            <c:import url="${serverDomain.adminPath}/${childExtReport.key}/reportExtModel/${childExtReport.value}.htm"/>
        </c:forEach>
        
        <!-- ############################ IMPORT STORES ################################### -->
        
        <c:import url="${serverDomain.adminPath}/${entityRef}/reportExtStore/${reportName}.htm"/>
        <c:forEach var="childExtReport" items="${reportConfig.childExtReports}">
            <c:import url="${serverDomain.adminPath}/${childExtReport.key}/reportExtStore/${childExtReport.value}.htm">
                <c:param name="restSession" value="${reportConfig.restSession}"/>
            </c:import>
        </c:forEach>
        
        <!-- ############################ IMPORT VIEWS ################################### -->
        
        <c:import url="${serverDomain.adminPath}/${entityRef}/reportExtView/${reportName}.htm">
            <c:param name="typeView" value="Parent"/>
        </c:import>
        <c:forEach var="childExtReport" items="${reportConfig.childExtReports}">
            <c:import url="${serverDomain.adminPath}/${childExtReport.key}/reportExtView/${childExtReport.value}.htm">
                <c:param name="typeView" value="Child"/>
            </c:import>
        </c:forEach>
        
        <c:import url="${serverDomain.adminPath}/${entityRef}/reportExtController/${reportName}.htm">
            <c:param name="typeController" value="Parent"/>
        </c:import>
        <c:forEach var="childExtReport" items="${reportConfig.childExtReports}">
            <c:import url="${serverDomain.adminPath}/${childExtReport.key}/reportExtController/${childExtReport.value}.htm">
                <c:param name="typeController" value="Child"/>
            </c:import>
        </c:forEach>
        
        <c:import url="${serverDomain.adminPath}/${entityRef}/reportExtViewport/${reportName}.htm"/>
        
        <c:import url="${serverDomain.adminPath}/${entityRef}/reportExtInit/${reportName}.htm"/>
        
        <!-- ############################ IMPORT COMPONENTS ################################### -->
        
        <jsp:include page="/WEB-INF/views/scripts/components/CommonExtView.jsp" />
        
        <!-- ############################ IMPORT CONFIG ################################### -->
        
        <jsp:include page="/WEB-INF/views/scripts/config/MVCExtController.jsp" />
        
    </head>
    <body>
        <jsp:include page="header.jsp"></jsp:include>
    </body>
</html>
