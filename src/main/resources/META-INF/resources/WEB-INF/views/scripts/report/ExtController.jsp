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
        Instance.idEntitySelected= null;
        Instance.requireValueMap= ${reportConfig.visibleValueMapForm};
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
        if(activeTab!=="1" && (Instance.entityExtView.store.totalCount===undefined || Instance.reloadGrid)){
            Instance.loadGridData();
            Instance.appliedFilters= filter;
        }
        if(activeTab==="1"){
            if(id!==null && id!==Instance.idEntitySelected){
                Instance.idEntitySelected= id;
                Instance.loadFormData(Instance.idEntitySelected);
            }
        }
    };
    
    Instance.loadGridData= function(){
        if(!Instance.requireValueMap || Object.getOwnPropertyNames(Instance.filter.vm).length !== 0){
            Instance.entityExtView.setFilterStore(JSON.stringify(Instance.filter));
            Instance.entityExtView.reloadPageStore(1);
            Instance.reloadGrid= false;
        }
    };
    
    Instance.loadFormData= function(id){
        if(Instance.entityExtView.formComponent!==null){
            if(id!==null && id!==""){
                var activeRecord= Instance.entityExtView.formComponent.getActiveRecord();

                if(activeRecord===null){
                    Instance.entityExtView.entityExtStore.load(id, function(data){
                        var record= Ext.create(Instance.modelName);
                        record.data= data;
                        Instance.entityExtView.formComponent.setActiveRecord(record || null);
                    });
                }
                Instance.loadChildExtControllers(Instance.idEntitySelected);
            }else{
                Instance.entityExtView.formComponent.getForm().reset();
                Instance.idEntitySelected= "";
                if('eq' in Instance.filter && Object.keys(Instance.filter.eq).length !== 0){
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
    
    Instance.loadChildExtControllers= function(idEntitySelected){
        if(Instance.typeController==="Parent"){
            var jsonChildRefColumnNames= ${jsonChildRefColumnNames};
            Instance.entityExtView.childExtControllers.forEach(function(childExtController) {
                if(idEntitySelected!==""){
                    childExtController.parentEntityId= idEntitySelected;
                    childExtController.filter= {"eq":{}};
                    childExtController.filter.eq[jsonChildRefColumnNames[childExtController.reportName]]= idEntitySelected;
                }else{
                    childExtController.parentEntityId= null;
                    childExtController.filter= {"eq":{}};
                    childExtController.filter.eq[jsonChildRefColumnNames[childExtController.reportName]]= 0;
                }
                childExtController.loadGridData();
                childExtController.loadFormData("");
            });
        }
    };
    
    Instance.doFilter= function(filter){
        var url= "?filter="+JSON.stringify(filter);
        Instance.reloadGrid= true;
        mvcExt.navigate(url);
    };

    Instance.init();
}
</script>