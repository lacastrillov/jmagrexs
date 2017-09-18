<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<script>

function ${entityName}ExtController(parentExtController, parentExtView){
    
    var Instance= this;
    
    Instance.id= "/${entityRef}";
    
    Instance.modelName="${entityName}Model";
    
    Instance.services= {};
    
    var util= new Util();
    
    // VIEWS *******************************************
    
    Instance.entityExtView= new ${entityName}ExtView(Instance, null);
    
    //*******************************************************
    
    
    Instance.init= function(){
        Instance.entityRef= "${entityRef}";
        Instance.typeController= "";
        mvcExt.mappingController(Instance.id, Instance);
        Instance.initFilter();
    };
    
    Instance.initFilter= function(){
        Instance.filter={};
    };
    
    Instance.services.index= function(request){
        var configObj= util.getParameter(request,"configObj");
        Instance.loadFormData(configObj);
    };
    
    Instance.loadFormData= function(configObj){
        if(configObj!==""){
            Instance.entityExtView.entityExtStore.loadConfig(configObj, function(data){
                //Show Process
                Ext.getCmp('content-configurationObjects').layout.setActiveItem('form-'+configObj);
                
                //Populate Form
                var record= Ext.create(configObj+"Model");
                record.data= util.unremakeJSONObject(data);
                var formComponent= Ext.getCmp('form-'+configObj).child('#form'+configObj+'Item');
                formComponent.setActiveRecord(record);
            });
        }
    };
    
    Instance.formSavedResponse= function(result){
        Ext.MessageBox.alert('Status', result.message);
    };

    Instance.init();
}
</script>