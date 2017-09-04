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
                    read: Ext.context+'/rest/${entityRefLogProcess}/find.htm',
                    create: Ext.context+'/rest/${entityRefLogProcess}/create.htm',
                    update: Ext.context+'/rest/${entityRefLogProcess}/update.htm',
                    destroy: Ext.context+'/rest/${entityRefLogProcess}/delete.htm'
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
                    var gridComponent= null;
                    if(this.gridContainer){
                        gridComponent= this.gridContainer.child('#grid'+modelName);
                        gridComponent.getSelectionModel().deselectAll();
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
            formContainer:null,
            gridContainer:null
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
            url: Ext.context+"/rest/${entityRefLogProcess}/find.htm",
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
    
    Instance.doProcess= function(processName, data, func){
        Ext.MessageBox.show({
            msg: 'Ejecutando...',
            width:200,
            wait:true,
            waitConfig: {interval:200}
        });
        Ext.Ajax.request({
            url: Ext.context+"/rest/${entityRef}/doProcess.htm",
            method: "POST",
            headers: {
                'Content-Type' : 'application/json'
            },
            jsonData: {'processName': processName, 'data': util.remakeJSONObject(data)},
            success: function(response){
                Ext.MessageBox.hide();
                var responseDataFormat= response.getAllResponseHeaders()['response-data-format'];
                func(processName, response.responseText, responseDataFormat);
            },
            failure: function(response){
                commonExtView.processFailure(response);
            }
        });
    };
    
    Instance.load= function(idEntity, func){
        Ext.MessageBox.show({
            msg: 'Cargando...',
            width:200,
            wait:true,
            waitConfig: {interval:200}
        });
        Ext.Ajax.request({
            url: Ext.context+"/rest/${entityRefLogProcess}/load.htm",
            method: "GET",
            params: 'data='+encodeURIComponent('{"id":'+idEntity+'}'),
            success: function(response){
                var responseText= Ext.decode(response.responseText);
                func(responseText.data);
                Ext.MessageBox.hide();
            },
            failure: function(response){
                commonExtView.processFailure(response);
            }
        });
    };

}
</script>