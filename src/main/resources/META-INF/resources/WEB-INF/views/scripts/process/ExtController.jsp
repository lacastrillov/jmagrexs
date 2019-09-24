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
        Instance.typeController= "${typeController}";
        Instance.reloadGrid= false;
        mvcExt.mappingController(Instance.id, Instance);
        Instance.initFilter();
    };
    
    Instance.initFilter= function(){
        Instance.filter={
            eq:{"mainProcessRef":"${viewConfig.mainProcessRef}"}
        };
    };
    
    Instance.services.index= function(request){
        var activeTab= util.getParameter(request,"tab");
        var filter= util.getParameter(request,"filter");
        var id= util.getParameter(request,"id");
        
        if(activeTab!==null){
            Instance.entityExtView.tabsContainer.setActiveTab(Number(activeTab));
        }/*else{
            Instance.entityExtView.tabsContainer.setActiveTab(0);
        }*/
        
        if(filter!==null){
            Instance.initFilter();
            var currentFilter= JSON.parse(filter);
            for (var key in currentFilter) {
                if(Instance.filter[key]!==currentFilter[key]){
                    Instance.filter[key]= currentFilter[key];
                    Instance.reloadGrid= true;
                }
            }
        }
        
        <c:forEach var="associatedER" items="${interfacesEntityRef}">
            <c:set var="associatedEntityName" value="${fn:toUpperCase(fn:substring(associatedER, 0, 1))}${fn:substring(associatedER, 1,fn:length(associatedER))}"></c:set>
        if(Instance.filter.eq.${associatedER}!==undefined && Instance.filter.eq.${associatedER}!==''){
            Instance.entityExtView.${associatedER}ExtInterfaces.entityExtStore.load(Instance.filter.eq.${associatedER}, Instance.entityExtView.${associatedER}ExtInterfaces.addLevel);
        }else{
            Instance.entityExtView.${associatedER}ExtInterfaces.addLevel(null);
        }
        </c:forEach>
        
        if(activeTab==="1" && (Instance.entityExtView.store.totalCount===undefined || Instance.reloadGrid)){
            Instance.loadGridData();
        }
        if(activeTab==="0"){
            Instance.loadFormData(id);
        }
    };
    
    Instance.loadGridData= function(){
        Instance.entityExtView.setFilterStore(JSON.stringify(Instance.filter));
        Instance.entityExtView.reloadPageStore(1);
        Instance.reloadGrid= false;
    };
    
    Instance.loadFormData= function(id){
        if(id!==null){
            Instance.entityExtView.entityExtStore.load(id, function(data){
                if(Ext.getCmp('formContainer'+data.processName)!==undefined){
                    
                    //Show Process
                    Ext.getCmp('content-processes').layout.setActiveItem('formContainer'+data.processName);

                    //Populate Form
                    Instance.populateForm(data.processName, data.dataIn);

                    //Populate tree result
                    Instance.formSavedResponse(data.processName, data.dataOut, data.outputDataFormat);
                    
                }else{
                    setTimeout(function(){
                        Ext.MessageBox.show({
                            title: 'ERROR',
                            msg: 'Proceso '+data.processName+' no encontrado',
                            icon: Ext.MessageBox.ERROR,
                            buttons: Ext.Msg.OK
                        });
                    },50);
                }
            });
        }
    };
    
    Instance.populateForm= function(processName, dataIn){
        var record= Ext.create(processName+"Model");
        record.data= util.unremakeJSONObject(JSON.parse(dataIn));
        var formComponent= Ext.getCmp('formContainer'+processName).child('#form'+processName);
        formComponent.setActiveRecord(record);

        Instance.entityExtView.commonExtView.showListItems(formComponent);
    };
    
    Instance.formSavedResponse= function(processName, dataOut, outputDataFormat){
        var treeTabs = Ext.getCmp('tree-tabs-'+processName);
        var divPanel = Ext.getCmp('div-result-'+processName);
        treeTabs.hide();
        divPanel.hide();
        if(outputDataFormat==='JSON'){
            treeTabs.show();
            var treePanel = Ext.getCmp('tree-result-'+processName);
            var jsonPanel = Ext.getCmp('json-result-'+processName);
            try{
                var dataOutObject= JSON.parse(dataOut);
                var rootMenu= util.objectToJSONMenu(dataOutObject, true);
                treePanel.getStore().setRootNode(rootMenu);
                jsonPanel.update('<textarea readonly style="width:99%; height:100%; white-space: pre !important;">'+
                        JSON.stringify(dataOutObject, null, 4)+'</textarea>');
            }catch(e){
                treePanel.getStore().setRootNode({});
            }
        }else if(outputDataFormat==='HTML'){
            divPanel.show();
            divPanel.update('<div style="width:99%; height:400px; overflow:auto;">'+ dataOut + '</div>');
        }else{
            divPanel.show();
            var textDataOut= dataOut;
            var textStyle='';
            if(outputDataFormat==='XML'){
                textDataOut= vkbeautify.xml(dataOut);
                textStyle= 'color:blue;';
            }
            divPanel.update('<textarea readonly style="width:99%; height:400px; white-space: pre !important; '+textStyle+'">'
                            + textDataOut + '</textarea>');
        }
    };
    
    Instance.getSelectedIds= function(){
        var selection = Instance.entityExtView.gridComponent.getSelectionModel().getSelection();
        var ids=[];
        if (selection.length>0) {
            for(var i=0; i<selection.length; i++){
                ids.push(selection[i].data.id);
            }
        }else{
            var check_items= document.getElementsByClassName("item_check");
            for(var i=0; i<check_items.length; i++){
                if(check_items[i].checked){
                    ids.push(check_items[i].value);
                }
            }
        }
        return ids;
    };
    
    Instance.deleteRecords= function(){
        var ids= Instance.getSelectedIds();
        if(ids.length>0){
            var filter={"in":{"id":ids}};
            if(ids.length===1){
                Instance.entityExtView.entityExtStore.deleteByFilter(JSON.stringify(filter), function(responseText){
                    Instance.entityExtView.reloadPageStore(Instance.entityExtView.store.currentPage);
                });
            }else{
                Ext.MessageBox.confirm('Confirmar', 'Esta seguro que desea eliminar '+ids.length+' registros?', function(result){
                    if(result==="yes"){
                        Instance.entityExtView.entityExtStore.deleteByFilter(JSON.stringify(filter), function(responseText){
                            Instance.loadFormData("");
                            Instance.entityExtView.reloadPageStore(Instance.entityExtView.store.currentPage);
                        });
                    }
                });
            }
        }
    };
    
    Instance.doFilter= function(){
        var url= "?filter="+JSON.stringify(Instance.filter)+"&tab=1";
        Instance.reloadGrid= true;
        console.log(url);
        mvcExt.navigate(url);
    };

    Instance.init();
}
</script>