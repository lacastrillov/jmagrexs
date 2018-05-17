<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ExtJSLib4" value="${serverDomain.domain}:8080/ext-4.2.1" />
<c:set var="ExtJSLib6" value="${serverDomain.domain}:8080/ext-6.2.0/build" />
<c:if test="${extViewConfig.extJsVersion==4}">
    <c:choose>
        <c:when test = "${extViewConfig.extJsLib4==''}">
            <c:set var="ExtJSLib" value="${ExtJSLib4}" />
        </c:when>
        <c:otherwise>
            <c:set var="ExtJSLib" value="${extViewConfig.extJsLib4}" />
        </c:otherwise>
    </c:choose>
    <script type="text/javascript">
        var ExtJSVersion=4;
        var ExtJSLib="${ExtJSLib}";
    </script>
    <script src="${ExtJSLib}/examples/shared/include-ext.js"></script>
</c:if>
<c:if test="${extViewConfig.extJsVersion==6}">
    <c:choose>
        <c:when test = "${extViewConfig.extJsLib6==''}">
            <c:set var="ExtJSLib" value="${ExtJSLib6}" />
        </c:when>
        <c:otherwise>
            <c:set var="ExtJSLib" value="${extViewConfig.extJsLib6}" />
        </c:otherwise>
    </c:choose>
    <script type="text/javascript">
        var ExtJSVersion=6;
        var ExtJSLib="${ExtJSLib}";
    </script>
    <script src="${ExtJSLib}/examples/classic/shared/include-ext.js"></script>
    <!--<script src="${ExtJSLib}/examples/classic/shared/options-toolbar.js"></script>-->
</c:if>
    <script>
        Ext.context="${serverDomain.restContext}";
    </script>