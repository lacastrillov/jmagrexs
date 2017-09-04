<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script>

function ${reportName}ExtModel(){
    
    var Instance = this;
    
    
    Instance.defineModel= function(modelName){
        Ext.define(modelName, {
            extend: 'Ext.data.Model',
            fields: ${jsonModel}
        });
    };
    
    <c:if test="${reportConfig.activeGridTemplate}">
    Instance.defineTemplateModel= function(modelName){
        Ext.define(modelName, {
            extend: 'Ext.data.Model',
            fields: ${jsonTemplateModel}
        });
    };
    </c:if>
    
}
</script>