<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<script>

function ${entityName}ExtView(parentExtController, parentExtView){
    
    var Instance= this;
    
    Instance.id= "/${entityRef}";
    
    Instance.modelName="${entityName}Model";
    
    var util= new Util();
    
    var fileUploader= new FileUploader();
    
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
    
    <c:if test="${viewConfig.visibleForm}">
    function getFormContainer(){
        var formFields= ${jsonFormFields};

        var renderReplacements= ${jsonRenderReplacements};

        Instance.defineWriterForm(formFields, renderReplacements);
        
        var itemsForm= [{
            itemId: 'form${entityName}',
            xtype: 'writerform${entityName}',
            border: false,
            width: '100%',
            listeners: {
                update: function(form, data){
                    Instance.entityExtStore.save('update', JSON.stringify(data), parentExtController.formSavedResponse);
                },
                render: function(panel) {
                    Instance.commonExtView.enableManagementTabHTMLEditor();
                }
            }
        }];
        
        return Ext.create('Ext.container.Container', {
            id: 'formContainer${entityName}',
            region: 'center',
            type: 'fit',
            align: 'stretch',
            items: itemsForm
        });
    };
    
    Instance.setFormActiveRecord= function(record){
        Instance.formComponent.setActiveRecord(record || null);
    };
    
    Instance.defineWriterForm= function(fields, renderReplacements){
        Ext.define('WriterForm${entityName}', {
            extend: 'Ext.form.Panel',
            alias: 'widget.writerform${entityName}',

            requires: ['Ext.form.field.Text'],

            initComponent: function(){
                //this.addEvents('create');
                
                var buttons= [];
                <c:if test="${viewConfig.editableForm}">
                buttons= [{
                    iconCls: 'icon-save',
                    itemId: 'save${entityName}',
                    text: 'Actualizar',
                    disabled: true,
                    scope: this,
                    handler: this.onSave
                }];
                </c:if>
                Ext.apply(this, {
                    activeRecord: null,
                    //iconCls: 'icon-user',
                    frame: false,
                    defaultType: 'textfield',
                    bodyPadding: 15,
                    fieldDefaults: {
                        minWidth: 300,
                        anchor: '95%',
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
                    this.renderReplaceActiveRecord(this.activeRecord);
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
    
    function getGridContainer(){
        var idGrid= 'grid${entityName}';
        var gridColumns= ${jsonGridColumns};
        
        Instance.getEmptyRec= function(){
            return new ${entityName}Model(${jsonEmptyModel});
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
    
    Instance.defineWriterGrid= function(modelText, columns){
        Ext.define('WriterGrid${entityName}', {
            extend: 'Ext.grid.Panel',
            alias: 'widget.writergrid${entityName}',

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
                        {
                            id: 'check-all',
                            xtype: 'checkbox',
                            boxLabel: 'Seleccionar todo',
                            listeners: {
                                change: function(checkbox, newValue, oldValue, eOpts) {
                                    util.checkAll(newValue);
                                }
                            }
                        },{
                            itemId: 'fileMenu',
                            text: 'Archivo',
                            //iconCls: 'add16',
                            menu: [
                                {text: 'Crear Entidad', menu:[
                                    <c:forEach var="entityRef" items="${viewConfig.refEntities}">
                                    {text: '${entityRef.value}', handler: function(){this.onCreateEntity('${entityRef.key}','${entityRef.value}');}, scope: this},
                                    </c:forEach>
                                ]},
                                {text: 'Crear Carpeta', handler: function(){this.onCreateFolder();}, scope: this},
                                <c:if test="${viewConfig.visibleRemoveButtonInGrid}">
                                {text: 'Eliminar', handler: function(){this.onDeleteClick();}, scope: this},
                                </c:if>
                                {text: 'Renombrar', handler: function(){this.onRenameFile();}, scope: this},
                                {text: 'Mover', handler: function(){this.onMoveFile();}, scope: this}]
                        },
                        getComboboxLimit(this.store),
                        getComboboxOrderBy(this.store),
                        getComboboxOrderDir(this.store)
                        <c:if test="${viewConfig.visibleExportButton}">
                        ,{
                            text: 'Exportar',
                            //iconCls: 'add16',
                            menu: [
                                {text: 'A Excel', handler: function(){this.exportTo('xls');}, scope: this},
                                {text: 'A JSON', handler: function(){this.exportTo('json');}, scope: this},
                                {text: 'A XML', handler: function(){this.exportTo('xml');}, scope: this}]
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
            
            onCreateEntity: function(entityRef, entityName){
                Ext.MessageBox.prompt('Crear '+entityName, 'Por favor, ingrese el nombre:', function(btn, text){
                    if(text!==""){
                        var data= {"name":text,"entityRef":entityRef,"entityName":entityName};
                        if(parentExtController.filter.eq!==undefined && parentExtController.filter.eq.webEntity!==undefined){
                            data["webEntity"]=parentExtController.filter.eq.webEntity;
                        }
                        Instance.entityExtStore.save('create', JSON.stringify(data), function(responseText){
                            Instance.reloadPageStore(Instance.store.currentPage);
                        });
                    }
                });
            },
            
            onCreateFolder: function(){
                Ext.MessageBox.prompt('Crear Carpeta', 'Por favor, ingrese el nombre:', function(btn, text){
                    if(text!==""){
                        var data= {"name":text,"entityRef":"folder"};
                        if(parentExtController.filter.eq!==undefined && parentExtController.filter.eq.webEntity!==undefined){
                            data["webEntity"]=parentExtController.filter.eq.webEntity;
                        }
                        Instance.entityExtStore.save('create', JSON.stringify(data), function(responseText){
                            Instance.reloadPageStore(Instance.store.currentPage);
                        });
                    }
                });
            },
            
            onRenameFile: function(){
                var check_items= document.getElementsByClassName("item_check");
                var checkedValue;
                var totalChecked= 0;
                for(var i=0; i<check_items.length; i++){
                    if(check_items[i].checked){
                        totalChecked++;
                        checkedValue=check_items[i].value;
                    }
                }
                if(totalChecked===1){
                    Ext.MessageBox.prompt('Renombrar', 'Por favor, ingrese el nuevo nombre:', function(btn, text){
                        if(text!==""){
                            var data= {"id":checkedValue,"name":text};
                            Instance.entityExtStore.save('update', JSON.stringify(data), function(responseText){
                                Instance.reloadPageStore(Instance.store.currentPage);
                            });
                        }
                    });
                }else{
                    Ext.MessageBox.show({
                        title: 'Renombrar',
                        msg: 'Para renombrar, debe seleccionar 1 archivo',
                        buttons: Ext.MessageBox.OK,
                        icon: Ext.MessageBox.WARNING
                    });
                }
            },
            
            onMoveFile: function(){
                var check_items= document.getElementsByClassName("item_check");
                Instance.filterMove={"in":{"id":[]},"uv":{}};
                for(var i=0; i<check_items.length; i++){
                    if(check_items[i].checked){
                        Instance.filterMove.in.id.push(check_items[i].value);
                    }
                }
                if(Instance.filterMove.in.id.length>0){
                    if (!Instance.formMove.isVisible()) {
                        Instance.entityExtStore.getNavigationTreeData(function(responseText){
                            var rootMenu= util.objectToJSONMenu(responseText, false);
                            var treePanel = Ext.getCmp('navigation-tree');
                            treePanel.getStore().setRootNode(rootMenu);
                        });
                        Instance.formMove.show(this.down('#fileMenu'), function() {});
                    } else {
                        Instance.formMove.hide(this.down('#fileMenu'), function() {});
                    }
                }else{
                    Ext.MessageBox.show({
                        title: 'Mover',
                        msg: 'Para mover, debe seleccionar al menos 1 archivo',
                        buttons: Ext.MessageBox.OK,
                        icon: Ext.MessageBox.WARNING
                    });
                }
            },
            
            exportTo: function(type){
                this.fireEvent('export', type);
            }
            
        });
    };
    
    function getFormMove(){
        
        var store = {
            //model: 'Item',
            root: {
                text: 'Root',
                expanded: true,
                children: []
            }
        };

        // Go ahead and create the TreePanel now so that we can use it below
         var treePanel = Ext.create('Ext.tree.Panel', {
            id: 'navigation-tree',
            //region:'north',
            split: true,
            width: '100%',
            autoHeight: true,
            minSize: 150,
            rootVisible: false,
            autoScroll: true,
            store: store
        });
        
        treePanel.getSelectionModel().on('select', function(selModel, record) {
            Instance.filterMove.uv.webEntity= record.getId();
        });

        var win = Ext.create('Ext.window.Window', {
            autoShow: false,
            title: 'Mover Archivo',
            closable: true,
            closeAction: 'hide',
            width: 400,
            height: 400,
            minWidth: 300,
            minHeight: 200,
            layout: 'fit',
            plain:true,
            items: treePanel,

            buttons: [{
                text: 'Mover',
                handler: function(){
                    if(Instance.filterMove.uv.webEntity!==undefined){
                        Instance.entityExtStore.updateByFilter(JSON.stringify(Instance.filterMove), function(responseText){
                            Instance.reloadPageStore(Instance.store.currentPage);
                            setTimeout(function(){ Instance.formMove.hide()},1000);
                        });
                    }
                }
            },{
                text: 'Cancelar',
                handler: function(){
                    Instance.formMove.hide();
                }
            }]
        });
        
        return win;
    }
    
    function getPropertyGrid(){
        var renderers= {
            "Ruta": function(entity){
                var breadcrumb= "<a href='#?filter={\"isn\":[\"webEntity\"]}'>Ra&iacute;z</a>";
                var path = entity.split("/");
                for(var i=0; i<path.length; i++){
                    if(path[i].indexOf("__")!==-1){
                        var res= path[i].split("__");
                        breadcrumb+=" / ";
                        if(i<path.length-1){
                            breadcrumb+="<a href='#?filter={\"eq\":{\"webEntity\":"+res[0]+"}}'>";
                        }
                        breadcrumb+=res[1];
                        if(i<path.length-1){
                            breadcrumb+="</a>";
                        }
                    }
                }
                return breadcrumb;
            },
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
    
    function ${labelField}EntityRender(value, p, record){
        if(record){
            if(Instance.typeView==="Parent"){
                return "<a style='font-size: 15px;' href='#?id="+record.data.id+"&tab=1'>"+value+"</a>";
            }else{
                return value;
            }
        }else{
            return value;
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
        Instance.combobox${associatedEntityName}Render= Instance.${associatedER}ExtInterfaces.getComboboxRender('grid');
        </c:forEach>
        
        Instance.formComponent= null;
        <c:if test="${viewConfig.visibleForm}">
        Instance.formContainer = getFormContainer();
        Instance.formComponent= Instance.formContainer.child('#form${entityName}');
        Instance.store.formComponent= Instance.formComponent;
        </c:if>
        
        Instance.gridContainer = getGridContainer();
        Instance.gridComponent = Instance.gridContainer.child('#grid${entityName}');
        Instance.store.gridComponent= Instance.gridComponent;
        
        Instance.propertyGrid= getPropertyGrid();
        
        Instance.formMove= getFormMove();

        Instance.tabsContainer= Ext.widget('tabpanel', {
            region: 'center',
            activeTab: 0,
            style: 'background-color:#dfe8f6; margin:0px',
            defaults: {bodyStyle: 'padding:15px', autoScroll:true},
            items:[
                Instance.gridContainer,
                <c:if test="${viewConfig.visibleForm}">
                {
                    title: 'Detalle Archivo',
                    layout: 'border',
                    bodyStyle: 'padding:0px',
                    items:[
                        {
                            id: 'webEntityDetail',
                            xtype : 'panel',
                            region: 'center',
                            width: '70%',
                            
                            autoScroll: true,
                            html  : Instance.commonExtView.getLoadingContent()
                       },
                       {
                            id: 'content-processes',
                            title: 'Detalles',
                            region: 'east',
                            layout: 'card',
                            margins: '0 0 0 0',
                            split: true,
                            collapsible: true,
                            autoScroll: true,
                            activeItem: 0,
                            border: false,
                            items: [
                                Instance.formContainer
                            ]
                       }
                       
                    ]
                }
                </c:if>
            ]
        });
        
        Instance.tabsContainer.getTabBar().hide();
        Instance.mainView= {
            id: Instance.id,
            title: 'Gestionar ${viewConfig.pluralEntityTitle}',
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