<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script>

function ${entityName}ExtModel(){
    
    var Instance = this;
    
    
    Instance.defineModel= function(modelName){
        Ext.define(modelName, {
            extend: 'Ext.data.Model',
            fields: ${jsonModel},
            validations: ${jsonModelValidations}
        });
    };
    
    <c:if test="${viewConfig.activeGridTemplateAsParent || viewConfig.activeGridTemplateAsChild}">
    Instance.defineTemplateModel= function(modelName){
        Ext.define(modelName, {
            extend: 'Ext.data.Model',
            fields: ${jsonTemplateModel}
        });
    };
    </c:if>
    
}
</script>