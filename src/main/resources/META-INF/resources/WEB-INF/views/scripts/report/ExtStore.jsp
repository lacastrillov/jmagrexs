<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script>

function ${reportName}ExtStore(){
    
    var Instance = this;
    
    var commonExtView= new CommonExtView();
    
    var baseAction= "";
    <c:if test="${restSession}">
    baseAction= "session_";
    </c:if>
    
    Instance.getStore= function(modelName){
        var store = Ext.create('Ext.data.Store', {
            model: modelName,
            autoLoad: false,
            pageSize: ${reportConfig.maxResultsPerPage},
            remoteSort: true,
            proxy: {
                type: 'ajax',
                batchActions: false,
                simpleSortMode: true,
                actionMethods : {
                    read   : 'GET'
                },
                api: {
                    read: Ext.context+'/rest/${entityRef}/'+baseAction+'report/${reportName}.htm'
                },
                reader: {
                    type: 'json',
                    successProperty: 'success',
                    root: 'data',
                    totalProperty: 'totalCount',
                    messageProperty: 'message'
                },
                extraParams: {
                    filter: null,
                    dtoName: '${reportConfig.dtoName}'
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
                }
            },
            sorters: [
                <c:if test="${not empty reportConfig.defaultOrderBy && not empty reportConfig.defaultOrderDir}">
                {
                    property: '${reportConfig.defaultOrderBy}',
                    direction: '${reportConfig.defaultOrderDir}'
                }
                </c:if>
            ],
            gridComponent:null
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
    
    <c:if test="${reportConfig.activeGridTemplate}">
    Instance.getTemplateStore= function(modelName){
        var store = Ext.create('Ext.data.Store', {
            model: modelName,
            autoLoad: false,
            pageSize: ${reportConfig.maxResultsPerPage},
            remoteSort: true,
            proxy: {
                type: 'ajax',
                batchActions: false,
                simpleSortMode: true,
                actionMethods : {
                    read   : 'GET'
                },
                api: {
                    read: Ext.context+'/rest/${entityRef}/'+baseAction+'report/${reportName}.htm'
                },
                reader: {
                    type: 'json',
                    successProperty: 'success',
                    root: 'data',
                    totalProperty: 'totalCount',
                    messageProperty: 'message'
                },
                extraParams: {
                    filter: null,
                    dtoName: '${reportConfig.dtoName}',
                    templateName: '${reportConfig.gridTemplate.templateName}',
                    numColumns: ${reportConfig.gridTemplate.numColumns}
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
                }
            },
            sorters: [
                <c:if test="${not empty reportConfig.defaultOrderBy && not empty reportConfig.defaultOrderDir}">
                {
                    property: '${reportConfig.defaultOrderBy}',
                    direction: '${reportConfig.defaultOrderDir}'
                }
                </c:if>
            ],
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
    </c:if>

    Instance.load= function(idRecord, func){
        Ext.MessageBox.show({
            msg: 'Cargando...',
            width:200,
            wait:true,
            waitConfig: {interval:200}
        });
        Ext.Ajax.request({
            url:  Ext.context+'/rest/${entityRef}/'+baseAction+'report/${reportName}.htm',
            method: "GET",
            params: 'filter='+encodeURIComponent('{eq:{"${reportConfig.idColumnName}":"'+idRecord+'"}}')+'&dtoName=${reportConfig.dtoName}',
            success: function(response){
                Ext.MessageBox.hide();
                var responseText= Ext.decode(response.responseText);
                func(responseText.data[0]);
            },
            failure: function(response){
                commonExtView.processFailure(response);
            }
        });
    };
    
    Instance.doProcess= function(mainProcessRef, processName, data, func){
        Ext.MessageBox.show({
            msg: 'Ejecutando...',
            width:200,
            wait:true,
            waitConfig: {interval:200}
        });
        Ext.Ajax.request({
            url: Ext.context+"/rest/"+mainProcessRef+"/doProcess.htm",
            method: "POST",
            headers: {
                'Content-Type' : 'application/json'
            },
            jsonData: {'processName': processName, 'data': util.remakeJSONObject(data)},
            success: function(response){
                Ext.MessageBox.hide();
                func(response.responseText);
            },
            failure: function(response){
                commonExtView.processFailure(response);
            }
        });
    };
    
}
</script>