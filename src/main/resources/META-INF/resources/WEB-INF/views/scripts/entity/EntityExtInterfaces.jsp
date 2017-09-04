<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<script>

function ${entityName}ExtInterfaces(parentExtController, parentExtView){
    
    var Instance= this;
    
    Instance.modelName="${entityName}Model";
    
    // MODELS **********************************************
    
    Instance.entityExtModel= new ${entityName}ExtModel();
    
    // STORES **********************************************
    
    Instance.entityExtStore= new ${entityName}ExtStore();
    
    //*******************************************************
    
    var util= new Util();
    
    
    Instance.init= function(){
        Instance.pluralEntityTitle= '${viewConfig.pluralEntityTitle}';
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
            source['${entityName}']= entity.id+"__"+entity.${labelField};
        }else{
            delete source['${entityName}'];
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
                    /*console.log("CHANGE>>"+JSON.stringify(record.getValue()));
                    console.log("component>>"+component);
                    if(typeof record.getValue() === "object"){
                       console.log("VALID>>"+JSON.stringify(record.getValue()));
                       Instance.combobox["grid"].realGridValue=record.getValue();
                    }else if(util.isNumeric(record.getValue())){
                        console.log(record.getValue());
                       Instance.combobox["grid"].realGridValue=record.getValue();
                    }else{
                        console.log("ERROR");
                    }*/
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
                        /*if(component==="grid"){
                            console.log("RG "+JSON.stringify(this.combobox[component].realGridValue));
                            if(this.combobox[component].realGridValue!==null){
                                console.log("IN SET");
                                this.combobox[component].setValue(this.combobox[component].realGridValue);
                            }
                            console.log("CLICK>> "+JSON.stringify(this.combobox[component].getValue()));
                        }
                        console.log("end");*/
                    },
                    scope: this
                }
            }/*,
            getDisplayValue: function() {
                var me = this,
                value = me.value,
                record = null;
                if(value) {
                    record = me.getStore().findRecord(me.valueField, value);
                }
                if(record) {
                    console.log(record.get(me.displayField));
                    return util.htmlEntitiesDecode(record.get(me.displayField));
                }
                return value;
            }*/
        });
        
        if(component!=='grid'){
            Instance.combobox[component].fieldLabel= fieldTitle;
        }
        
        if(component==='filter'){
            Instance.store.addListener('load', function(){
                var rec = { id: 0, ${labelField}: '-' };
                Instance.store.insert(0,rec);
            });
        }
        
        return Instance.combobox[component];
    };
    
    Instance.getComboboxRender= function(component){
        Instance.comboboxRender[component]= function (value, p, record){
            var displayField= Instance.combobox[component].displayField;
            var valueField= Instance.combobox[component].valueField;

            if (typeof value === "object" && Object.getOwnPropertyNames(value).length === 0){
                return "";
            }else if(typeof(value[displayField]) !== 'undefined'){
                return value[displayField];
            }else{
                if(typeof(value[valueField]) !== 'undefined'){
                    value= value[valueField];
                }
                var record = Instance.combobox[component].findRecord(valueField, value);
                if(record){
                    return record.get(Instance.combobox[component].displayField);
                }else{
                    return value;
                }
            }
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