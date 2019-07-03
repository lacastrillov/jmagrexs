<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<script>

function ${entityName}ExtInterfaces(parentExtController, parentExtView){
    
    var Instance= this;
    
    Instance.modelName="${entityName}ModelInt";
    
    // MODELS **********************************************
    
    Instance.entityExtModel= new ${entityName}ExtModel();
    
    // STORES **********************************************
    
    Instance.entityExtStore= new ${entityName}ExtStore();
    
    //*******************************************************
    
    
    Instance.init= function(){
        Instance.pluralEntityTitle= '${viewConfig.pluralEntityTitle}';
        Instance.singularEntityTitle= '${viewConfig.singularEntityTitle}';
        Instance.entityExtModel.defineModel(Instance.modelName);
        Instance.store= Instance.entityExtStore.getStore(Instance.modelName);
        Instance.combobox={};
        Instance.comboboxRender={};
    };
    
    Instance.setFilterStore= function(filter){
        Instance.store.getProxy().extraParams.filter= filter;
    };
    
    Instance.reloadPageStore= function(page){
        Instance.store.loadPage(page);
    };
    
    Instance.addLevel= function(entity){
        var source= parentExtView.propertyGrid.getSource();
        
        if(entity!==null && typeof(entity)!=='undefined'){
            source[Instance.singularEntityTitle]= entity.id+"__"+entity.${labelField};
        }else{
            delete source[Instance.singularEntityTitle];
        }
            
        parentExtView.propertyGrid.setSource(source);
    };
    
    Instance.getCombobox= function(component, entityDestination, fieldName, fieldTitle){
        Instance.store.pageSize= 1000;
        Instance.store.sorters.items[0].property='${labelField}';
        Instance.store.sorters.items[0].direction='ASC';
        Instance.combobox[component]= new Ext.form.ComboBox({
            id: component+'Combobox'+fieldName+'In'+entityDestination,
            name: fieldName,
            allowBlank: true,
            editable: true,
            store: Instance.store,
            displayField: '${labelField}',
            valueField: 'id',
            queryMode: 'remote',
            optionAll: false,
            comboboxDependent: [],
            reloadData: false,
            realGridValue: null,
            listeners: {
                change: function(record){
                    if(component==='filter'){
                        if(record.getValue()!==0){
                            parentExtController.filter.eq[fieldName]= record.getValue();
                        }else{
                            delete parentExtController.filter.eq[fieldName];
                        }
                    }
                    this.comboboxDependent.forEach(function(combobox) {
                        var filter= {"eq":{"${entityRef}":record.getValue()}};
                        combobox.store.getProxy().extraParams.filter= JSON.stringify(filter);
                        combobox.reloadData= true;
                    });
                },
                el: {
                    click: function() {
                        if(this.combobox[component].reloadData){
                            this.combobox[component].store.loadPage(1);
                            this.combobox[component].reloadData= false;
                        }
                    },
                    scope: this
                },
                afterrender: function( ){
                    Instance.store.addListener('load', function(){
                        if(component==='filter'){
                            var rec = { id: 0, ${labelField}: '-' };
                            Instance.store.insert(0,rec);
                        }
                        <c:if test="${viewConfig.labelPlusId}">
                        if(Instance.combobox[component].displayField!==Instance.combobox[component].valueField){
                            Instance.store.each(function(record,id){
                                if(record.data['id']!==0 && record.data['${labelField}'].indexOf(record.data['id']+" - ")===-1){
                                    record.data['${labelField}']= record.data['id']+ " - " + record.data['${labelField}'];
                                }
                            },this);
                            Instance.combobox[component].bindStore(Instance.store);
                        }
                        </c:if>
                    });
                }
            },
            getDisplayValue: function() {
                var me = this;
                var displayValue="";
                if(me.value!==null){
                    if(typeof me.value === "object"){
                        displayValue= me.value[me.displayField];
                        if(displayValue!==undefined){
                            if(${viewConfig.labelPlusId} && me.displayField!==me.valueField){
                                displayValue= me.value[me.valueField] + " - " + displayValue;
                            }
                            me.setValue(me.value[me.valueField]);
                        }
                    }else{
                        var record = null;
                        if(me.value) {
                            record = me.getStore().findRecord(me.valueField, me.value);
                        }
                        if(record) {
                            if(${viewConfig.labelPlusId} && me.displayField!==me.valueField && record.get(me.displayField).indexOf(me.value +" - ")===-1){
                                displayValue= me.value + " - ";
                            }
                            displayValue+= record.get(me.displayField);
                        }else{
                            displayValue= me.value;
                        }
                    }
                }
                return displayValue;                
            }
        });
        
        if(component!=='grid'){
            Instance.combobox[component].fieldLabel= fieldTitle;
        }
        
        return Instance.combobox[component];
    };
    
    Instance.getMultiselect= function(entityDestination, fieldName, fieldTitle){
        Instance.store.pageSize= 1000;
        Instance.store.sorters.items[0].property='${labelField}';
        Instance.store.sorters.items[0].direction='ASC';
        Instance.multiselect= {
            id: 'multiselect'+fieldName+'In'+entityDestination,
            name: fieldName,
            fieldLabel: fieldTitle,
            xtype: 'multiselect',
            displayField: '${labelField}',
            valueField: 'id',
            allowBlank: true,
            anchor: '100%',
            maxHeight: 150,
            msgTarget: 'side',
            arrayValues:[],
            lastSelected: null,
            store: Instance.store,
            listeners: {
                change: function(record){
                    var value= record.getValue();
                    if(value.length===1){
                        this.lastSelected= value[0];
                    }
                },
                el: {
                    click: function() {
                        var selector=Ext.getCmp('multiselect'+fieldName+'In'+entityDestination);
                        var index= selector.arrayValues.indexOf(selector.lastSelected);
                        if(selector.lastSelected!==null && index===-1){
                            selector.arrayValues.push(selector.lastSelected);
                        }else{
                            selector.arrayValues.splice(index, 1);
                        }
                        selector.setValue(selector.arrayValues);
                        if(selector.arrayValues.length>0){
                            parentExtController.filter.in[fieldName]= selector.arrayValues;
                        }else{
                            delete parentExtController.filter.in[fieldName];
                        }
                    },
                    scope: this
                },
                afterrender: function( ){
                    Instance.multiselect.store.loadPage(1);
                }
            }
        };
        
        return Instance.multiselect;
    };
    
    Instance.getComboboxRender= function(component){
        Instance.comboboxRender[component]= function (value, p, record){
            var displayField= Instance.combobox[component].displayField;
            var valueField= Instance.combobox[component].valueField;
            var result="";

            if (typeof value === "object" && Object.getOwnPropertyNames(value).length === 0){
                result= "";
            }else if(value[displayField] !== undefined){
                <c:if test="${viewConfig.labelPlusId}">
                if(displayField!==valueField){
                    result= value[valueField] + " - ";
                }
                </c:if>
                result+= value[displayField];
            }else{
                if(value[valueField] !== undefined){
                    value= value[valueField];
                }
                var record = Instance.combobox[component].findRecord(valueField, value);
                if(record){
                    result= record.get(Instance.combobox[component].displayField);
                }else{
                    result= value;
                }
            }
            return result;
        };
        
        return Instance.comboboxRender[component];
    };
    
    Instance.getCheckboxGroup= function(entityDestination, fieldName, callback){
        
        Instance.checkboxGroup=  new Ext.form.CheckboxGroup({
            id: 'checkboxGroup'+fieldName+'In'+entityDestination,
            fieldLabel: 'Listado '+Instance.pluralEntityTitle,
            allowBlank: true,
            columns: 3,
            vertical: true,
            items: []
        });
        
        Instance.entityExtStore.find("", "", function(responseText){
            if(responseText.success){
                responseText.data.forEach(function(item){
                    var cb = Ext.create('Ext.form.field.Checkbox', {
                        id: 'checkNN'+fieldName+item.id,
                        boxLabel: item.${labelField},
                        name: fieldName,
                        inputValue: item.id,
                        checked: false,
                        activeChange: true,
                        listeners: {
                            change: callback
                        }
                    });
                    Instance.checkboxGroup.add(cb);
                });
            }
        });
        
        return Instance.checkboxGroup;
    };

    Instance.init();
}
</script>