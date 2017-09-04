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
        Instance.typeController= "${typeController}";
        Instance.idEntitySelected= "";
        mvcExt.mappingController(Instance.id, Instance);
        Instance.initFilter();
    };
    
    Instance.initFilter= function(){
        Instance.filter={};
        Instance.filter.eq={};
        Instance.filter.lk={};
        Instance.filter.btw={};
        Instance.filter.in={};
    };
    
    Instance.services.index= function(request){
        var activeTab= util.getParameter(request,"tab");
        var filter= util.getParameter(request,"filter");
        var id= util.getParameter(request,"id");
        
        if(activeTab!==""){
            Instance.entityExtView.tabsContainer.setActiveTab(Number(activeTab));
        }else{
            Instance.entityExtView.tabsContainer.setActiveTab(0);
        }
        
        if(filter!==""){
            Instance.initFilter();
            var currentFilter= JSON.parse(filter);
            for (var key in currentFilter) {
                Instance.filter[key]= currentFilter[key];
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
        
        if(activeTab==="1"){
            if(id!==""){
                Instance.idEntitySelected= id;
            }
            Instance.loadFormData(Instance.idEntitySelected);
        }
        <c:if test="${viewConfig.visibleGrid}">
        if(activeTab==="" || activeTab==="0"){
            Instance.loadGridData();
        }
        </c:if>
        <c:if test="${viewConfig.preloadedForm && formRecordId!=null}">
        Instance.loadFormData(${formRecordId});
        </c:if>
    };
    
    Instance.loadGridData= function(){
        Instance.entityExtView.setFilterStore(JSON.stringify(Instance.filter));
        Instance.entityExtView.reloadPageStore(1);
    };
    
    Instance.setFormData= function(record){
        if(Instance.entityExtView.formContainer!==null){
            var formComponent= Instance.entityExtView.formContainer.child('#form'+Instance.modelName);
            formComponent.setActiveRecord(record || null);
            Instance.idEntitySelected= record.data.id;
        }
    };
    
    Instance.loadFormData= function(id){
        if(Instance.entityExtView.formContainer!==null){
            var formComponent= Instance.entityExtView.formContainer.child('#form'+Instance.modelName);
            if(id!==""){
                var activeRecord= formComponent.getActiveRecord();

                if(activeRecord===null){
                    Instance.entityExtView.entityExtStore.load(id, function(data){
                        var record= Ext.create(Instance.modelName);
                        record.data= data;
                        formComponent.setActiveRecord(record || null);
                        Instance.findAssociatedEntities(data);
                    });
                }else{
                    Instance.findAssociatedEntities(activeRecord.data);
                }
                Instance.loadChildExtControllers(id);
            }else{
                Instance.idEntitySelected= "";
                if(Object.keys(Instance.filter.eq).length !== 0){
                    var record= Ext.create(Instance.modelName);
                    for (var key in Instance.filter.eq) {
                        record.data[key]= Instance.filter.eq[key];
                    }
                    formComponent.setActiveRecord(record || null);
                }
            }
        }
    };
    
    Instance.findAssociatedEntities= function(data){
        <c:forEach var="associatedER" items="${interfacesEntityRef}">
        if("${associatedER}" in data && "id" in data["${associatedER}"]){
            Instance.entityExtView.${associatedER}ExtInterfaces.reloadPageStore(1);
        }
        </c:forEach>
    };
    
    Instance.loadNNMulticheckData= function(){
        Instance.entityExtView.clearNNMultichecks();
        Instance.entityExtView.findAndLoadNNMultichecks(JSON.stringify(Instance.filter));
    };
    
    Instance.loadChildExtControllers= function(idEntitySelected){
        if(Instance.typeController==="Parent"){
            var jsonTypeChildExtViews= ${jsonTypeChildExtViews};
            Instance.entityExtView.childExtControllers.forEach(function(childExtController) {
                childExtController.filter= {"eq":{"${entityRef}":idEntitySelected}};
                childExtController.entityExtView.setValueInEmptyModel("${entityRef}", idEntitySelected);
                if(jsonTypeChildExtViews[childExtController.entityRef]==="tcv_standard"){
                    childExtController.loadGridData();
                    childExtController.loadFormData("");
                }else if(jsonTypeChildExtViews[childExtController.entityRef]==="tcv-n-n-multicheck"){
                    childExtController.loadNNMulticheckData();
                }
            });
        }
    };
    
    Instance.formSavedResponse= function(responseText){
        if(responseText.success){
            var formComponent= Instance.entityExtView.formContainer.child('#form'+Instance.modelName);
            <c:if test="${viewConfig.multipartFormData}">
            Instance.entityExtView.entityExtStore.upload(formComponent, responseText.data.id, function(responseUpload){
                Ext.MessageBox.alert('Status', responseText.message+"<br>"+responseUpload.message);
                if(responseUpload.success){
                    var record= Ext.create(Instance.modelName);
                    record.data= responseUpload.data;
                    formComponent.setActiveRecord(record || null);
                    
                    Instance.loadChildExtControllers(record.data.id);
                }
            });
            </c:if>
            <c:if test="${not viewConfig.multipartFormData}">
            var record= Ext.create(Instance.modelName);
            record.data= responseText.data;
            formComponent.setActiveRecord(record || null);
            Ext.MessageBox.alert('Status', responseText.message);
            
            Instance.loadChildExtControllers(record.data.id);
            </c:if>
        }else{
            Ext.MessageBox.alert('Status', responseText.message);
        }
    };
    
    Instance.doFilter= function(){
        var url= "?filter="+JSON.stringify(Instance.filter);
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