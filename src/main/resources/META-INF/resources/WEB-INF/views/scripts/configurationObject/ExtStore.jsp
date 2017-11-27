<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script>

function ${entityName}ExtStore(){
    
    var Instance = this;
    
    var commonExtView= new CommonExtView();
    
    
    Instance.saveConfig= function(configurationObjectRef, data, func){
        Ext.MessageBox.show({
            msg: 'Guardando...',
            width:200,
            wait:true,
            waitConfig: {interval:200}
        });
        Ext.Ajax.request({
            url: Ext.context+"/rest/${entityRef}/saveConfig.htm",
            method: "POST",
            headers: {
                'Content-Type' : 'application/json'
            },
            jsonData: {'configurationObjectRef': configurationObjectRef, 'data': util.remakeJSONObject(data)},
            success: function(response){
                Ext.MessageBox.hide();
                var result= Ext.decode(response.responseText);
                func(configurationObjectRef, result);
            },
            failure: function(response){
                commonExtView.processFailure(response);
            }
        });
    };
    
    Instance.loadConfig= function(configurationObjectRef, func){
        Ext.MessageBox.show({
            msg: 'Cargando...',
            width:200,
            wait:true,
            waitConfig: {interval:200}
        });
        Ext.Ajax.request({
            url: Ext.context+"/rest/${entityRef}/loadConfig/"+configurationObjectRef+".htm",
            method: "GET",
            success: function(response){
                var result= Ext.decode(response.responseText);
                func(result.data);
                Ext.MessageBox.hide();
            },
            failure: function(response){
                commonExtView.processFailure(response);
            }
        });
    };
    
    Instance.upload= function(form, configurationObjectRef, func){
        form.submit({
            url: Ext.context+'/rest/${entityRef}/diskupload/'+configurationObjectRef+'.htm',
            waitMsg: 'Subiendo archivo...',
            success: function(form, action) {
                func(action.result);
            },
            failure: function(response){
                commonExtView.processFailure(response);
            }
        });
    };

}
</script>