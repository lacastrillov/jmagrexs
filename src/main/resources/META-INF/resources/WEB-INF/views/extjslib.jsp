<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ExtJSLib4" value="${pageContext.request.scheme}://${pageContext.request.serverName}:8080/ext-4.2.1" />
<c:set var="ExtJSLib6" value="${pageContext.request.scheme}://${pageContext.request.serverName}:8080/ext-6.2.0/build" />
<c:if test="${extViewConfig.extJsVersion==4}">
    <script type="text/javascript">
        var ExtJSVersion=4;
        var ExtJSLib="${ExtJSLib4}";
    </script>
    <script src="${ExtJSLib4}/examples/shared/include-ext.js"></script>
</c:if>
<c:if test="${extViewConfig.extJsVersion==6}">
    <script type="text/javascript">
        var ExtJSVersion=6;
        var ExtJSLib="${ExtJSLib6}";
    </script>
    <script src="${ExtJSLib6}/examples/classic/shared/include-ext.js"></script>
    <!--<script src="${ExtJSLib6}/examples/classic/shared/options-toolbar.js"></script>-->
</c:if>