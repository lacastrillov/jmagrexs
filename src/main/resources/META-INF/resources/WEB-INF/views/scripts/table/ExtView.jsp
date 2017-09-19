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
        Instance.entityExtModel.defineModel(Instance.modelName);
        Instance.store= Instance.entityExtStore.getStore(Instance.modelName);
        Instance.createMainView();
    };
    
    Instance.setFilterStore= function(filter){
        Instance.store.getProxy().extraParams.filter= filter;
    };
    
    Instance.reloadPageStore= function(page){
        Instance.store.loadPage(page);
    };
    
    <c:if test="${viewConfig.visibleForm}">
    function getFormContainer(modelName, store){
        var formFields= ${jsonFormFields};

        var renderReplacements= [];

        var additionalButtons= [];

        Instance.defineWriterForm(Instance.modelName, formFields, renderReplacements, additionalButtons);
        
        var itemsForm= [{
            itemId: 'form'+modelName,
            xtype: 'writerform'+modelName,
            border: false,
            width: '100%',
            listeners: {
                create: function(form, data){
                    Instance.entityExtStore.save('create', JSON.stringify(data), parentExtController.formSavedResponse);
                },
                update: function(form, data){
                    Instance.entityExtStore.save('update', JSON.stringify(data), parentExtController.formSavedResponse);
                },
                render: function(panel) {
                    Instance.commonExtView.enableManagementTabHTMLEditor();
                }
            }
        }];
        
        return Ext.create('Ext.container.Container', {
            id: 'formContainer'+modelName,
            title: 'Formulario',
            type: 'fit',
            align: 'stretch',
            items: itemsForm
        });
    };
    
    Instance.setFormActiveRecord= function(record){
        var formComponent= Instance.formContainer.child('#form'+Instance.modelName);
        formComponent.setActiveRecord(record || null);
    };
    
    Instance.defineWriterForm= function(modelName, fields, renderReplacements, additionalButtons){
        Ext.define('WriterForm'+modelName, {
            extend: 'Ext.form.Panel',
            alias: 'widget.writerform'+modelName,

            requires: ['Ext.form.field.Text'],

            initComponent: function(){
                //this.addEvents('create');
                
                var buttons= [];
                <c:if test="${viewConfig.editableForm}">
                buttons= [{
                    iconCls: 'icon-save',
                    itemId: 'save'+modelName,
                    text: 'Actualizar',
                    disabled: true,
                    scope: this,
                    handler: this.onSave
                }, {
                    //iconCls: 'icon-user-add',
                    text: 'Crear',
                    scope: this,
                    handler: this.onCreate
                }, {
                    //iconCls: 'icon-reset',
                    text: 'Limpiar',
                    scope: this,
                    handler: this.onReset
                },'|'];
                </c:if>
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
                    if(this.down('#save'+modelName)!==null){
                        this.down('#save'+modelName).enable();
                    }
                    this.getForm().loadRecord(this.activeRecord);
                    this.renderReplaceActiveRecord(this.activeRecord);
                } else {
                    if(this.down('#save'+modelName)!==null){
                        this.down('#save'+modelName).disable();
                    }
                    this.getForm().reset();
                }
            },
                    
            getActiveRecord: function(){
                return this.activeRecord;
            },
            
            onSave: function(){
                var active = this.activeRecord,
                    form = this.getForm();
            
                if (!active) {
                    return;
                }
                if (form.isValid()) {
                    this.fireEvent('update', this, form.getValues());
                    //form.updateRecord(active);
                    //this.onReset();
                }
            },

            onCreate: function(){
                var form = this.getForm();

                if (form.isValid()) {
                    this.fireEvent('create', this, form.getValues());
                    //form.reset();
                }

            },

            onReset: function(){
                this.getForm().reset();
                parentExtController.loadFormData("");
            },
            
            renderReplaceActiveRecord: function(record){
                if(renderReplacements){
                    for(var i=0; i<renderReplacements.length; i++){
                        var renderReplace= renderReplacements[i];
                        var replaceField= renderReplace.replace.field;
                        var replaceAttribute= renderReplace.replace.attribute;
                        var value="";
                        
                        if (typeof record.data[replaceField] === "object" && Object.getOwnPropertyNames(record.data[replaceField]).length === 0){
                            value= "";
                        }else if(replaceAttribute.indexOf(".")===-1 && replaceField in record.data){
                            value= record.data[replaceField][replaceAttribute];
                        }else{
                            var niveles= replaceAttribute.split(".");
                            try{
                                switch(niveles.length){
                                    case 2:
                                        value= record.data[replaceField][niveles[0]][niveles[1]];
                                        break;
                                    case 3:
                                        value= record.data[replaceField][niveles[0]][niveles[1]][niveles[2]];
                                        break;
                                    case 4:
                                        value= record.data[replaceField][niveles[0]][niveles[1]][niveles[2]][niveles[3]];
                                        break;
                                    case 5:
                                        value= record.data[replaceField][niveles[0]][niveles[1]][niveles[2]][niveles[3]][niveles[4]];
                                        break;
                                }
                            }catch(err){
                                console.log(err);
                            }
                            
                        }
                        if(typeof(value) !== 'undefined'){
                            renderReplace.component.setValue(value);
                        }
                    }
                }
                return record;
            }
    
        });
        
    };
    
    </c:if>
    
    <c:if test="${viewConfig.visibleGrid}">
    function getGridContainer(modelName, store, formContainer){
        var idGrid= 'grid'+modelName;
        var gridColumns= ${jsonGridColumns};
        
        Instance.emptyModel= ${jsonEmptyModel};
        Instance.getEmptyRec= function(){
            return new ${entityName}Model(Instance.emptyModel);
        };

        Instance.defineWriterGrid(modelName, '${viewConfig.pluralEntityTitle}', gridColumns);
        
        return Ext.create('Ext.container.Container', {
            id: 'gridContainer'+modelName,
            title: 'Listado',
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
                listeners: {
                    selectionchange: function(selModel, selected) {
                        if(formContainer!==null && selected[0]){
                            formContainer.child('#form'+modelName).setActiveRecord(selected[0]);
                        }
                    },
                    export: function(typeReport){
                        var filterData= JSON.stringify(parentExtController.filter);
                        filterData= filterData.replaceAll("{","(").replaceAll("}",")");
                        var data= "?filter="+filterData;
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
    
    Instance.setValueInEmptyModel= function(fieldName, value){
        Instance.emptyModel[fieldName]= value;
        Instance.getEmptyRec= function(){
            return new ${entityName}Model(Instance.emptyModel);
        };
    };
    
    function getComboboxLimit(store){
        var combobox= Instance.commonExtView.getSimpleCombobox('limit', 'L&iacute;mite', 'config', [50, 100, 200, 500]);
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
        var combobox= Instance.commonExtView.getSimpleCombobox('sort', 'Ordenar por', 'config', ${sortColumns});
        combobox.addListener('change',function(record){
            if(record.getValue()!=="" && store.getOrderProperty()!==record.getValue()){
                var dir= store.getOrderDir();
                store.sortBy(record.getValue(), dir);
                Instance.reloadPageStore(1);
            }
        }, this);
        combobox.labelWidth= 80;
        combobox.width= 250;
        combobox.setValue("${viewConfig.defaultOrderBy}");
        
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
        combobox.labelWidth= 60;
        combobox.width= 150;
        combobox.setValue("${viewConfig.defaultOrderDir}");
        
        return combobox;
    }
    
    Instance.defineWriterGrid= function(modelName, modelText, columns){
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
                        <c:if test="${viewConfig.editableGrid && viewConfig.visibleAddButtonInGrid}">
                        {
                            //iconCls: 'icon-add',
                            text: 'Agregar',
                            scope: this,
                            handler: this.onAddClick
                        },
                        </c:if>
                        <c:if test="${viewConfig.visibleRemoveButtonInGrid}">
                        {
                            //iconCls: 'icon-delete',
                            text: 'Eliminar',
                            disabled: true,
                            itemId: 'delete',
                            scope: this,
                            handler: this.onDeleteClick
                        },
                        </c:if>
                        
                        <c:if test="${viewConfig.visibleExportButton}">
                        {
                            text: 'Exportar',
                            //iconCls: 'add16',
                            menu: [
                                {text: 'A xls', handler: function(){this.exportTo('xls');}, scope: this},
                                {text: 'A json', handler: function(){this.exportTo('json');}, scope: this},
                                {text: 'A xml', handler: function(){this.exportTo('xml');}, scope: this}]
                        },
                        </c:if>
                        <c:if test="${viewConfig.editableGrid}">
                        {
                            text: 'Auto-Guardar',
                            enableToggle: ${viewConfig.defaultAutoSave},
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
                        },
                        </c:if>
                        getComboboxLimit(this.store),
                        getComboboxOrderBy(this.store),
                        getComboboxOrderDir(this.store)
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
                var selection = this.getView().getSelectionModel().getSelection()[0];
                if (selection) {
                    this.store.getProxy().extraParams.idEntity= selection.data.id;
                    this.store.remove(selection);
                    parentExtController.loadFormData("");
                }else{
                    var check_items= document.getElementsByClassName("item_check");
                    var filter={"in":{"id":[]}};
                    for(var i=0; i<check_items.length; i++){
                        if(check_items[i].checked){
                            filter.in.id.push(check_items[i].value);
                        }
                    }
                    if(filter.in.id.length>0){
                        Instance.entityExtStore.deleteByFilter(JSON.stringify(filter), function(responseText){
                            Instance.reloadPageStore(Instance.store.currentPage);
                        });
                    }
                }
            },

            onAddClick: function(){
                var rec = Instance.getEmptyRec(), edit = this.editing;
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
    
    function idEntityRender(value, p, record){
        if(record){
            return "<a style='font-size: 15px;' href='#?id="+record.data.id+"&tab=1'>"+value+"</a>";
        }else{
            return value;
        }
    };
    
    Instance.createMainView= function(){
        
        Instance.formContainer= null;
        <c:if test="${viewConfig.visibleForm}">
        Instance.formContainer = getFormContainer(Instance.modelName, Instance.store);
        Instance.store.formContainer= Instance.formContainer;
        </c:if>
        
        <c:if test="${viewConfig.visibleGrid}">
        Instance.gridContainer = getGridContainer(Instance.modelName, Instance.store, Instance.formContainer);
        Instance.store.gridContainer= Instance.gridContainer;
        </c:if>

        Instance.tabsContainer= Ext.widget('tabpanel', {
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
                    if(url!==""){
                        mvcExt.navigate(url);
                    }
                }
            }
        });
        
        Instance.mainView= {
            id: Instance.id,
            title: 'Gestionar ${viewConfig.pluralEntityTitle}',
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