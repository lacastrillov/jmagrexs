<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script>

function ${reportName}ExtViewport(){
    
    var Instance= this;
    
    var util= new Util();
    
    Instance.entityExtController= new ${reportName}ExtController(Instance);
    
    
    Instance.init= function(){
        var views = [];
        
        views.push(Instance.entityExtController.entityExtView.getMainView());

        Instance.contentViews = {
             id: 'content-panel',
             region: 'center',
             layout: 'card',
             margins: '0 0 0 0',
             //margins: '5 5 5 5',
             activeItem: 0,
             border: false,
             items: views
        };
        
        Instance.border= {
            region: 'center',
            layout: 'border',
            bodyBorder: false,
            defaults: {
                split: true
            },
            items: [Instance.contentViews]
        };
        
        <c:if test="${reportConfig.visibleFilters}">
        Instance.filters = Ext.create('Ext.form.Panel', {
            id: 'filters-panel',
            title: 'Filtros',
            region: 'west',
            plain: true,
            bodyBorder: false,
            bodyPadding: 10,
            margins: '0 0 0 0',
            //margins: '5 0 5 5',
            split: true,
            collapsible: true,
            collapsed: ${reportConfig.collapsedFilters},
            height: '100%',
            autoScroll: true,
            width: 400,
            minSize: 200,

            fieldDefaults: {
                labelWidth: "51%",
                anchor: '100%',
                labelAlign: 'right'
            },

            layout: {
                type: 'vbox',
                align: 'stretch'  // Child items are stretched to full width
            },

            items: ${jsonFieldsFilters},
            
            dockedItems: [{
                xtype: 'toolbar',
                dock: 'bottom',
                ui: 'footer',
                items: ['->',{
                    text: 'Filtrar',
                    scope: this,
                    handler: function(){
                        Instance.doFilter();
                    }
                },{
                    text: 'Limpiar Filtros',
                    scope: this,
                    handler: function(){
                        Instance.filters.getForm().reset();
                        var filterData= Instance.filters.getForm().getValues();
                        for(var fieldName in filterData){
                            if(fieldName.startsWith("operator_")){
                                var operatorCombobox= Instance.entityExtController.entityExtView.commonExtView.combobox[fieldName];
                                operatorCombobox.setValue(operatorCombobox.defaultOperator);
                            }
                        }
                        Instance.entityExtController.doFilter({});
                    }
                }]
            }]
        });
        
        Instance.doFilter= function(){
            var filterData= Instance.filters.getForm().getValues();
            var filter= {};
            for(var fieldName in filterData){
                if(!fieldName.startsWith("operator_") && !fieldName.endsWith("_end")){
                    var finalFieldName= fieldName;
                    var isRangeField= fieldName.endsWith("_start");
                    if(isRangeField){
                        finalFieldName= fieldName.replace("_start","");
                    }
                    var operatorFieldName= "operator_"+finalFieldName;
                    var operator= filterData[operatorFieldName];
                    if(!filter.hasOwnProperty(operator)){
                        if(operator==="isn" || operator==="isnn"){
                            filter[operator]= [];
                        }else{
                            filter[operator]= {};
                        }
                    }
                    switch(operator){
                        case "eq":
                        case "lk":
                        case "dt":
                        case "gt":
                        case "gte":
                        case "lt":
                        case "lte":
                            var value= filterData[fieldName];
                            if(value!==""){
                                filter[operator][finalFieldName]= value;
                            }
                            break;
                        case "isn":
                        case "isnn":
                            filter[operator].push(finalFieldName);
                            break;
                        case "in":
                            var value= filterData[fieldName];
                            if(value!==""){
                                filter[operator][finalFieldName]= value.split(",");
                            }
                            break;
                        case "btw":
                            var startValue= filterData[finalFieldName+"_start"];
                            var endValue= filterData[finalFieldName+"_end"];
                            if(startValue!=="" && endValue!==""){
                                filter[operator][finalFieldName]= [startValue, endValue];
                            }
                            break;
                    }
                }
            }
            for(var typeF in filter){
                if(Object.keys(filter[typeF]).length===0){
                    delete filter[typeF];
                }
            }
            Instance.entityExtController.doFilter(filter);
        };
        </c:if>
        
        <c:if test="${reportConfig.visibleMenu}">
        Instance.menuBar= Ext.create('Ext.toolbar.Toolbar', {
            region: 'north',
            items: ${menuItems}
        });
        </c:if>
        
    };
    
    /*mvcExt.loadView= function(idView){
        console.log(idView);
        Ext.getCmp('content-panel').layout.setActiveItem(idView);
        var record = Instance.treePanel.getStore().getNodeById(idView);
        if(typeof(record)!=='undefined'){
            Instance.treePanel.getSelectionModel().select(record);
        }
    };*/
    
    Instance.renderViewport= function(){
        Ext.create('Ext.Viewport', {
            layout: 'border',
            title: 'Toures Balon',
            items: [
            <c:if test="${reportConfig.visibleHeader}">
            {
                xtype: 'box',
                id: 'header',
                region: 'north',
                html: util.getHtml("headerHtml"),
                height: 30
            },
            </c:if>
            <c:if test="${reportConfig.visibleMenu}">
            Instance.menuBar,
            </c:if>
            <c:if test="${reportConfig.visibleFilters}">
            Instance.filters,
            </c:if>
            Instance.border
            ],
            renderTo: Ext.getBody()
        });
    };
    
    Instance.init();
}
</script>