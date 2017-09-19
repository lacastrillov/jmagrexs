<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script>

function ${reportName}ExtController(parentExtView){
    
    var Instance= this;
    
    Instance.id= "/${entityRef}";
    
    Instance.modelName="${reportName}Model";
    
    Instance.services= {};
    
    var util= new Util();
    
    // VIEWS **********************************************
    
    Instance.entityExtView= new ${reportName}ExtView(Instance, null);
    
    //*******************************************************
    
    
    
    Instance.init= function(){
        Instance.reportName= "${reportName}";
        Instance.typeController= "${typeController}";
        mvcExt.mappingController(Instance.id, Instance);
        Instance.requireValueMap= ${reportConfig.visibleValueMapForm};
        Instance.initFilter();
    };
    
    Instance.initFilter= function(){
        Instance.filter={};
        Instance.filter.eq={};
        Instance.filter.lk={};
        Instance.filter.btw={};
        Instance.filter.vm={};
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
        
        if(activeTab==="1"){
            Instance.loadFormData(id);
        }
        
        if(activeTab==="" || activeTab==="0"){
            Instance.loadGridData();
        }
    };
    
    Instance.loadGridData= function(){
        if(!Instance.requireValueMap || Object.getOwnPropertyNames(Instance.filter.vm).length !== 0){
            Instance.entityExtView.setFilterStore(JSON.stringify(Instance.filter));
            Instance.entityExtView.reloadPageStore(1);
        }
    };
    
    Instance.loadFormData= function(id){
        if(Instance.entityExtView.formContainer!==null){
            var formComponent= Instance.entityExtView.formContainer.child('#form'+Instance.modelName);
            if(id!==""){
                Instance.idEntitySelected= id;
                var activeRecord= formComponent.getActiveRecord();

                if(activeRecord===null){
                    Instance.entityExtView.entityExtStore.load(id, function(data){
                        var record= Ext.create(Instance.modelName);
                        record.data= data;
                        formComponent.setActiveRecord(record || null);
                    });
                }
                Instance.loadChildExtControllers(Instance.idEntitySelected);
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
    
    Instance.loadChildExtControllers= function(idEntitySelected){
        if(Instance.typeController==="Parent"){
            var jsonChildRefColumnNames= ${jsonChildRefColumnNames};
            Instance.entityExtView.childExtControllers.forEach(function(childExtController) {
                childExtController.filter= {"eq":{}};
                childExtController.filter.eq[jsonChildRefColumnNames[childExtController.reportName]]= idEntitySelected;
                childExtController.loadGridData();
                childExtController.loadFormData("");
            });
        }
    };
    
    Instance.doFilter= function(){
        var url= "?filter="+JSON.stringify(Instance.filter);
        mvcExt.navigate(url);
    };

    Instance.init();
}
</script>