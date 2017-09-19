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
        mvcExt.mappingController(Instance.id, Instance);
        Instance.initFilter();
    };
    
    Instance.initFilter= function(){
        Instance.filter={};
        Instance.filter.eq={"mainProcessRef":"${viewConfig.mainProcessRef}"};
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
        }/*else{
            Instance.entityExtView.tabsContainer.setActiveTab(0);
        }*/
        
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
        
        if(activeTab==="0"){
            Instance.loadFormData(id);
        }
        
        if(activeTab==="1"){
            Instance.loadGridData();
        }
    };
    
    Instance.loadGridData= function(){
        Instance.entityExtView.setFilterStore(JSON.stringify(Instance.filter));
        Instance.entityExtView.reloadPageStore(1);
    };
    
    Instance.loadFormData= function(id){
        if(id!==""){
            Instance.entityExtView.entityExtStore.load(id, function(data){
                //Show Process
                Ext.getCmp('content-processes').layout.setActiveItem('formContainer'+data.processName+'Model');
                
                //Populate Form
                var record= Ext.create(data.processName+"Model");
                record.data= util.unremakeJSONObject(JSON.parse(data.dataIn));
                var formComponent= Ext.getCmp('formContainer'+data.processName+'Model').child('#form'+data.processName+'Model');
                formComponent.setActiveRecord(record);
                
                //Populate tree result
                Instance.formSavedResponse(data.processName, data.dataOut, data.outputDataFormat);
            });
        }
    };
    
    Instance.formSavedResponse= function(processName, dataOut, outputDataFormat){
        if(outputDataFormat==='JSON'){
            var rootMenu= util.objectToJSONMenu(JSON.parse(dataOut), true);
            var treePanel = Ext.getCmp('tree-result-'+processName);
            treePanel.getStore().setRootNode(rootMenu);
        }else if(outputDataFormat==='HTML'){
            var divPanel = Ext.getCmp('div-result-'+processName);
            divPanel.update('<div style="width:99%; height:400px; overflow:auto;">'+ dataOut + '</div>');
        }else{
            var divPanel = Ext.getCmp('div-result-'+processName);
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
    
    Instance.doFilter= function(){
        var url= "?filter="+JSON.stringify(Instance.filter)+"&tab=1";
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