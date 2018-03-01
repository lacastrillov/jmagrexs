<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script>

function ${entityName}ExtStore(){
    
    var Instance = this;
    
    var commonExtView= new CommonExtView();
    
    
    Instance.getStore= function(modelName){
        var store = Ext.create('Ext.data.Store', {
            model: modelName,
            autoLoad: false,
            autoSync: ${viewConfig.defaultAutoSave},
            pageSize: ${viewConfig.maxResultsPerPage},
            remoteSort: true,
            proxy: {
                type: 'ajax',
                batchActions: false,
                simpleSortMode: true,
                actionMethods : {
                    create : 'POST',
                    read   : 'GET',
                    update : 'POST',
                    destroy: 'GET'
                },
                api: {
                    read: Ext.context+'/rest/${entityRef}/${tableName}/find.htm',
                    create: Ext.context+'/rest/${entityRef}/${tableName}/create.htm',
                    update: Ext.context+'/rest/${entityRef}/${tableName}/update.htm',
                    destroy: Ext.context+'/rest/${entityRef}/${tableName}/delete.htm'
                },
                reader: {
                    type: 'json',
                    successProperty: 'success',
                    root: 'data',
                    totalProperty: 'totalCount',
                    messageProperty: 'message'
                },
                writer: {
                    type: 'json',
                    //encode: true,
                    writeAllFields: false
                    //root: 'data'
                },
                extraParams: {
                    filter: null,
                    idEntity: null
                },
                listeners: {
                    exception: function(proxy, response, operation){
                        var errorMsg= operation.getError();
                        if(typeof errorMsg === "object"){
                            commonExtView.processFailure(errorMsg);
                        }else{
                            commonExtView.showErrorMessage(errorMsg);
                        }
                    }
                }
            },
            listeners: {
                load: function() {
                    if(this.gridComponent!==null){
                        this.gridComponent.getSelectionModel().deselectAll();
                    }
                },
                write: function(proxy, operation){
                    if (operation.action === 'destroy') {
                        Ext.MessageBox.alert('Status', operation.resultSet.message);
                    }
                }
            },
            sorters: [{
                property: '${viewConfig.defaultOrderBy}',
                direction: '${viewConfig.defaultOrderDir}'
            }],
            formComponent: null,
            gridComponent: null
        });
        store.getOrderProperty= function(){
            if(ExtJSVersion===4){
                return store.sorters.items[0]["property"];
            }else{
                return store.getSorters().items[0]["_id"];
            }
        };
        store.getOrderDir= function(){
            if(ExtJSVersion===4){
                return store.sorters.items[0]["direction"];
            }else{
                return store.getSorters().items[0]["_direction"];
            }
        };
        store.sortBy= function(property, direction){
            if(ExtJSVersion===4){
                store.sorters.items[0]["property"]= property;
                store.sorters.items[0]["direction"]= direction;
            }else{
                store.getSorters().clear();
                store.setSorters([{property:property, direction:direction}]);
            }
        };
        
        return store;
    };

    Instance.find= function(filter, params, func){
        Ext.Ajax.request({
            url: Ext.context+"/rest/${entityRef}/${tableName}/find.htm",
            method: "GET",
            params: "filter="+encodeURIComponent(filter) + params,
            success: function(response){
                var responseText= Ext.decode(response.responseText);
                func(responseText);
            },
            failure: function(response){
                commonExtView.processFailure(response);
            }
        });
    };
    
    Instance.save= function(operation, data, func){
        Ext.MessageBox.show({
            msg: 'Guardando...',
            width:200,
            wait:true,
            waitConfig: {interval:200}
        });
        Ext.Ajax.request({
            url: Ext.context+"/rest/${entityRef}/${tableName}/"+operation+".htm",
            method: "POST",
            params: "data="+encodeURIComponent(data),
            success: function(response){
                Ext.MessageBox.hide();
                var responseText= Ext.decode(response.responseText);
                func(responseText);
            },
            failure: function(response){
                commonExtView.processFailure(response);
            }
        });
    };
    
    Instance.load= function(idEntity, func){
        Ext.Ajax.request({
            url: Ext.context+"/rest/${entityRef}/${tableName}/load.htm",
            method: "GET",
            params: 'idEntity='+idEntity,
            success: function(response){
                var responseText= Ext.decode(response.responseText);
                func(responseText.data);
            },
            failure: function(response){
                commonExtView.processFailure(response);
            }
        });
    };
    
    Instance.upload= function(form, idEntity, func){
        form.submit({
            url: Ext.context+"/rest/${entityRef}/${tableName}/diskupload/"+idEntity+".htm",
            waitMsg: 'Subiendo archivo...',
            success: function(form, action) {
                func(action.result);
            },
            failure: function(response){
                commonExtView.processFailure(response);
            }
        });
    };
    
    Instance.import= function(form, typeReport, func){
        form.submit({
            url: Ext.context+'/rest/${entityRef}/${tableName}/import/'+typeReport+'.htm',
            waitMsg: 'Importando archivo...',
            success: function(form, action) {
                func(action.result);
            },
            failure: function(response){
                commonExtView.processFailure(response);
            }
        });
    };
    
    Instance.deleteByFilter= function(filter, func){
        Ext.MessageBox.show({
            msg: 'Eliminando...',
            width:200,
            wait:true,
            waitConfig: {interval:200}
        });
        Ext.Ajax.request({
            url: Ext.context+"/rest/${entityRef}/${tableName}/delete/byfilter.htm",
            method: "GET",
            params: "filter="+encodeURIComponent(filter),
            success: function(response){
                var responseText= Ext.decode(response.responseText);
                func(responseText);
                Ext.MessageBox.hide();
            },
            failure: function(response){
                commonExtView.processFailure(response);
            }
        });
    };

}
</script>