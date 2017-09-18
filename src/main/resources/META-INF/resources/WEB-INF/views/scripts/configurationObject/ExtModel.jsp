<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script>

function ${entityName}ExtModel(){
    
    var Instance = this;
    
    <c:forEach var="configurationObjectName" items="${nameConfigurationObjects}">
    Instance.define${configurationObjectName.key}Model= function(modelName){
        Ext.define(modelName, {
            extend: 'Ext.data.Model',
            fields: ${jsonModelMap[configurationObjectName.key]},
            validations: ${jsonModelValidationsMap[configurationObjectName.key]}
        });
    };
    </c:forEach>
    
}
</script>