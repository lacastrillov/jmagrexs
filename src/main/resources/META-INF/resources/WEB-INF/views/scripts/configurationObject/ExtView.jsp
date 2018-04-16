<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<script>

function ${entityName}ExtView(parentExtController, parentExtView){
    
    var Instance= this;
    
    Instance.id= "/${entityRef}";
    
    var util= new Util();
    
    // MODELS **********************************************
    
    Instance.entityExtModel= new ${entityName}ExtModel();
    
    // STORES **********************************************
    
    Instance.entityExtStore= new ${entityName}ExtStore();
    
    // COMPONENTS *******************************************
    
    Instance.commonExtView= new CommonExtView(parentExtController, Instance, '${entityName}');
    
    //*******************************************************
    
    
    Instance.init= function(){
        <c:forEach var="configurationObjectName" items="${nameConfigurationObjects}">
        Instance.entityExtModel.define${configurationObjectName.key}Model("${configurationObjectName.key}Model");
        </c:forEach>
        
        Instance.createMainView();
    };
    
    <c:if test="${viewConfig.visibleForm}">
        
    function getTreeMenuConfigurationObjects(){
        var store1 = {
            //model: 'Item',
            root: {
                text: 'Root 1',
                expanded: true,
                children: [
                    <c:forEach var="configurationObjectName" items="${nameConfigurationObjects}">
                    {
                        id: 'formContainer${configurationObjectName.key}',
                        text: '${configurationObjectName.value}',
                        leaf: true
                    },
                    </c:forEach>
                ]
            }
        };
        
        var treePanelConfigurationObjects = Ext.create('Ext.tree.Panel', {
            id: 'tree-panel-process',
            title: 'Configuraci&oacute;n',
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
        
        treePanelConfigurationObjects.getSelectionModel().on('select', function(selModel, record) {
            if (record.get('leaf')) {
                Ext.getCmp('content-configurationObjects').layout.setActiveItem(record.getId());
            }
        });
        
        return treePanelConfigurationObjects;
    }
    
    <c:forEach var="configurationObjectName" items="${nameConfigurationObjects}">
    function getFormContainer${configurationObjectName.key}(){
        var formFields= ${jsonFormFieldsMap[configurationObjectName.key]};

        Instance.defineWriterForm("${configurationObjectName.key}", formFields);
        
        return Ext.create('Ext.container.Container', {
            id: 'formContainer${configurationObjectName.key}',
            type: 'fit',
            align: 'stretch',
            items: [{
                itemId: 'form${configurationObjectName.key}',
                xtype: 'writerform${configurationObjectName.key}',
                title: '${configurationObjectName.value}',
                style: 'min-width: 450px',
                border: false,
                width: '60%',
                minWidth: 300,
                listeners: {
                    saveConfig: function(form, data){
                        Instance.entityExtStore.saveConfig('${configurationObjectName.key}', data, function(configurationObjectRef, result){
                            <c:if test="${fn:contains(viewConfig.multipartFormConfig, configurationObjectName.key)}">
                            var formComponent= Ext.getCmp('formContainer'+configurationObjectRef).child('#form'+configurationObjectRef);
                            Instance.entityExtStore.upload(formComponent, configurationObjectRef, function(responseUpload){
                                Ext.MessageBox.alert('Status', responseUpload.message);
                                if(responseUpload.success){
                                    parentExtController.populateForm(configurationObjectRef, JSON.parse(responseUpload.data));
                                    parentExtController.formSavedResponse(result);
                                }
                            });
                            </c:if>
                            <c:if test="${!fn:contains(viewConfig.multipartFormConfig, configurationObjectName.key)}">
                            parentExtController.formSavedResponse(result);
                            </c:if>
                        });
                    },
                    cancelConfig: function(form){
                        parentExtController.loadFormData('${configurationObjectName.key}');
                    },
                    render: function(panel) {
                        Instance.commonExtView.enableManagementTabHTMLEditor();
                    }
                }
            }],
            listeners:{
                activate: function(panel) {
                    parentExtController.loadFormData('${configurationObjectName.key}');
                }
            }
        });
    };
    </c:forEach>
    
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
                    text: 'Guardar',
                    scope: this,
                    handler: this.onSave
                }, {
                    //iconCls: 'icon-reset',
                    text: 'Cancelar',
                    scope: this,
                    handler: this.onCancel
                },
                <c:if test="${viewConfig.visibleSeeAllButton}">
                {
                    text: '&#x25BC; Ver todo',
                    scope: this,
                    handler: this.onSeeAll
                },
                </c:if>
                '|'];
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
                    this.fireEvent('saveConfig', this, form.getValues());
                }
            },
            
            onCancel: function(){
                this.fireEvent('cancelConfig', this);
            },
                    
            onSeeAll: function(){
                this.doLayout();
            }
    
        });
        
    };
    
    </c:if>
    
    Instance.createMainView= function(){
        <c:if test="${viewConfig.visibleForm}">
        Instance.menuConfigurationObjects= getTreeMenuConfigurationObjects();
        </c:if>

        Instance.tabsContainer= Ext.widget('tabpanel', {
            region: 'center',
            activeTab: 0,
            style: 'background-color:#dfe8f6; margin:0px',
            defaults: {bodyStyle: 'padding:15px', autoScroll:true},
            items:[
                <c:if test="${viewConfig.visibleForm}">
                {
                    title: 'Gestionar Objetos de Configuraci&oacute;n',
                    layout: 'border',
                    bodyStyle: 'padding:0px',
                    items:[
                        Instance.menuConfigurationObjects,
                        {
                            id: 'content-configurationObjects',
                            region: 'center',
                            layout: 'card',
                            margins: '0 0 0 0',
                            autoScroll: true,
                            activeItem: 0,
                            border: false,
                            items: [
                                <c:forEach var="configurationObjectName" items="${nameConfigurationObjects}">
                                getFormContainer${configurationObjectName.key}(),
                                </c:forEach>
                            ]
                       }
                    ]
                },
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
        Instance.tabsContainer.getTabBar().hide();
        
        Instance.mainView= {
            id: Instance.id,
            title: '${viewConfig.mainConfigurationTitle}',
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