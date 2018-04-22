<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script>

function ${reportName}ExtView(parentExtController, parentExtView){
    
    var Instance= this;
    
    Instance.id= "/${entityRef}";
    
    Instance.modelName="${reportName}Model";
    
    var util= new Util();
    
    // MODELS **********************************************
    
    Instance.entityExtModel= new ${reportName}ExtModel();
    
    // STORES **********************************************
    
    Instance.entityExtStore= new ${reportName}ExtStore();
    
    // COMPONENTS *******************************************
    
    Instance.commonExtView= new CommonExtView(parentExtController, Instance, '${reportName}');
    
    //*******************************************************
    
    
    Instance.init= function(){
        Instance.typeView= "${typeView}";
        Instance.pluralReportTitle= '${reportConfig.pluralReportTitle}';
        Instance.entityExtModel.defineModel(Instance.modelName);
        Instance.store= Instance.entityExtStore.getStore(Instance.modelName);
        <c:if test="${reportConfig.activeGridTemplate}">
        Instance.gridModelName= "${reportName}TemplateModel";
        Instance.entityExtModel.defineTemplateModel(Instance.gridModelName);
        Instance.gridStore= Instance.entityExtStore.getTemplateStore(Instance.gridModelName);
        </c:if>
        Instance.createMainView();
    };
    
    Instance.setFilterStore= function(filter){
        <c:if test="${not reportConfig.activeGridTemplate}">
            Instance.store.getProxy().extraParams.filter= filter;
        </c:if>
        <c:if test="${reportConfig.activeGridTemplate}">
            Instance.gridStore.getProxy().extraParams.filter= filter;
        </c:if>
    };
    
    Instance.reloadPageStore= function(page){
        <c:if test="${not reportConfig.activeGridTemplate}">
        Instance.store.loadPage(page);
        </c:if>
        <c:if test="${reportConfig.activeGridTemplate}">
        Instance.gridStore.loadPage(page);
        </c:if>
    };
    
    <c:if test="${reportConfig.visibleForm}">
    function getFormContainer(childExtControllers){
        var formFields= ${jsonFormFields};

        var renderReplacements= [];

        var additionalButtons= ${jsonInternalViewButtons};

        Instance.defineWriterForm(formFields, renderReplacements, additionalButtons);
        
        var itemsForm= [{
            itemId: 'form${reportName}',
            xtype: 'writerform${reportName}',
            border: false,
            width: '100%',
            listeners: {
                create: function(form, data){
                    Instance.entityExtStore.save${entityName}('create', JSON.stringify(data), parentExtController.formSavedResponse);
                },
                update: function(form, data){
                    Instance.entityExtStore.save${entityName}('update', JSON.stringify(data), parentExtController.formSavedResponse);
                },
                render: function(panel) {
                    Instance.commonExtView.enableManagementTabHTMLEditor();
                }
            }
        }];
        
        if(Instance.typeView==="Parent"){
            itemsForm.push(getChildsExtViewTabs(childExtControllers));
        }
        
        return Ext.create('Ext.container.Container', {
            id: 'formContainer${reportName}',
            title: 'Detalle',
            type: 'fit',
            align: 'stretch',
            items: itemsForm
        });
    };
    
    function getChildsExtViewTabs(childExtControllers){
        var items=[];
        
        childExtControllers.forEach(function(childExtController) {
            var itemTab= {
                xtype:'tabpanel',
                title: childExtController.entityExtView.pluralReportTitle,
                plain:true,
                activeTab: 0,
                style: 'background-color:#dfe8f6; padding:10px;',
                defaults: {bodyStyle: 'padding:15px', autoScroll:true},
                items:[
                    childExtController.entityExtView.gridContainer,

                    childExtController.entityExtView.formContainer

                ]
            };
            
            items.push(itemTab);
        });
        
        var tabObect= {
            xtype:'tabpanel',
            plain:true,
            activeTab: 0,
            style: 'padding:25px 15px 45px 15px;',
            items:items
        };
        
        return tabObect;
    };
    
    Instance.setFormActiveRecord= function(record){
        Instance.formComponent.setActiveRecord(record || null);
    };
    
    Instance.defineWriterForm= function(fields, renderReplacements, additionalButtons){
        Ext.define('WriterForm${reportName}', {
            extend: 'Ext.form.Panel',
            alias: 'widget.writerform${reportName}',

            requires: ['Ext.form.field.Text'],

            initComponent: function(){
                //this.addEvents('create');
                
                var buttons= [
                <c:if test="${viewConfig.visibleSeeAllButton}">
                {
                    text: '&#x25BC; Ver todo',
                    scope: this,
                    handler: this.onSeeAll
                },'|'
                </c:if>
                ];
                if(additionalButtons){
                    for(var i=0; i<additionalButtons.length; i++){
                        buttons.push(additionalButtons[i]);
                    }
                }
                Ext.apply(this, {
                    activeRecord: null,
                    //iconCls: 'icon-user',
                    frame: false,
                    defaultType: 'textfield',
                    bodyPadding: 15,
                    fieldDefaults: {
                        minWidth: 300,
                        anchor: '50%',
                        labelAlign: 'right'
                    },
                    items: fields,
                    dockedItems: [{
                        xtype: 'toolbar',
                        dock: 'bottom',
                        ui: 'footer',
                        items: buttons
                    }]
                });
                this.callParent();
            },

            setActiveRecord: function(record){
                this.activeRecord = record;
                this.getForm().loadRecord(this.activeRecord);
            },
                    
            getActiveRecord: function(){
                return this.activeRecord;
            },
            
            onSeeAll: function(){
                this.doLayout();
            },

            onReset: function(){
                this.getForm().reset();
                parentExtController.loadFormData("");
            },
            
            renderReplaceActiveRecord: function(record){
                return record;
            }
    
        });
        
    };
    
    </c:if>
    
    <c:if test="${reportConfig.visibleValueMapForm}">
    function getValueMapFormContainer(){
        return Ext.widget({
            xtype: 'form',
            layout: 'form',
            title: 'Establecer variables',
            id: 'simpleForm${reportName}',
            region: 'north',
            frame: false,
            collapsible: true,
            bodyPadding: '5 5 0',
            fieldDefaults: {
                labelWidth: 170,
                labelAlign: 'right'
            },
            defaultType: 'textfield',
            items: ${jsonFormMapFields},

            buttons: [{
                text: 'Consultar',
                handler: function() {
                    this.up('form').getForm().isValid();
                    parentExtController.filter.vm=this.up('form').getForm().getValues();
                    parentExtController.doFilter();
                }
            },{
                text: 'Limpiar',
                handler: function() {
                    this.up('form').getForm().reset();
                }
            }]
        });
    }
    </c:if>
    
    function getGridContainer(){
        var idGrid= 'grid${reportName}';
        var gridColumns= ${jsonGridColumns};

        Instance.getEmptyRec= function(){
            return new ${reportName}Model(${jsonEmptyModel});
        };
        
        var store= Instance.store;
        <c:if test="${reportConfig.activeGridTemplate}">
        store= Instance.gridStore;
        </c:if>

        Instance.defineGrid('${reportConfig.pluralReportTitle}', gridColumns);
        
        return Ext.create('Ext.container.Container', {
            id: 'gridContainer${reportName}',
            title: 'Listado',
            region: 'center',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [{
                itemId: idGrid,
                xtype: 'writergrid${reportName}',
                style: 'border: 0px',
                flex: 1,
                store: store,
                disableSelection: ${reportConfig.activeGridTemplate},
                trackMouseOver: !${reportConfig.activeGridTemplate},
                listeners: {
                    selectionchange: function(selModel, selected) {
                        if(selected[0]){
                            Instance.setFormActiveRecord(selected[0]);
                        }
                    },
                    export: function(typeReport){
                        var filterData= JSON.stringify(parentExtController.filter);
                        filterData= filterData.replaceAll("{","(").replaceAll("}",")");
                        var data= "?filter="+filterData;
                        data+="&limit="+store.pageSize+"&page="+store.currentPage;
                        if(store.sorters.items.length>0){
                            data+="&sort="+store.getOrderProperty()+"&dir="+store.getOrderDir();
                        }
                        data+="&dtoName=${reportConfig.dtoName}";
                        
                        switch(typeReport){
                            case "json":
                                var urlFind= store.proxy.api.read;
                                window.open(urlFind+data,'_blank');
                                break;
                            case "xml":
                                var urlFind= store.proxy.api.read.replace("report/","report/xml/");
                                window.open(urlFind+data,'_blank');
                                break;
                            case "xls":
                                var urlFind= store.proxy.api.read.replace("report/","report/xls/");
                                window.open(urlFind+data,'_blank');
                                break;
                            case "csv":
                                var urlFind= store.proxy.api.read.replace("report/","report/csv/");
                                window.open(urlFind+data,'_blank');
                                break;
                        }
                    }
                }
            }],
            listeners: {
                activate: function(panel) {
                    //store.loadPage(1);
                }
            }
        });
    };
    
    function getComboboxLimit(store){
        var combobox= Instance.commonExtView.getSimpleCombobox('limit', 'L&iacute;mite', 'config', [50, 100, 200, 500, 1000, 2000, 5000, 10000]);
        combobox.addListener('change',function(record){
            if(record.getValue()!=="" && store.pageSize!==record.getValue()){
                store.pageSize=record.getValue();
                Instance.reloadPageStore(1);
            }
        }, this);
        combobox.labelWidth= 46;
        combobox.width= 125;
        combobox.setValue(${reportConfig.maxResultsPerPage});
        
        return combobox;
    }
    
    function getComboboxOrderBy(store){
        var combobox= Instance.commonExtView.getSimpleCombobox('sort', 'Ordenar por', 'config', ${sortColumns});
        combobox.addListener('change',function(record){
            if(record.getValue()!=="" && store.getOrderProperty()!==record.getValue()){
                var dir= store.getOrderDir();
                store.sortBy(record.getValue(), dir);
                Instance.reloadPageStore(1);
            }
        }, this);
        combobox.setValue("${reportConfig.defaultOrderBy}");
        
        return combobox;
    }
    
    function getComboboxOrderDir(store){
        var combobox= Instance.commonExtView.getSimpleCombobox('dir', 'Direcci&oacute;n', 'config', ["ASC", "DESC"]);
        combobox.addListener('change',function(record){
            if(record.getValue()!=="" && store.getOrderDir()!==record.getValue()){
                var prop= store.getOrderProperty();
                store.sortBy(prop, record.getValue());
                Instance.reloadPageStore(1);
            }
        }, this);
        combobox.setValue("${reportConfig.defaultOrderDir}");
        
        return combobox;
    }
    
    Instance.defineGrid= function(modelText, columns){
        Ext.define('WriterGrid${reportName}', {
            extend: 'Ext.grid.Panel',
            alias: 'widget.writergrid${reportName}',

            requires: [
                'Ext.grid.plugin.CellEditing',
                'Ext.form.field.Text',
                'Ext.toolbar.TextItem'
            ],

            initComponent: function(){

                this.editing = Ext.create('Ext.grid.plugin.CellEditing');
                
                Ext.apply(this, {
                    //iconCls: 'icon-grid',
                    hideHeaders:${reportConfig.hideHeadersGrid},
                    frame: false,
                    plugins: [this.editing],
                    dockedItems: [ {
                        weight: 2,
                        xtype: 'toolbar',
                        margin  : '5 0 5 0',
                        dock: 'top',
                        items: [{
                            xtype: 'tbtext',
                            text: '<b>@lacv</b>'
                        }, '|',
                        getComboboxLimit(this.store),
                        {
                            text: 'Ordenar',
                            //iconCls: 'add16',
                            menu: [
                                getComboboxOrderBy(this.store),
                                getComboboxOrderDir(this.store)]
                        }
                        <c:if test="${reportConfig.visibleExportButton}">
                        ,{
                            xtype:'splitbutton',
                            text: 'Exportar',
                            //iconCls: 'add16',
                            menu: [
                                {text: 'A CSV', handler: function(){this.exportTo('csv')}, scope: this},
                                {text: 'A Excel', handler: function(){this.exportTo('xls')}, scope: this},
                                {text: 'A JSON', handler: function(){this.exportTo('json')}, scope: this},
                                {text: 'A XML', handler: function(){this.exportTo('xml')}, scope: this}]
                        }
                        </c:if>
                        ]
                    }, {
                        weight: 1,
                        xtype: 'pagingtoolbar',
                        dock: 'bottom',
                        ui: 'footer',
                        store: this.store,
                        displayInfo: true,
                        displayMsg: modelText+' {0} - {1} de {2}',
                        emptyMsg: "No hay "+modelText
                    }],
                    columns: columns
                });
                this.callParent();
                this.getSelectionModel().on('selectionchange', this.onSelectChange, this);
            },

            onSelectChange: function(selModel, selections){
                if(this.down('#delete')!=null){
                    this.down('#delete').setDisabled(selections.length === 0);
                }
            },

            onSync: function(){
                this.store.sync();
            },
            
            exportTo: function(type){
                this.fireEvent('export', type);
            }
            
        });
    };
    
    <c:forEach var="processButton" items="${reportConfig.processButtons}">
    function getForm${processButton.processName}Process(){
        
        var processForm = Ext.create('Ext.form.Panel', {
            itemId: 'form${processButton.processName}Process',
            defaultType: 'textfield',
            border: false,
            bodyPadding: 15,
            autoScroll: true,
            fieldDefaults: {
                minWidth: 300,
                anchor: '100%',
                labelAlign: 'right'
            },

            items: ${jsonFormFieldsProcessMap[processButton.processName]}
        });

        var win = Ext.create('Ext.window.Window', {
            autoShow: false,
            title: '${processButton.processTitle}',
            closable: true,
            closeAction: 'hide',
            width: '50%',
            height: 300,
            minWidth: 300,
            minHeight: 200,
            layout: 'fit',
            plain:true,
            maximizable: true,
            minimizable: true,
            items: processForm,

            buttons: [{
                text: 'Ejecutar',
                handler: function(){
                    var jsonData= processForm.getForm().getValues();
                    Instance.entityExtStore.doProcess('${processButton.mainProcessRef}', '${processButton.processName}', jsonData, function(responseText){
                        Ext.MessageBox.alert('Status', responseText);
                        win.hide();
                    });
                }
            },{
                text: 'Cancelar',
                handler: function(){
                    win.hide();
                }
            }],
            listeners: {
                "minimize": function (window, opts) {
                    window.collapse();
                    window.setWidth(150);
                    window.alignTo(Ext.getBody(), 'bl-bl')
                }
            },
            tools: [{
                type: 'restore',
                handler: function (evt, toolEl, owner, tool) {
                    var window = owner.up('window');
                    window.setWidth(600);
                    window.setHeight(300);
                    window.expand('', false);
                    window.center();
                }
            }]
        });
        
        return win;
    }
    </c:forEach>
    
    Instance.showProcessForm= function(processName, sourceByDestinationFields, rowIndex){
        var initData={};
        if(rowIndex!==-1){
            var rec = Instance.gridComponent.getStore().getAt(rowIndex);
            for (var source in sourceByDestinationFields) {
                var destination = sourceByDestinationFields[source];
                initData[destination]=rec.get(source);
            }
            
        }else{
            var formData= Instance.formComponent.getForm().getValues();
            for (var source in sourceByDestinationFields) {
                var destination = sourceByDestinationFields[source];
                initData[destination]=formData[source];
            }
            
        }
        Instance.processForms[processName].show(null, function() {});
        var processForm= Instance.processForms[processName].child('#form'+processName+'Process');
        processForm.getForm().reset();
        processForm.getForm().setValues(initData);
    };
    
    function ${reportConfig.labelField}EntityRender(value, p, record){
        if(record){
            if(Instance.typeView==="Parent"){
                return "<a style='font-size: 15px;' href='#?id="+record.data.${reportConfig.idColumnName}+"&tab=1'>"+value+"</a>";
            }else{
                return value;
            }
        }else{
            return value;
        }
    };
    
    Instance.hideParentField= function(entityRef){
        if(Instance.formContainer!==null){
            var fieldsForm= Instance.formComponent.items.items;
            fieldsForm.forEach(function(field) {
                if(field.name===entityRef){
                    field.hidden= true;
                }
            });
        }
        if(Instance.gridContainer!==null){
            var columnsGrid= Instance.gridComponent.columns;
            columnsGrid.forEach(function(column) {
                if(column.dataIndex===entityRef){
                    column.hidden= true;
                }
            });
        }
    };

    Instance.createMainView= function(){
        Instance.childExtControllers= [];
        if(Instance.typeView==="Parent"){
        <c:forEach var="childExtReport" items="${reportConfig.childExtReports}">
            var ${childExtReport.value}ExtControllerVar= new ${childExtReport.value}ExtController(parentExtController, Instance);
            ${childExtReport.value}ExtControllerVar.entityExtView.hideParentField("${reportConfig.childRefColumnNames[childExtReport.value]}");
            Instance.childExtControllers.push(${childExtReport.value}ExtControllerVar);
        </c:forEach>
        }
    
        <c:if test="${reportConfig.visibleValueMapForm}">
        Instance.valueMapformContainer = getValueMapFormContainer();
        </c:if>
            
        Instance.formComponent= null;
        <c:if test="${reportConfig.visibleForm}">
        Instance.formContainer = getFormContainer(Instance.childExtControllers);
        Instance.formComponent= Instance.formContainer.child('#form${reportName}');
        Instance.store.formComponent= Instance.formComponent;
        </c:if>
            
        Instance.gridContainer = getGridContainer();
        Instance.gridComponent = Instance.gridContainer.child('#grid${reportName}');
        Instance.store.gridComponent= Instance.gridComponent;
        
        Instance.processForms={};
        <c:forEach var="processButton" items="${reportConfig.processButtons}">
        Instance.processForms["${processButton.processName}"]= getForm${processButton.processName}Process();
        </c:forEach>
        
        Instance.tabsContainer= Ext.widget('tabpanel', {
            region: 'center',
            activeTab: 0,
            style: 'background-color:#dfe8f6; margin:0px',
            defaults: {bodyStyle: 'padding:15px', autoScroll:true},
            items:[
                Instance.gridContainer,
                <c:if test="${reportConfig.visibleForm}">
                Instance.formContainer
                </c:if>
            ],
            listeners: {
                tabchange: function(tabPanel, tab){
                    var idx = tabPanel.items.indexOf(tab);
                    var url= util.addUrlParameter(parentExtController.request,"tab", idx);
                    if(idx===0){
                        url= util.removeUrlParameter(url,"id");
                    }
                    if(url!==""){
                        mvcExt.navigate(url);
                    }
                }
            }
        });
        
        Instance.mainView= {
            id: Instance.id,
            title: '${reportConfig.pluralReportTitle}',
            frame: false,
            layout: 'border',
            items: [
                <c:if test="${reportConfig.visibleValueMapForm}">
                Instance.valueMapformContainer,
                </c:if>
                Instance.tabsContainer
            ]
        };
        
    };
    
    Instance.getMainView= function(){
        return Instance.mainView;
    };

    Instance.init();
}
</script>