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

        Instance.showListItems(formComponent);
    };
    
    Instance.formSavedResponse= function(result){
        Ext.MessageBox.alert('Status', result.message);
    };
    
    Instance.showListItems= function(formComponent){
        formComponent.query('.fieldset').forEach(function(c){
            if(c.itemTop!==undefined){
                var itemsGroup=Ext.getCmp(c.id);
                for(var i=1; i<Instance.MAX_LIST_ITEMS; i++){
                    var itemEntity=Ext.getCmp(c.id+'['+i+']');
                    var filled= false;
                    if(itemEntity.query){
                        itemEntity.query('.field').forEach(function(c){
                            var text=c.getValue();
                            if(text!==null && text!=="" && text!==false){
                                filled=true;
                                c.setDisabled(false);
                            }
                        });
                    }else{
                        var text=itemEntity.getValue();
                        if(text!==null && text!=="" && text!==false){
                            filled=true;
                        }
                    }
                    if(filled){
                        itemEntity.setVisible(true);
                        itemEntity.setDisabled(false);
                        itemsGroup.itemTop=i;
                    }else{
                        itemEntity.setVisible(false);
                        itemEntity.setDisabled(true);
                    }
                }
            }
        });
    };

    Instance.init();
}
</script>