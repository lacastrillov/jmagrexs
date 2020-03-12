<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:useBean id="extJSLibs" class="java.util.HashMap" scope="request"/>
<c:set target="${extJSLibs}" property="4" value="${serverDomain.domain}:8080/ext-4.2.1"/>
<c:set target="${extJSLibs}" property="6" value="${serverDomain.domain}:8080/ext-6.2.0/build"/>
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