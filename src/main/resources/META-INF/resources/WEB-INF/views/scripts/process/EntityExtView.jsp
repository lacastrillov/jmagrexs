<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<script>

function ${entityName}ExtView(parentExtController, parentExtView){
    
    var Instance= this;
    
    Instance.id= "/${entityRef}";
    
    Instance.modelName="${viewConfig.entityNameLogProcess}Model";
    
    var util= new Util();
    
    // MODELS **********************************************
    
    Instance.entityExtModel= new ${entityName}ExtModel();
    
    // STORES **********************************************
    
    Instance.entityExtStore= new ${entityName}ExtStore();
    
    // COMPONENTS *******************************************
    
    Instance.commonExtView= new CommonExtView(parentExtController, Instance, '${entityName}');
    
    //*******************************************************
    
    
    Instance.init= function(){
        Instance.typeView= "${typeView}";
        <c:forEach var="processName" items="${nameProcesses}">
        Instance.entityExtModel.define${processName.key}Model("${processName.key}Model");
        </c:forEach>
        
        Instance.entityExtModel.defineModel(Instance.modelName);
        Instance.store= Instance.entityExtStore.getStore(Instance.modelName);
        
        Instance.createMainView();
    };
    
    Instance.setFilterStore= function(filter){
        <c:if test="${not viewConfig.activeGridTemplate}">
            Instance.store.getProxy().extraParams.filter= filter;
        </c:if>
        <c:if test="${viewConfig.activeGridTemplate}">
            Instance.gridStore.getProxy().extraParams.filter= filter;
        </c:if>
    };
    
    Instance.reloadPageStore= function(page){
        <c:if test="${not viewConfig.activeGridTemplate}">
            Instance.store.loadPage(page);
        </c:if>
        <c:if test="${viewConfig.activeGridTemplate}">
            Instance.gridStore.loadPage(page);
        </c:if>
    };
    
    <c:if test="${viewConfig.visibleForm}">
        
    function getTreeMenuProcesses(){
        var store1 = {
            //model: 'Item',
            root: {
                text: 'Root 1',
                expanded: true,
                children: [
                    <c:forEach var="processName" items="${nameProcesses}">
                    {
                        id: 'formContainer${processName.key}Model',
                        text: '${processName.value}',
                        leaf: true
                    },
                    </c:forEach>
                ]
            }
        };
        
        var treePanelProcess = Ext.create('Ext.tree.Panel', {
            id: 'tree-panel-process',
            title: 'Lista Procesos',
            region:'west',
            collapsible: true,
            split: true,
            width: 300,
            minSize: 200,
            height: '100%',
            rootVisible: false,
            autoScroll: true,
            store: store1
        });
        
        treePanelProcess.getSelectionModel().on('select', function(selModel, record) {
            if (record.get('leaf')) {
                Ext.getCmp('content-processes').layout.setActiveItem(record.getId());
            }
        });
        
        return treePanelProcess;
    }
    
    <c:forEach var="processName" items="${nameProcesses}">
    function getFormContainer${processName.key}(modelName, store){
        var formFields= ${jsonFormFieldsMap[processName.key]};

        Instance.defineWriterForm("${processName.key}Model", formFields);
        
        var itemsForm= [{
            itemId: 'form${processName.key}Model',
            xtype: 'writerform${processName.key}Model',
            title: '${processName.value}',
            border: false,
            width: '60%',
            minWidth: 300,
            listeners: {
                doProcess: function(form, data){
                    Instance.entityExtStore.doProcess('${processName.key}', data, parentExtController.formSavedResponse);
                },
                render: function(panel) {
                    Instance.commonExtView.enableManagementTabHTMLEditor();
                }
            }
        }];
            
        itemsForm.push(getResultTree("${processName.key}"));
        
        itemsForm.push({id: 'div-result-${processName.key}', xtype: "panel", html: ""});
        
        return Ext.create('Ext.container.Container', {
            id: 'formContainer${processName.key}Model',
            type: 'fit',
            align: 'stretch',
            items: itemsForm
        });
    };
    </c:forEach>
    
    function getResultTree(processName){
        var store = {
            id: 'store-result-'+processName,
            //model: 'Item',
            root: {
                text: 'Root',
                expanded: true,
                children: []
            }
        };

        // Go ahead and create the TreePanel now so that we can use it below
         var treePanel = Ext.create('Ext.tree.Panel', {
            id: 'tree-result-'+processName,
            title: 'Resultado',
            //region:'north',
            split: true,
            width: '100%',
            autoHeight: true,
            minSize: 150,
            rootVisible: false,
            autoScroll: true,
            store: store
        });
        
        return treePanel;
    }
    
    Instance.setFormActiveRecord= function(record){
        var formComponent= Instance.formContainer.child('#form'+Instance.modelName);
        formComponent.setActiveRecord(record || null);
    };
    
    Instance.defineWriterForm= function(modelName, fields){
        Ext.define('WriterForm'+modelName, {
            extend: 'Ext.form.Panel',
            alias: 'widget.writerform'+modelName,

            requires: ['Ext.form.field.Text'],

            initComponent: function(){
                //this.addEvents('create');
                
                var buttons= [];
                <c:if test="${viewConfig.editableForm}">
                buttons= [{
                    itemId: 'save'+modelName,
                    text: 'Ejecutar',
                    scope: this,
                    handler: this.onSave
                }, {
                    //iconCls: 'icon-reset',
                    text: 'Limpiar',
                    scope: this,
                    handler: this.onReset
                },'|'];
                </c:if>
                Ext.apply(this, {
                    activeRecord: null,
                    //iconCls: 'icon-user',
                    frame: false,
                    defaultType: 'textfield',
                    bodyPadding: 15,
                    fieldDefaults: {
                        minWidth: 300,
                        anchor: '100%',
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
                this.getForm().reset();
                if (this.activeRecord) {
                    this.getForm().setValues(this.activeRecord.data);
                }
            },
                    
            getActiveRecord: function(){
                return this.activeRecord;
            },
            
            onSave: function(){
                var form = this.getForm();
                
                if (form.isValid()) {
                    this.fireEvent('doProcess', this, form.getValues());
                }
            },
            
            onReset: function(){
                this.setActiveRecord(null);
                this.getForm().reset();
            }
    
        });
        
    };
    
    </c:if>
    
    <c:if test="${viewConfig.visibleGrid}">
        
    function getFiltersPanel(){
        return Ext.create('Ext.form.Panel', {
            id: 'filters-panel',
            title: 'Filtros',
            region: 'west',
            plain: true,
            bodyBorder: false,
            bodyPadding: 10,
            margins: '0 0 0 0',
            split: true,
            collapsible: true,
            collapsed: ${viewConfig.collapsedFilters},
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
                        parentExtController.doFilter();
                    }
                },{
                    text: 'Limpiar Filtros',
                    scope: this,
                    handler: function(){
                        Instance.filters.getForm().reset();
                        parentExtController.initFilter();
                        parentExtController.doFilter();
                    }
                }]
            }]
        });
    }
        
    function getGridContainer(modelName, store, formContainer){
        var idGrid= 'grid'+modelName;
        var gridColumns= ${jsonGridColumns};
        
        var getEmptyRec= function(){
            return new ${viewConfig.entityNameLogProcess}Model(${jsonEmptyModel});
        };
        
        <c:if test="${viewConfig.activeGridTemplate}">
        modelName= Instance.gridModelName;
        store= Instance.gridStore;
        </c:if>

        Instance.defineWriterGrid(modelName, '${viewConfig.entityNameLogProcess}', gridColumns, getEmptyRec, Instance.typeView);
        
        return Ext.create('Ext.container.Container', {
            id: 'gridContainer'+modelName,
            region: 'center',
            margins: '0 0 0 0',
            <c:if test="${viewConfig.gridHeightChildView != 0}">
            height: ${viewConfig.gridHeightChildView},
            </c:if>
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [{
                itemId: idGrid,
                xtype: 'writergrid'+modelName,
                style: 'border: 0px',
                flex: 1,
                store: store,
                disableSelection: ${viewConfig.activeGridTemplate},
                trackMouseOver: !${viewConfig.activeGridTemplate},
                listeners: {
                    selectionchange: function(selModel, selected) {
                        if(formContainer!==null){
                            formContainer.child('#form'+modelName).setActiveRecord(selected[0] || null);
                        }
                    },
                    export: function(typeReport){
                        var data= "?filter="+JSON.stringify(parentExtController.filter);
                        data+="&limit="+store.pageSize+"&page="+store.currentPage;
                        data+="&sort="+store.getOrderProperty()+"&dir="+store.getOrderDir();
                        
                        switch(typeReport){
                            case "json":
                                var urlFind= store.proxy.api.read;
                                window.open(urlFind+data,'_blank');
                                break;
                            case "xml":
                                var urlFind= store.proxy.api.read.replace("/find.htm","/find/xml.htm");
                                window.open(urlFind+data,'_blank');
                                break;
                            case "xls":
                                var urlFind= store.proxy.api.read.replace("/find.htm","/find/xls.htm");
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
    
    Instance.setGridEmptyRec= function(obj){
        var gridComponent= Instance.gridContainer.child('#grid'+Instance.modelName);
        gridComponent.getEmptyRec= function(){
            return new ${entityName}Model(obj);
        };
    };
    
    Instance.defineWriterGrid= function(modelName, modelText, columns, getEmptyRec, typeView){
        Ext.define('WriterGrid'+modelName, {
            extend: 'Ext.grid.Panel',
            alias: 'widget.writergrid'+modelName,

            requires: [
                'Ext.grid.plugin.CellEditing',
                'Ext.form.field.Text',
                'Ext.toolbar.TextItem'
            ],

            initComponent: function(){

                this.editing = Ext.create('Ext.grid.plugin.CellEditing');
                
                var comboboxLimit= Instance.commonExtView.getSimpleCombobox('limit', 'L&iacute;mite', 'config', [50, 100, 200, 500]);
                comboboxLimit.addListener('change',function(record){
                    if(record.getValue()!=="" && this.store.pageSize!==record.getValue()){
                        this.store.pageSize=record.getValue();
                        Instance.reloadPageStore(1);
                    }
                }, this);
                comboboxLimit.labelWidth= 50;
                comboboxLimit.width= 130;
                comboboxLimit.setValue(${viewConfig.maxResultsPerPage});

                Ext.apply(this, {
                    //iconCls: 'icon-grid',
                    hideHeaders:${viewConfig.hideHeadersGrid},
                    frame: false,
                    plugins: [this.editing],
                    dockedItems: [{
                        weight: 2,
                        xtype: 'toolbar',
                        dock: 'bottom',
                        items: [{
                            xtype: 'tbtext',
                            text: '<b>@lacv</b>'
                        }, '|',
                        comboboxLimit
                        <c:if test="${viewConfig.editableGrid && viewConfig.visibleAddButtonInGrid}">
                        ,{
                        //iconCls: 'icon-add',
                        text: 'Agregar',
                        scope: this,
                        hidden: (typeView==="Child"),
                        handler: this.onAddClick
                        },
                        </c:if>
                        <c:if test="${viewConfig.editableGrid && viewConfig.visibleRemoveButtonInGrid}">
                        {
                            //iconCls: 'icon-delete',
                            text: 'Eliminar',
                            disabled: true,
                            itemId: 'delete',
                            scope: this,
                            handler: this.onDeleteClick
                        }
                        </c:if>
                        
                        <c:if test="${viewConfig.visibleExportButton}">
                        ,{
                            text: 'Exportar',
                            hidden: (typeView==="Child"),
                            //iconCls: 'add16',
                            menu: [
                                {text: 'A xls', handler: function(){this.exportTo('xls');}, scope: this},
                                {text: 'A json', handler: function(){this.exportTo('json');}, scope: this},
                                {text: 'A xml', handler: function(){this.exportTo('xml');}, scope: this}]
                        }
                        </c:if>
                        <c:if test="${viewConfig.editableGrid}">
                        , {
                            text: 'Auto-Guardar',
                            enableToggle: ${viewConfig.defaultAutoSave},
                            hidden: (typeView==="Child"),
                            pressed: true,
                            tooltip: 'When enabled, Store will execute Ajax requests as soon as a Record becomes dirty.',
                            scope: this,
                            toggleHandler: function(btn, pressed){
                                this.store.autoSync = pressed;
                            }
                        }, {
                            iconCls: 'icon-save',
                            text: 'Guardar',
                            scope: this,
                            handler: this.onSync
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
                    columns: columns,
                    getEmptyRec: getEmptyRec
                });
                this.callParent();
                this.getSelectionModel().on('selectionchange', this.onSelectChange, this);
            },

            onSelectChange: function(selModel, selections){
                if(this.down('#delete')!==null){
                    this.down('#delete').setDisabled(selections.length === 0);
                }
            },

            onSync: function(){
                this.store.sync();
            },

            onDeleteClick: function(){
                var selection = this.getView().getSelectionModel().getSelection()[0];
                if (selection) {
                    this.store.getProxy().extraParams.idEntity= selection.data.id;
                    this.store.remove(selection);
                    parentExtController.loadFormData("");
                }
            },

            onAddClick: function(){
                var rec = this.getEmptyRec(), edit = this.editing;
                edit.cancelEdit();
                this.store.insert(0, rec);
                edit.startEditByPosition({
                    row: 0,
                    column: 0
                });
            },
            
            exportTo: function(type){
                this.fireEvent('export', type);
            }
            
        });
    };
    </c:if>
    
    function ${labelField}EntityRender(value, p, record){
        if(record){
            if(Instance.typeView==="Parent"){
                return "<a style='font-size: 15px;' href='#?id="+record.data.id+"&tab=0'>"+value+"</a>";
            }else{
                return value;
            }
        }else{
            return value;
        }
    };
    
    Instance.createMainView= function(){
        Instance.formContainer= null;
        <c:if test="${viewConfig.visibleForm}">
        Instance.menuProcesses= getTreeMenuProcesses();
        </c:if>
        
        <c:if test="${viewConfig.visibleGrid}">
        Instance.gridContainer = getGridContainer(Instance.modelName, Instance.store, Instance.formContainer);
        Instance.store.gridContainer= Instance.gridContainer;
        Instance.filters= getFiltersPanel();
        </c:if>

        Instance.tabsContainer= Ext.widget('tabpanel', {
            region: 'center',
            activeTab: 0,
            style: 'background-color:#dfe8f6; margin:0px',
            defaults: {bodyStyle: 'padding:15px', autoScroll:true},
            items:[
                <c:if test="${viewConfig.visibleForm}">
                {
                    title: 'Gestionar Procesos',
                    layout: 'border',
                    bodyStyle: 'padding:0px',
                    items:[
                        Instance.menuProcesses,
                        {
                            id: 'content-processes',
                            region: 'center',
                            layout: 'card',
                            margins: '0 0 0 0',
                            autoScroll: true,
                            activeItem: 0,
                            border: false,
                            items: [
                                <c:forEach var="processName" items="${nameProcesses}">
                                getFormContainer${processName.key}("${processName.key}Model", Instance.store, Instance.childExtControllers),
                                </c:forEach>
                            ]
                       }
                    ]
                },
                </c:if>
                <c:if test="${viewConfig.visibleGrid}">
                {
                    title: 'Solicitudes',
                    layout: 'border',
                    bodyStyle: 'padding:0px',
                    items: [
                        Instance.filters,
                        Instance.gridContainer
                    ]
                }
                </c:if>
            ],
            listeners: {
                tabchange: function(tabPanel, tab){
                    var idx = tabPanel.items.indexOf(tab);
                    var url= util.addUrlParameter(parentExtController.request,"tab", idx);
                    if(url!==""){
                        mvcExt.navigate(url);
                    }
                }
            }
        });
        
        Instance.mainView= {
            id: Instance.id,
            title: '${viewConfig.mainProcessTitle}',
            frame: false,
            layout: 'border',
            items: [
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