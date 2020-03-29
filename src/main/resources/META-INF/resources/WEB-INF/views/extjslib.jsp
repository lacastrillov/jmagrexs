<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:useBean id="extJSLibs" class="java.util.HashMap" scope="request"/>
<c:set var="libDomain" value="http://${serverDomain.serverName}:8080" />
<c:if test="${pageContext.request.scheme eq 'https'}">
    <c:set var="libDomain" value="https://${serverDomain.serverName}:8082" />
</c:if>
<c:set target="${extJSLibs}" property="4" value="${libDomain}/ext-4.2.1"/>
<c:set target="${extJSLibs}" property="6" value="${libDomain}/ext-6.2.0/build"/>
<c:set target="${extJSLibs}" property="7" value="https://examples.sencha.com/extjs/7.1.0"/>
<c:choose>
<c:when test = "${extViewConfig.extJsLib==''}">
    <c:set var="extJSLib" value="${extJSLibs[extViewConfig.extJsVersion]}" />
</c:when>
<c:otherwise>
    <c:set var="extJSLib" value="${extViewConfig.extJsLib}" />
</c:otherwise>
</c:choose>
    <script type="text/javascript">
        var ExtJSVersion=${extViewConfig.extJsVersion};
        var ExtJSLib="${extJSLib}";
    </script>
<c:choose>
<c:when test = "${extViewConfig.extJsVersion==4}">
    <script src="${extJSLib}/examples/shared/include-ext.js"></script>
</c:when>
<c:otherwise>
    <script src="${extJSLib}/examples/classic/shared/include-ext.js"></script>
    <script src="${extJSLib}/examples/classic/shared/options-toolbar.js"></script>
</c:otherwise>
</c:choose>
    <!-- ### JMAGREXS BUILD CODE ::: ${extViewConfig.jmagrexsBuildCode} ### -->