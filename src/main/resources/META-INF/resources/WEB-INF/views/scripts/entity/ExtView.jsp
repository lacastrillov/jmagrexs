<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<script>

function ${entityName}ExtView(parentExtController, parentExtView){
    
    var Instance= this;
    
    Instance.id= "/${entityRef}";
    
    Instance.modelName="${entityName}Model";
    
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
        Instance.pluralEntityTitle= '${viewConfig.pluralEntityTitle}';
        Instance.singularEntityTitle= '${viewConfig.singularEntityTitle}';
        Instance.entityExtModel.defineModel(Instance.modelName);
        Instance.store= Instance.entityExtStore.getStore(Instance.modelName);
        Instance.massiveUpdateExecuteInProgress= false;
        <c:if test="${viewConfig.activeGridTemplate}">
        Instance.gridModelName= "${entityName}TemplateModel";
        Instance.entityExtModel.defineTemplateModel(Instance.gridModelName);
        Instance.gridStore= Instance.entityExtStore.getTemplateStore(Instance.gridModelName);
        </c:if>
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
    
    <c:if test="${viewConfig.activeNNMulticheckChild}">
    Instance.clearNNMultichecks= function(){
        var checkboxGroup=Ext.getCmp('checkboxGroup${viewConfig.entityRefNNMulticheckChild}In${entityName}');
        if(checkboxGroup.items.length > 0 && checkboxGroup.items.items.length > 0){
            checkboxGroup.items.items.forEach(function(checkbox){
                checkbox.activeChange=false;
                checkbox.setValue(false);
                checkbox.activeChange=true;
            });
        }
    }
    
    Instance.findAndLoadNNMultichecks= function(filter){
        Instance.entityExtStore.find(filter, "", function(responseText){
            if(responseText.success){
                responseText.data.forEach(function(item){
                    var itemCheckValue= item.${viewConfig.entityRefNNMulticheckChild}.id;
                    var checkbox= Ext.getCmp('checkNN${viewConfig.entityRefNNMulticheckChild}'+itemCheckValue);
                    checkbox.activeChange=false;
                    checkbox.setValue(true);
                    checkbox.activeChange=true;
                });
            }
        });
    };
    </c:if>
    
    <c:if test="${viewConfig.visibleForm}">
    function getFormContainer(childExtControllers){
        var formFields= ${jsonFormFields};

        var additionalButtons= ${jsonInternalViewButtons};

        Instance.defineWriterForm(formFields, additionalButtons);
        
        var itemsForm= [{
            itemId: 'form${entityName}',
            xtype: 'writerform${entityName}',
            <c:if test="${onlyForm}">
            title: '${viewConfig.singularEntityTitle}',
            </c:if>
            border: false,
            width: '100%',
            listeners: {
                render: function(panel) {
                    Instance.commonExtView.enableManagementTabHTMLEditor();
                }
            }
        }];
        
        if(Instance.typeView==="Parent"){
            itemsForm.push(getChildsExtViewTabs(childExtControllers));
        }
        
        return Ext.create('Ext.container.Container', {
            id: 'formContainer${entityName}',
            title: 'Formulario',
            type: 'fit',
            align: 'stretch',
            items: itemsForm
        });
    };
    
    function getChildsExtViewTabs(childExtControllers){
        var items=[];
        var jsonTypeChildExtViews= ${jsonTypeChildExtViews};
        childExtControllers.forEach(function(childExtController) {
            var itemTab= null;
            if(jsonTypeChildExtViews[childExtController.entityRef]==="tcv_1_to_n"){
                itemTab= {
                    id: childExtController.entityRef+'SubEntity',
                    xtype:'tabpanel',
                    title: childExtController.entityExtView.pluralEntityTitle,
                    plain:true,
                    activeTab: 0,
                    height: 600,
                    style: 'background-color:#dfe8f6; padding:10px;',
                    defaults: {bodyStyle: 'padding:15px', autoScroll:true},
                    items:[
                        childExtController.entityExtView.gridContainer,

                        childExtController.entityExtView.formContainer
                    ]
                };
            }else if(jsonTypeChildExtViews[childExtController.entityRef]==="tcv_1_to_1"){
                itemTab= Ext.widget('tabpanel',{
                    xtype:'tabpanel',
                    title: childExtController.entityExtView.singularEntityTitle,
                    plain:true,
                    activeTab: 0,
                    style: 'background-color:#dfe8f6; padding:5px;',
                    defaults: {bodyStyle: 'padding:0px', autoScroll:true},
                    items:[
                        childExtController.entityExtView.formContainer
                    ]
                });
                var buttonCreate = childExtController.entityExtView.formComponent.down("#create"+childExtController.entityName);
                buttonCreate.setVisible(false);
                itemTab.getTabBar().hide();
            }else if(jsonTypeChildExtViews[childExtController.entityRef]==="tcv_n_to_n"){
                itemTab= childExtController.entityExtView.checkboxGroupContainer;
            }
            
            items.push(itemTab);
        });
        
        var tabObect= {
            xtype:'tabpanel',
            plain:true,
            activeTab: 0,
            tools: [
                {type:'toggle'}
            ],
            style: 'padding:25px 15px 45px 15px;',
            items:items
        };
        
        return tabObect;
    };
    
    Instance.setFormActiveRecord= function(record){
        Instance.formComponent.setActiveRecord(record || null);
    };
    
    Instance.defineWriterForm= function(fields, additionalButtons){
        Ext.define('WriterForm${entityName}', {
            extend: 'Ext.form.Panel',
            alias: 'widget.writerform${entityName}',

            requires: ['Ext.form.field.Text'],

            initComponent: function(){
                //this.addEvents('create');
                
                var buttons= [];
                buttons= [
                <c:if test="${viewConfig.editableForm}">
                {
                    itemId: 'save${entityName}',
                    iconCls: 'icon-save',
                    text: 'Actualizar',
                    disabled: true,
                    scope: this,
                    handler: this.onUpdate
                },
                <c:if test="${not viewConfig.preloadedForm}">
                {
                    itemId: 'create${entityName}',
                    //iconCls: 'icon-user-add',
                    text: 'Crear',
                    scope: this,
                    handler: this.onCreate
                }, {
                    //iconCls: 'icon-reset',
                    text: 'Limpiar',
                    scope: this,
                    handler: this.onReset
                },
                </c:if>
                </c:if>
                <c:if test="${viewConfig.visibleSeeAllButton}">
                {
                    text: '&#x25BC; Ver todo',
                    scope: this,
                    handler: this.onSeeAll
                },
                </c:if>
                '|'];
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
                if (this.activeRecord) {
                    if(this.down('#save${entityName}')!==null){
                        this.down('#save${entityName}').enable();
                    }
                    this.getForm().loadRecord(this.activeRecord);
                } else {
                    if(this.down('#save${entityName}')!==null){
                        this.down('#save${entityName}').disable();
                    }
                    this.getForm().reset();
                }
            },
                    
            getActiveRecord: function(){
                return this.activeRecord;
            },
            
            onUpdate: function(){
                var active = this.activeRecord,
                    form = this.getForm();
            
                if (!active) {
                    return;
                }
                if (form.isValid()) {
                    parentExtController.saveFormData('update', form.getValues());
                    //form.updateRecord(active);
                    //this.onReset();
                }
            },

            onCreate: function(){
                var form = this.getForm();

                if (form.isValid()) {
                    parentExtController.saveFormData('create', form.getValues());
                    //form.reset();
                }

            },

            onReset: function(){
                parentExtController.loadFormData("");
            },
                    
            onSeeAll: function(){
                if(ExtJSVersion===4){
                    this.doLayout();
                }else{
                    this.updateLayout();
                }
            }
    
        });
        
    };
    
    </c:if>
    
    <c:if test="${viewConfig.visibleGrid}">
    function getGridContainer(){
        var idGrid= 'grid${entityName}';
        var gridColumns= ${jsonGridColumns};
        
        Instance.emptyModel= ${jsonEmptyModel};
        Instance.getEmptyRec= function(){
            return new ${entityName}Model(Instance.emptyModel);
        };
        
        var store= Instance.store;
        <c:if test="${viewConfig.activeGridTemplate}">
        store= Instance.gridStore;
        </c:if>

        Instance.defineWriterGrid('${viewConfig.pluralEntityTitle}', gridColumns);
        
        return Ext.create('Ext.container.Container', {
            id: 'gridContainer${entityName}',
            title: 'Listado',
            <c:if test="${viewConfig.gridHeightChildView != 0}">
            height: ${viewConfig.gridHeightChildView},
            </c:if>
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [{
                itemId: idGrid,
                xtype: 'writergrid${entityName}',
                style: 'border: 0px',
                flex: 1,
                store: store,
                disableSelection: ${viewConfig.activeGridTemplate},
                trackMouseOver: !${viewConfig.activeGridTemplate},
                <c:if test="${viewConfig.gridAutoReloadInterval > 1000}">
                viewConfig: {
                    loadMask: false
                },
                </c:if>
                listeners: {
                    selectionchange: function(selModel, selected) {
                        if(selected[0] && parentExtController.typeController==="Child"){
                            parentExtController.setFormData(selected[0]);
                        }
                    },
                    export: function(typeReport){
                        var filterData= JSON.stringify(parentExtController.filter);
                        filterData= filterData.replaceAll("{","(").replaceAll("}",")");
                        filterData= filterData.replaceAll("\\[","<").replaceAll("\\]",">");
                        var data= "?filter="+filterData;
                        data+="&limit="+store.pageSize+"&page="+store.currentPage;
                        data+="&sort="+store.getOrderProperty()+"&dir="+store.getOrderDir();
                        
                        switch(typeReport){
                            case "json":
                                var urlFind= store.proxy.api.read;
                                window.open(urlFind+data,'_blank');
                                break;
                            case "xml":
                                var urlFind= store.proxy.api.read.replace("find.htm","find/xml.htm");
                                window.open(urlFind+data,'_blank');
                                break;
                            case "csv":
                                var urlFind= store.proxy.api.read.replace("find.htm","find/csv.htm");
                                window.open(urlFind+data,'_blank');
                                break;
                            case "xlsx":
                                var urlFind= store.proxy.api.read.replace("find.htm","find/xlsx.htm");
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
    
    Instance.setValueInEmptyModel= function(fieldName, value){
        Instance.emptyModel[fieldName]= value;
        Instance.getEmptyRec= function(){
            return new ${entityName}Model(Instance.emptyModel);
        };
    };
    
    Instance.createEmptyRecUpdater= function(){
        if(Instance.emptyModelUpdater===undefined){
            Instance.emptyModelUpdater= ${jsonDefaultModel};
            Instance.getEmptyRecUpdater= function(){
                return new ${entityName}Model(Instance.emptyModelUpdater);
            };
        }
    };
    
    function getComboboxLimit(store){
        var combobox= Instance.commonExtView.getSimpleCombobox('limit', 'L&iacute;mite', '${entityRef}config', [50, 100, 200, 500], true);
        combobox.addListener('change',function(record){
            if(record.getValue()!=="" && store.pageSize!==record.getValue()){
                store.pageSize=record.getValue();
                Instance.reloadPageStore(1);
            }
        }, this);
        combobox.labelWidth= 46;
        combobox.width= 125;
        combobox.setValue(${viewConfig.maxResultsPerPage});
        
        return combobox;
    }
    
    function getComboboxOrderBy(store){
        var combobox= Instance.commonExtView.getSimpleCombobox('sort', 'Ordenar por', '${entityRef}config', ${sortColumns}, true);
        combobox.addListener('change',function(record){
            if(record.getValue()!=="" && store.getOrderProperty()!==record.getValue()){
                var dir= store.getOrderDir();
                store.sortBy(record.getValue(), dir);
                Instance.reloadPageStore(1);
            }
        }, this);
        combobox.setValue("${viewConfig.defaultOrderBy}");
        
        return combobox;
    }
    
    function getComboboxOrderDir(store){
        var combobox= Instance.commonExtView.getSimpleCombobox('dir', 'Direcci&oacute;n', '${entityRef}config', ["ASC", "DESC"], true);
        combobox.addListener('change',function(record){
            if(record.getValue()!=="" && store.getOrderDir()!==record.getValue()){
                var prop= store.getOrderProperty();
                store.sortBy(prop, record.getValue());
                Instance.reloadPageStore(1);
            }
        }, this);
        combobox.setValue("${viewConfig.defaultOrderDir}");
        
        return combobox;
    }
    
    Instance.defineWriterGrid= function(modelText, columns){
        Ext.define('WriterGrid${entityName}', {
            extend: 'Ext.grid.Panel',
            alias: 'widget.writergrid${entityName}',

            requires: [
                'Ext.grid.plugin.CellEditing',
                'Ext.selection.CheckboxModel',
                'Ext.form.field.Text',
                'Ext.toolbar.TextItem'
            ],

            initComponent: function(){
                
                Instance.cellEditing = Ext.create('Ext.grid.plugin.CellEditing');
                
                Ext.apply(this, {
                    //iconCls: 'icon-grid',
                    hideHeaders:${viewConfig.hideHeadersGrid},
                    frame: false,
                    selType: 'checkboxmodel',
                    plugins: [Instance.cellEditing],
                    dockedItems: [{
                        weight: 2,
                        xtype: 'toolbar',
                        margin  : '5 0 5 0',
                        dock: 'top',
                        items: [{
                            xtype: 'tbtext',
                            text: '<b>@lacv</b>'
                        }, '|',
                        <c:if test="${viewConfig.editableGrid && viewConfig.visibleAddButtonInGrid}">
                        {
                            xtype: 'splitbutton',
                            text: 'Nuevo',
                            handler: this.onNewClick,
                            menu: [{
                                text: 'Agregar',
                                scope: this,
                                handler: this.onAddClick
                            }]
                        },
                        </c:if>
                        <c:if test="${viewConfig.editableGrid && viewConfig.visibleMassiveUpdateButton}">
                        {
                            itemId: 'massiveUpdateButton',
                            iconCls: 'icon-save',
                            xtype: 'splitbutton',
                            text: 'Actualizaci&oacute;n masiva',
                            scope: this,
                            handler: this.onMassiveUpdate,
                            menu: [{
                                text: 'Ejecutar',
                                scope: this,
                                handler: this.onMassiveUpdateExecute
                            },{
                                text: 'Cancelar',
                                scope: this,
                                handler: this.onMassiveUpdateCancel
                            }]
                        },
                        </c:if>
                        <c:if test="${viewConfig.editableGrid && !viewConfig.defaultAutoSave}">
                        {
                            iconCls: 'icon-save',
                            text: 'Guardar',
                            scope: this,
                            handler: this.onSync
                        },
                        </c:if>
                        <c:if test="${viewConfig.visibleRemoveButtonInGrid}">
                        {
                            //iconCls: 'icon-delete',
                            text: 'Eliminar',
                            <c:if test="${not viewConfig.activeGridTemplate}">
                            disabled: true,
                            </c:if>
                            itemId: 'delete',
                            scope: this,
                            handler: this.onDeleteClick
                        },
                        </c:if>
                        <c:if test="${viewConfig.gridAutoReloadInterval > 1000}">
                        {
                            text: 'Auto-recarga',
                            enableToggle: true,
                            pressed: true,
                            tooltip: 'Cuando est&aacute; activa, la tabla se recarga automaticamente cada ${viewConfig.gridAutoReloadInterval} milisegundos.',
                            scope: this,
                            toggleHandler: function(btn, pressed){
                                this.store.gridAutoReload = pressed;
                                if(pressed){
                                    parentExtController.doGridAutoReload();
                                }
                            }
                        },
                        </c:if>
                        <c:if test="${fn:length(viewConfig.processGlobalActions)>0}">
                        {
                            text: 'Acciones Globales',
                            //iconCls: 'add16',
                            menu: ${jsonGlobalActions}
                        },
                        </c:if>
                        getComboboxLimit(this.store),
                        {
                            text: 'Ordenar',
                            //iconCls: 'add16',
                            menu: [
                                getComboboxOrderBy(this.store),
                                getComboboxOrderDir(this.store)]
                        }
                        <c:if test="${viewConfig.visibleExportButton}">
                        ,{
                            text: 'Exportar',
                            //iconCls: 'add16',
                            menu: [
                                {text: 'A CSV', handler: function(){this.exportTo('csv');}, scope: this},
                                {text: 'A Excel', handler: function(){this.exportTo('xlsx');}, scope: this},
                                {text: 'A JSON', handler: function(){this.exportTo('json');}, scope: this},
                                {text: 'A XML', handler: function(){this.exportTo('xml');}, scope: this}]
                        },{
                            itemId: 'importMenu',
                            text: 'Importar',
                            //iconCls: 'add16',
                            menu: [
                                {text: 'De CSV', handler: function(){this.importFrom('csv');}, scope: this},
                                {text: 'De Excel', handler: function(){this.importFrom('xlsx');}, scope: this},
                                {text: 'De JSON', handler: function(){this.importFrom('json');}, scope: this},
                                {text: 'De XML', handler: function(){this.importFrom('xml');}, scope: this}]
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
                if(this.down('#delete')!==null){
                    this.down('#delete').setDisabled(selections.length === 0);
                }
            },

            onSync: function(){
                this.store.sync();
            },
            
            onDeleteClick: function(){
                parentExtController.deleteRecords();
            },
            
            onNewClick: function(){
                parentExtController.idEntitySelected= null;
                if(Instance.typeView==="Parent"){
                    mvcExt.navigate("?tab=1&id=");
                }else{
                    Ext.getCmp("${entityRef}TabsContainer").clickInTab("Formulario");
                    parentExtController.loadFormData("");
                }
            },

            onAddClick: function(){
                if(parentExtController.typeController==="Child" && parentExtController.parentEntityId===null){
                    Ext.MessageBox.alert('Operaci&oacute;n cancelada', "No se ha seleccionado "+parentExtController.parentEntityTitle+" padre!!!");
                }else{
                    this.store.proxy.writer.writeAllFields= true;
                    var edit = Instance.cellEditing;
                    var rec = Instance.getEmptyRec();
                    edit.cancelEdit();
                    this.store.insert(0, rec);
                    edit.startEditByPosition({row:0, column:0});
                }
            },
                
            onMassiveUpdate: function(){
                if(parentExtController.typeController==="Child" && parentExtController.parentEntityId===null){
                    Ext.MessageBox.alert('Operaci&oacute;n cancelada', "No se ha seleccionado "+parentExtController.parentEntityTitle+" padre!!!");
                }else{
                    if(!Instance.massiveUpdateExecuteInProgress){
                        Instance.massiveUpdateExecuteInProgress= true;
                        Instance.gridComponent.store.autoSync= false;
                        Instance.gridComponent.down('#massiveUpdateButton').setIconCls('icon-red');
                        var apiUpdate= Instance.gridComponent.store.proxy.api.update;
                        Instance.gridComponent.store.proxy.api.update= apiUpdate.replaceAll("update.htm","update/byfilter.htm");
                        console.log(Instance.gridComponent.store.proxy.api.update);
                    }
                    if(this.store.getAt(0)!==undefined && this.store.getAt(0).get("id")!==-1 && this.store.getAt(0).get("id")!=="-1"){
                        //Agregar registro en editor
                        Instance.createEmptyRecUpdater();
                        var edit= Instance.cellEditing;
                        edit.cancelEdit();
                        var rec = Instance.getEmptyRecUpdater();
                        this.store.insert(0, rec);
                        edit.startEdit();
                    }
                }
            },
            
            onMassiveUpdateExecute: function(){
                if(Instance.massiveUpdateExecuteInProgress){
                    Ext.MessageBox.confirm('Confirmar', 'Esta seguro que desea actualizar '+this.store.getTotalCount()+' registros?', function(result){
                        if(result==="yes"){
                            Instance.gridComponent.store.sync({
                                success: function(){
                                    Instance.reloadPageStore(1);
                                    console.log("success!!");
                                },
                                scope: this
                            });
                            Instance.gridComponent.down('#massiveUpdateButton').setIconCls('icon-save');
                            Instance.massiveUpdateExecuteInProgress= false;
                            Instance.gridComponent.store.autoSync= ${viewConfig.defaultAutoSave};
                            var apiUpdateByFilter= Instance.gridComponent.store.proxy.api.update;
                            Instance.gridComponent.store.proxy.api.update= apiUpdateByFilter.replaceAll("update/byfilter.htm","update.htm");
                        }
                    });
                }else{
                    Ext.MessageBox.alert('Operaci&oacute;n cancelada', "No hay Actualizaci&oacute;n masiva en progreso!!!");
                }
            },
            
            onMassiveUpdateCancel: function(){
                var apiUpdate= Instance.gridComponent.store.proxy.api.update;
                if(Instance.massiveUpdateExecuteInProgress || apiUpdate.indexOf("update/byfilter.htm")!==-1){
                    Instance.gridComponent.down('#massiveUpdateButton').setIconCls('icon-save');
                    Instance.massiveUpdateExecuteInProgress= false;
                    Instance.gridComponent.store.autoSync= ${viewConfig.defaultAutoSave};
                    Instance.gridComponent.store.proxy.api.update= apiUpdate.replaceAll("update/byfilter.htm","update.htm");
                    Instance.reloadPageStore(1);
                }else{
                    Ext.MessageBox.alert('Operaci&oacute;n cancelada', "No hay Actualizaci&oacute;n masiva en progreso!!!");
                }
            },
            
            exportTo: function(type){
                this.fireEvent('export', type);
            },
            
            importFrom: function(type){
                if (Instance.containerImport.isVisible()) {
                    Instance.containerImport.hide(this.down('#importMenu'), function() {});
                } else {
                    Instance.containerImport.typeReport= type;
                    Instance.containerImport.show(this.down('#importMenu'), function() {});
                }
            }

        });
    };
    
    function createFormImport(){
        Instance.formImport = Ext.create('Ext.form.Panel', {
            border: false,
            bodyPadding: 15,
            fieldDefaults: {
                labelAlign: 'left',
                anchor: '100%'
            },
            items: [{
                xtype: 'filefield',
                name: 'data',
                fieldLabel: 'Seleccione archivo',
                labelWidth: 125,
                style: 'margin-top:20px',
                allowBlank: false
            }]
        });

        Instance.containerImport = Ext.create('Ext.window.Window', {
            autoShow: false,
            title: 'Subir Archivo',
            closable: true,
            closeAction: 'hide',
            width: 600,
            height: 200,
            minWidth: 300,
            minHeight: 200,
            layout: 'fit',
            plain:true,
            typeReport: 'json',
            items: Instance.formImport,

            buttons: [{
                text: 'Importar',
                handler: function(){
                    Instance.entityExtStore.import(Instance.formImport, Instance.containerImport.typeReport, function(responseText){
                        Ext.MessageBox.alert('Status', responseText.message);
                        Instance.reloadPageStore(Instance.store.currentPage);
                        setTimeout(function(){ Instance.containerImport.hide()},1000);
                    });
                }
            },{
                text: 'Cancelar',
                handler: function(){
                    Instance.containerImport.hide();
                }
            }]
        });
    }
    </c:if>

    <c:if test="${viewConfig.activeNNMulticheckChild}">
    function getCheckboxGroupContainer(){
        var checkboxGroupContainer= Ext.create('Ext.container.Container', {
            title: Instance.${viewConfig.entityRefNNMulticheckChild}ExtInterfaces.pluralEntityTitle,
            style: 'background-color:#dfe8f6; padding:10px;',
            layout: 'anchor',
            defaults: {
                anchor: '100%'
            },
            items: [
                Instance.${viewConfig.entityRefNNMulticheckChild}ExtInterfaces.getCheckboxGroup('${entityName}', '${viewConfig.entityRefNNMulticheckChild}',
                function (checkbox, isChecked) {
                    if(checkbox.activeChange){
                        var record= Ext.create(Instance.modelName);
                        if(Object.keys(parentExtController.filter.eq).length !== 0){
                            for (var key in parentExtController.filter.eq) {
                                record.data[key]= parentExtController.filter.eq[key];
                            }
                        }
                        record.data[checkbox.name]= checkbox.inputValue;
                        if(isChecked){
                            parentExtController.saveFormData('create', record.data);
                        }else{
                            var filter= record.data;
                            delete filter["id"];
                            Instance.entityExtStore.deleteByFilter(JSON.stringify({"eq":filter}), function(responseText){
                                console.log(responseText.data);
                            });
                        }
                    }
                })
            ]
        });
        
        return checkboxGroupContainer;
    }
    </c:if>
    
    function getPropertyGrid(){
        var renderers= {
            <c:forEach var="associatedER" items="${interfacesEntityRef}">
            "${titledFieldsMap[associatedER]}": function(entity){
                var res = entity.split("__");
                return '<a href="${serverDomain.applicationContext}${serverDomain.adminContext}${serverDomain.adminPath}/${associatedER}/entity.htm#?tab=1&id='+res[0]+'">'+res[1]+'</a>';
            },
            </c:forEach>
        };
        var pg= Ext.create('Ext.grid.property.Grid', {
            id: 'propertyGrid${entityName}',
            region: 'north',
            hideHeaders: true,
            resizable: false,
            defaults: {
                sortable: false
            },
            customRenderers: renderers,
            disableSelection:true,
            listeners: {
                'beforeedit':{
                    fn:function(){
                        return false;
                    }
                }
            }
        });
        pg.getStore().sorters.items= [];
        
        return pg;
    };
    
    <c:forEach var="processForm" items="${viewConfig.processForms}">
    function getForm${processForm.processName}Process(){
        
        var processForm = Ext.create('Ext.form.Panel', {
            itemId: 'form${processForm.processName}Process',
            defaultType: 'textfield',
            border: false,
            bodyPadding: 15,
            autoScroll: true,
            fieldDefaults: {
                minWidth: 300,
                anchor: '100%',
                labelAlign: 'right'
            },

            items: ${jsonFormFieldsProcessMap[processForm.processName]}
        });

        var win = Ext.create('Ext.window.Window', {
            autoShow: false,
            title: '${processForm.processTitle}',
            closable: true,
            closeAction: 'hide',
            width: '50%',
            height: 500,
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
                    if (processForm.getForm().isValid()) {
                        var jsonData= processForm.getForm().getValues();
                        Instance.entityExtStore.doProcess('${processForm.mainProcessRef}', '${processForm.processName}', jsonData, function(responseText){
                            Ext.MessageBox.alert('Status', responseText);
                            parentExtController.loadGridData();
                            win.hide();
                        });
                    }
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
    
    Instance.showGlobalProcessForm= function(processName, idsField){
        var initData={};
        var ids= "";
        var selection = Instance.gridComponent.getSelectionModel().getSelection();
        if (selection.length>0) {
            for(var i=0; i<selection.length; i++){
                ids+=selection[i].data.id;
                if(i<selection.length-1){
                    ids+=",";
                }
            }
        }else{
            var check_items= document.getElementsByClassName("item_check");
            for(var i=0; i<check_items.length; i++){
                if(check_items[i].checked){
                    ids+=check_items[i].value;
                    if(i<selection.length-1){
                        ids+=",";
                    }
                }
            }
        }
        initData[idsField]= ids;
        Instance.processForms[processName].show(null, function() {});
        var processForm= Instance.processForms[processName].child('#form'+processName+'Process');
        processForm.getForm().reset();
        processForm.getForm().setValues(initData);
    };
    
    function ${labelField}EntityRender(value, p, record){
        if(record){
            if(Instance.typeView==="Parent"){
                return "<a style='font-size: 15px;' href='#?id="+record.data.id+"&tab=1'>"+value+"</a>";
            }else{
                return "<a style='font-size: 15px;' href='javascript:Ext.getCmp(\"${entityRef}TabsContainer\").clickInTab(\"Formulario\")'>"+value+"</a>";
            }
        }else{
            return value;
        }
    };
    
    Instance.hideParentField= function(entityRef){
        if(Instance.formComponent!==null){
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
        <c:forEach var="associatedER" items="${interfacesEntityRef}">
            <c:set var="associatedEntityName" value="${fn:toUpperCase(fn:substring(associatedER, 0, 1))}${fn:substring(associatedER, 1,fn:length(associatedER))}"></c:set>
            <c:set var="associatedEntityTitle" value="${titledFieldsMap[associatedER]}"></c:set>
        Instance.${associatedER}ExtInterfaces= new ${associatedEntityName}ExtInterfaces(parentExtController, Instance);
        Instance.formCombobox${associatedEntityName}= Instance.${associatedER}ExtInterfaces.getCombobox('form', '${entityName}', '${associatedER}', '${associatedEntityTitle}');
        Instance.gridCombobox${associatedEntityName}= Instance.${associatedER}ExtInterfaces.getCombobox('grid', '${entityName}', '${associatedER}', '${associatedEntityTitle}');
        Instance.filterCombobox${associatedEntityName}= Instance.${associatedER}ExtInterfaces.getCombobox('filter', '${entityName}', '${associatedER}', '${associatedEntityTitle}');
        Instance.filterMultiselect${associatedEntityName}= Instance.${associatedER}ExtInterfaces.getMultiselect('${entityName}', '${associatedER}', '${associatedEntityTitle}');
        Instance.combobox${associatedEntityName}Render= Instance.${associatedER}ExtInterfaces.getComboboxRender('grid');
        </c:forEach>
        
        Instance.entityDependency= {};
        <c:forEach var="entry" items="${viewConfig.comboboxChildDependent}">
            <c:set var="parentEntityName" value="${fn:toUpperCase(fn:substring(entry.key, 0, 1))}${fn:substring(entry.key, 1,fn:length(entry.key))}"></c:set>
            <c:forEach var="childEntityRef" items="${entry.value}">
                <c:set var="childEntityName" value="${fn:toUpperCase(fn:substring(childEntityRef, 0, 1))}${fn:substring(childEntityRef, 1,fn:length(childEntityRef))}"></c:set>
        Instance.entityDependency["${childEntityRef}"]="${entry.key}";
        Instance.formCombobox${parentEntityName}.comboboxDependent.push(Instance.formCombobox${childEntityName});
        Instance.formCombobox${parentEntityName}.comboboxDependent.push(Instance.gridCombobox${childEntityName});
        
        Instance.gridCombobox${parentEntityName}.comboboxDependent.push(Instance.formCombobox${childEntityName});
        Instance.gridCombobox${parentEntityName}.comboboxDependent.push(Instance.gridCombobox${childEntityName});
        
        Instance.filterCombobox${parentEntityName}.comboboxDependent.push(Instance.filterCombobox${childEntityName});
            </c:forEach>
        </c:forEach>
        
        Instance.childExtControllers= [];
        
        if(Instance.typeView==="Parent"){
        <c:forEach var="childExtViewER" items="${viewsChildEntityRef}">
            <c:set var="childExtViewEN" value="${fn:toUpperCase(fn:substring(childExtViewER, 0, 1))}${fn:substring(childExtViewER, 1,fn:length(childExtViewER))}"></c:set>
            var ${childExtViewER}ExtController= new ${childExtViewEN}ExtController(parentExtController, Instance);
            ${childExtViewER}ExtController.parentEntityTitle= "${viewConfig.singularEntityTitle}";
            ${childExtViewER}ExtController.entityExtView.hideParentField("${entityRef}");
            Instance.childExtControllers.push(${childExtViewER}ExtController);
        </c:forEach>
        }
        
        Instance.formComponent= null;
        <c:if test="${viewConfig.visibleForm}">
        Instance.formContainer = getFormContainer(Instance.childExtControllers);
        Instance.formComponent= Instance.formContainer.child('#form${entityName}');
        Instance.store.formComponent= Instance.formComponent;
        </c:if>
        
        Instance.gridComponent = null;
        <c:if test="${viewConfig.visibleGrid}">
        Instance.gridContainer = getGridContainer();
        Instance.gridComponent = Instance.gridContainer.child('#grid${entityName}');
        Instance.store.gridComponent= Instance.gridComponent;
        createFormImport();
        </c:if>
            
        <c:if test="${viewConfig.activeNNMulticheckChild}">
        Instance.checkboxGroupContainer= getCheckboxGroupContainer();
        </c:if>
        
        Instance.processForms={};
        <c:forEach var="processForm" items="${viewConfig.processForms}">
        Instance.processForms["${processForm.processName}"]= getForm${processForm.processName}Process();
        </c:forEach>
        
        Instance.propertyGrid= getPropertyGrid();

        Instance.tabsContainer= Ext.widget('tabpanel', {
            id: "${entityRef}TabsContainer",
            region: 'center',
            activeTab: 0,
            style: 'background-color:#dfe8f6; margin:0px',
            defaults: {bodyStyle: 'padding:15px', autoScroll:true},
            items:[
                <c:if test="${viewConfig.visibleGrid}">
                Instance.gridContainer,
                </c:if>
                <c:if test="${viewConfig.visibleForm}">
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
            },
            clickInTab: function(labelTab){
                $("#${entityRef}SubEntity").find("span.x-tab-inner").each(function() {
                    if(this.innerText===labelTab){
                        util.eventFire(document.getElementById(this.id), "click");
                    }
                });
            }
        });
        <c:if test="${viewConfig.preloadedForm || onlyForm}">
        Instance.tabsContainer.getTabBar().hide();
        </c:if>
        
        Instance.mainView= {
            id: Instance.id,
            <c:if test="${not onlyForm}">
            title: 'Gestionar ${viewConfig.pluralEntityTitle}',
            </c:if>
            frame: false,
            layout: 'border',
            items: [
                Instance.propertyGrid,
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