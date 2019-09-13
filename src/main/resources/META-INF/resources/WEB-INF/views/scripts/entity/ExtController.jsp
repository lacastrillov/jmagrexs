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
        Instance.entityName= "${entityName}";
        Instance.entityRef= "${entityRef}";
        Instance.typeController= "${typeController}";
        Instance.idEntitySelected= null;
        Instance.parentEntityTitle= null;
        Instance.parentEntityId= null;
        Instance.reloadGrid= false;
        mvcExt.mappingController(Instance.id, Instance);
        Instance.initFilter();
    };
    
    Instance.initFilter= function(){
        Instance.filter={};
    };
    
    Instance.services.index= function(request){
        var activeTab= util.getParameter(request,"tab");
        var filter= util.getParameter(request,"filter");
        var id= util.getParameter(request,"id");
        
        if(activeTab!==null){
            Instance.entityExtView.tabsContainer.setActiveTab(Number(activeTab));
        }else{
            Instance.entityExtView.tabsContainer.setActiveTab(0);
        }
        
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
        if('eq' in Instance.filter && Instance.filter.eq.${associatedER}!==undefined && Instance.filter.eq.${associatedER}!==''){
            Instance.entityExtView.${associatedER}ExtInterfaces.entityExtStore.load(Instance.filter.eq.${associatedER}, Instance.entityExtView.${associatedER}ExtInterfaces.addLevel);
        }else{
            Instance.entityExtView.${associatedER}ExtInterfaces.addLevel(null);
        }
        </c:forEach>
        
        <c:if test="${viewConfig.visibleGrid}">
        if(activeTab!=="1" && (Instance.entityExtView.store.totalCount===undefined || Instance.reloadGrid)){
            Instance.loadGridData();
            Instance.appliedFilters= filter;
        }
        </c:if>
        if(activeTab==="1"){
            if(id!==null && id!==Instance.idEntitySelected){
                Instance.idEntitySelected= id;
                Instance.loadFormData(Instance.idEntitySelected);
            }
        }
        <c:if test="${viewConfig.preloadedForm && formRecordId!=null}">
        Instance.loadFormData(${formRecordId});
        </c:if>
        <c:if test="${viewConfig.gridAutoReloadInterval > 1000}">
        Instance.entityExtView.store.gridAutoReload= true;
        Instance.doGridAutoReload();
        </c:if>
    };
    
    Instance.loadGridData= function(){
        Instance.entityExtView.setFilterStore(JSON.stringify(Instance.filter));
        Instance.entityExtView.reloadPageStore(1);
        Instance.reloadGrid= false;
    };
    
    Instance.setFormData= function(record){
        if(Instance.entityExtView.formComponent!==null){
            Instance.entityExtView.formComponent.setActiveRecord(record || null);
            Instance.idEntitySelected= record.data.id;
        }
    };
    
    Instance.loadFormData= function(id){
        if(Instance.entityExtView.formComponent!==null){
            if(id!==null && id!==""){
                var loaded = false;
                if(Instance.entityExtView.gridComponent!==null){
                    var selection = Instance.entityExtView.gridComponent.getSelectionModel().getSelection();
                    if(selection.length===1 && selection[0].data.id+""===id){
                        Instance.setFormData(selection[0]);
                        loaded=true;
                    }
                }
                if(!loaded){
                    Instance.entityExtView.entityExtStore.load(id, function(data){
                        var record= Ext.create(Instance.modelName);
                        record.data= data;
                        Instance.entityExtView.formComponent.setActiveRecord(record || null);
                    });
                }
                Instance.loadChildExtControllers(id);
            }else{
                Instance.entityExtView.formComponent.getForm().reset();
                Instance.idEntitySelected= "";
                if(Object.keys(Instance.filter.eq).length !== 0){
                    var record= Ext.create(Instance.modelName);
                    for (var key in Instance.filter.eq) {
                        record.data[key]= Instance.filter.eq[key];
                    }
                    Instance.entityExtView.formComponent.setActiveRecord(record || null);
                }
                Instance.loadChildExtControllers("");
            }
        }
    };
    
    Instance.loadFormFirstItem= function(){
        Instance.loadFormData("");
        var params="&limit=1&page=1";
        params+="&sort="+Instance.entityExtView.store.getOrderProperty()+"&dir="+Instance.entityExtView.store.getOrderDir();
        Instance.entityExtView.entityExtStore.find(JSON.stringify(Instance.filter), params, function(responseText){
            if(responseText.success && responseText.totalCount>0){
                var data= responseText.data[0];
                var record= Ext.create(Instance.modelName);
                record.data= data;
                Instance.setFormData(record);
            }
        });
    };
    
    Instance.doGridAutoReload= function(){
        if(Instance.entityExtView.store.gridAutoReload){
            setTimeout(function(){
                Instance.entityExtView.reloadPageStore(1);
                Instance.doGridAutoReload();
            },${viewConfig.gridAutoReloadInterval});
        }
    };
    
    Instance.loadNNMulticheckData= function(){
        Instance.entityExtView.clearNNMultichecks();
        Instance.entityExtView.findAndLoadNNMultichecks(JSON.stringify(Instance.filter));
    };
    
    Instance.loadChildExtControllers= function(idEntitySelected){
        if(Instance.typeController==="Parent"){
            var jsonTypeChildExtViews= ${jsonTypeChildExtViews};
            Instance.entityExtView.childExtControllers.forEach(function(childExtController) {
                if(idEntitySelected!==""){
                    childExtController.parentEntityId= idEntitySelected;
                    childExtController.filter= {"eq":{"${entityRef}":idEntitySelected}};
                    childExtController.entityExtView.setValueInEmptyModel("${entityRef}", idEntitySelected);
                }else{
                    childExtController.parentEntityId= null;
                    childExtController.filter= {"eq":{"id":"0"}};
                }
                if(jsonTypeChildExtViews[childExtController.entityRef]==="tcv_1_to_n"){
                    childExtController.loadGridData();
                    childExtController.loadFormData("");
                }else if(jsonTypeChildExtViews[childExtController.entityRef]==="tcv_1_to_1"){
                    childExtController.loadFormFirstItem();
                }else if(jsonTypeChildExtViews[childExtController.entityRef]==="tcv_n_to_n"){
                    childExtController.loadNNMulticheckData();
                }
            });
        }
    };
    
    Instance.saveFormData= function(action, data){
        if(Instance.typeController==="Child" && Instance.parentEntityId===null){
            Ext.MessageBox.alert('Operaci&oacute;n cancelada', "No se ha seleccionado "+Instance.parentEntityTitle+" padre!!!");
        }else{
            Instance.entityExtView.entityExtStore.save(action, JSON.stringify(data), Instance.formSavedResponse);
        }
    };
    
    Instance.formSavedResponse= function(responseText){
        var message= responseText.message.replaceAll("${entityRef}","${viewConfig.singularEntityTitle}");
        if(responseText.success){
            <c:if test="${viewConfig.multipartFormData}">
            Instance.entityExtView.entityExtStore.upload(Instance.entityExtView.formComponent, responseText.data.id, function(responseUpload){
                Ext.MessageBox.alert('Status', message+"<br>"+responseUpload.message);
                if(responseUpload.success){
                    var record= Ext.create(Instance.modelName);
                    record.data= responseUpload.data;
                    Instance.entityExtView.formComponent.setActiveRecord(record || null);
                    
                    Instance.loadChildExtControllers(record.data.id);
                }
            });
            </c:if>
            <c:if test="${not viewConfig.multipartFormData}">
            var record= Ext.create(Instance.modelName);
            record.data= responseText.data;
            Instance.entityExtView.formComponent.setActiveRecord(record || null);
            Ext.MessageBox.alert('Status', message);
            
            Instance.loadChildExtControllers(record.data.id);
            </c:if>
            Instance.reloadGrid= true;
        }else{
            Ext.MessageBox.alert('Status', message);
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
                    Instance.loadFormData("");
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
    
    Instance.doFilter= function(filter){
        var url= "?filter="+JSON.stringify(filter);
        Instance.reloadGrid= true;
        console.log(url);
        mvcExt.navigate(url);
    };
    
    Instance.viewInternalPage= function(path){
        var urlAction= path;
        if(Instance.idEntitySelected!==""){
            urlAction+='#?filter={"eq":{"${entityRef}":'+Instance.idEntitySelected+'}}';
        }
        mvcExt.redirect(urlAction);
    };

    Instance.init();
}
</script>