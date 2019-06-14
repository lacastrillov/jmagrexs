<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<script>

function ${entityName}ExtController(parentExtController, parentExtView){
    
    var Instance= this;
    
    Instance.id= "/${entityRef}";
    
    Instance.modelName="${entityName}Model";
    
    Instance.services= {};
    
    var util= new Util();
    
    Instance.MAX_LIST_ITEMS= 20;
    
    // VIEWS *******************************************
    
    Instance.entityExtView= new ${entityName}ExtView(Instance, null);
    
    //*******************************************************
    
    
    Instance.init= function(){
        Instance.entityRef= "${entityRef}";
        Instance.typeController= "";
        mvcExt.mappingController(Instance.id, Instance);
        Instance.initFilter();
        Instance.configObjects=[];
        <c:forEach var="configurationObjectName" items="${nameConfigurationObjects}">
        Instance.configObjects.push('${configurationObjectName.key}');
        </c:forEach>
        if(util.getParameter(document.URL,"configObj")===null && Instance.configObjects.length>0){
            mvcExt.navigate("?configObj="+Instance.configObjects[0]);
        }
    };
    
    Instance.initFilter= function(){
        Instance.filter={};
    };
    
    Instance.services.index= function(request){
        var configObj= util.getParameter(request,"configObj");
        Instance.loadFormData(configObj);
    };
    
    Instance.loadFormData= function(configObj){
        if(configObj!==null){
            Instance.entityExtView.entityExtStore.loadConfig(configObj, function(data){
                //Show Process
                Ext.getCmp('content-configurationObjects').layout.setActiveItem('formContainer'+configObj);
                
                //Populate Form
                Instance.populateForm(configObj, data);
            });
        }
    };
    
    Instance.populateForm= function(configObj, data){
        var record= Ext.create(configObj+"Model");
        record.data= util.unremakeJSONObject(data);
        var formComponent= Ext.getCmp('formContainer'+configObj).child('#form'+configObj);
        formComponent.setActiveRecord(record);

        Instance.entityExtView.commonExtView.showListItems(formComponent);
    };
    
    Instance.formSavedResponse= function(configurationObjectRef, message, result){
        Ext.MessageBox.alert('Status', message);
        if(result.success){
            Instance.populateForm(configurationObjectRef, result.data);
        }
    };
    

    Instance.init();
}
</script>