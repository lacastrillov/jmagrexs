<script>

function CommonExtView(parentExtController, parentExtView, model){
    
    var Instance= this;
    
    var util= new Util();
    
    var customColorPicker= new CustomColorPicker();
    
    var MAX_LIST_ITEMS= 20;
    
    Instance.init= function(){
        if(model!==undefined && model!==null){
            Instance.modelNameCombobox= "ComboboxModelIn"+model;
            Instance.combobox={};
            Instance.comboboxRender={};
            Instance.multiselect={};
            Instance.radiogroup={};
            Instance.errorGeneral= "Error de servidor";
            Instance.error403= "Usted no tiene permisos para realizar esta operaci&oacute;n";
            Ext.define(Instance.modelNameCombobox, {
                extend: 'Ext.data.Model',
                fields: [
                    'value',
                    'text'
                ]
            });
            customColorPicker.define();
        }
    };
    
    Instance.getSimpleCombobox= function(fieldName, fieldTitle, component, dataArray, allowBlank){
        var data=[];
        data.push({value:"",text:"-"});
        dataArray.forEach(function(item) {
            if((item+"").indexOf(':')!==-1){
                var itemValue= item.split(':');
                var value= itemValue[0];
                if (!isNaN(value)){
                    value=Number(value);
                }
                data.push({value:value,text:itemValue[1]});
            }else{
                data.push({value:item,text:item});
            }
        });
        var store = Ext.create('Ext.data.Store', {
            autoDestroy: false,
            model: Instance.modelNameCombobox,
            data: data
        });
        Instance.combobox[component+'_'+fieldName]= new Ext.form.ComboBox({
            id: component+'_'+fieldName,
            name: fieldName,
            editable: false,
            allowBlank: allowBlank,
            store: store,
            displayField: 'text',
            valueField: 'value',
            queryMode: 'local',
            dataStore: data,
            listeners: {
                change: function(record){
                    if(component==='filter'){
                        if(record.getValue()!==0){
                            parentExtController.filter.eq[fieldName]= record.getValue();
                        }else{
                            delete parentExtController.filter.eq[fieldName];
                        }
                    }
                }
            }
        });
        if(component!=='grid'){
            Instance.combobox[component+'_'+fieldName].fieldLabel=fieldTitle;
        }
        
        return Instance.combobox[component+'_'+fieldName];
    };
    
    Instance.getSimpleMultiselect= function(fieldName, fieldTitle, dataArray, allowBlank){
        var data=[];
        dataArray.forEach(function(item) {
            if((item+"").indexOf(':')!==-1){
                var itemValue= item.split(':');
                var value= itemValue[0];
                if (!isNaN(value)){
                    value=Number(value);
                }
                data.push({value:value,text:itemValue[1]});
            }else{
                data.push({value:item,text:item});
            }
        });
        var store = Ext.create('Ext.data.Store', {
            autoDestroy: false,
            model: Instance.modelNameCombobox,
            data: data
        });
        Instance.multiselect[fieldName]= {
            id: 'multiselect'+fieldName+'In'+model,
            name: fieldName,
            fieldLabel: fieldTitle,
            xtype: 'multiselect',
            displayField: 'text',
            valueField: 'value',
            allowBlank: allowBlank,
            anchor: '100%',
            maxHeight: 150,
            msgTarget: 'side',
            arrayValues:[],
            lastSelected: null,
            store: store,
            listeners: {
                change: function(record){
                    var value= record.getValue();
                    if(value.length===1){
                        this.lastSelected= value[0];
                    }
                },
                el: {
                    click: function() {
                        var selector=Ext.getCmp('multiselect'+fieldName+'In'+model);
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
                }
            }
        };
        
        return Instance.multiselect[fieldName];
    };
    
    Instance.getSimpleComboboxRender= function(component, fieldName){
        Instance.comboboxRender[component+'_'+fieldName]= function (value, p, record){
            var displayField= Instance.combobox[component+'_'+fieldName].displayField;
            var valueField= Instance.combobox[component+'_'+fieldName].valueField;
            var result="";
            if(value[displayField] !== undefined){
                result+= value[displayField];
            }else{
                if(value[valueField] !== undefined){
                    value= value[valueField];
                }
                result= value;
                Instance.combobox[component+'_'+fieldName].dataStore.forEach(function(item){
                    if(item[valueField]===value){
                        result= item[displayField];
                    }
                });
            }
            return result;
        };
        
        return Instance.comboboxRender[component+'_'+fieldName];
    };
    
    Instance.getRadioGroup= function(fieldName, fieldTitle, dataArray){
        var data=[];
        dataArray.forEach(function(item) {
            if((item+"").indexOf(':')!==-1){
                var itemValue= item.split(':');
                var value= itemValue[0];
                if (!isNaN(value)){
                    value=Number(value);
                }
                data.push({name: fieldName, inputValue:value, boxLabel:itemValue[1]});
            }else{
                data.push({name: fieldName, inputValue:item, boxLabel:item});
            }
        });
        Ext.override(Ext.form.RadioGroup, {
            setValue : function(v){
                if (this.rendered) {
                    var value={};
                    value[this.el.dom.name]=true;
                    Ext.getCmp(this.id).items.items.forEach(function(item){
                        if(item.inputValue===v){
                            item.setValue(true);
                        }
                    });
                }
                return this;
            }
        });
        Instance.radiogroup[fieldName]= new Ext.form.RadioGroup({
            name: fieldName,
            fieldLabel: fieldTitle,
            items: data
        });
        
        return Instance.radiogroup[fieldName];
    };
    
    Instance.enableManagementTabHTMLEditor= function(){
        var htmlEditors = document.getElementsByClassName('x-html-editor-input');
        if(htmlEditors!==null){
            for(var i=0; i<htmlEditors.length; i++){
                var divHtmlEditor= htmlEditors[i];
                var textareaEditors = divHtmlEditor.getElementsByTagName('textarea');
                if(textareaEditors!==null){
                    textareaEditors[0].onkeydown= function(e){
                        if(e.keyCode===9 || e.which===9){
                            e.preventDefault();
                            var s = this.selectionStart;
                            this.value = this.value.substring(0,this.selectionStart) + "\t" + this.value.substring(this.selectionEnd);
                            this.selectionEnd = s+1; 
                        }
                    };
                }
            }
        }
    };
    
    Instance.defineMultiFilefield= function(){
        Ext.define('Ext.ux.form.MultiFile', {
            extend: 'Ext.form.field.File',
            alias: 'widget.multifilefield',

            initComponent: function () {
                var me = this;
                me.on('render', function () {
                    me.fileInputEl.set({ multiple: true });
                });
                me.callParent(arguments);
            },

            onFileChange: function (button, e, value) {
                this.duringFileSelect = true;
                var me = this,
                    upload = me.fileInputEl.dom,
                    files = upload.files,
                    names = [];
                if (files) {
                    for (var i = 0; i < files.length; i++){
                        names.push(files[i].name);
                    }
                    value = names.join(', ');
                }
                Ext.form.field.File.superclass.setValue.call(this, value);
                delete this.duringFileSelect;
            }
        });
    };
    
    Instance.showListItems= function(formComponent){
        formComponent.query('.fieldset').forEach(function(c){
            if(c.itemTop!==undefined){
                var itemsGroup=Ext.getCmp(c.id);
                for(var i=0; i<MAX_LIST_ITEMS; i++){
                    var itemField= Ext.getCmp(c.id+'['+i+']');
                    var rendererField= Ext.getCmp(c.id+'['+i+']Renderer');
                    var linkField= Ext.getCmp(c.id+'['+i+']Link');
                    
                    var filled= false;
                    if(itemField.query){
                        itemField.query('.field').forEach(function(c){
                            var text=c.getValue();
                            if(text!==null && text!=="" && text!==false){
                                filled=true;
                            }
                        });
                    }else{
                        var text= itemField.getValue();
                        if(linkField){
                            text= linkField.getValue();
                        }
                        if(text!==null && text!=="" && text!==false){
                            filled=true;
                        }
                    }
                    if(filled){
                        itemField.setVisible(true);
                        itemField.setDisabled(false);
                        if(itemField.query){
                            itemField.query('.field').forEach(function(c){
                                var visible= true;
                                var upFieldset=c.up('fieldset');
                                while(upFieldset!==undefined && visible===true){
                                    visible=upFieldset.isVisible();
                                    upFieldset= upFieldset.up('fieldset');
                                };
                                c.setDisabled(!c.isVisible() || !visible);
                            });
                        }
                        if(rendererField){
                            rendererField.setVisible(true);
                            rendererField.setDisabled(false);
                        }
                        if(linkField){
                            linkField.setVisible(true);
                            linkField.setDisabled(false);
                        }
                        itemsGroup.itemTop=i+1;
                    }else{
                        itemField.setVisible(false);
                        itemField.setDisabled(true);
                        if(rendererField){
                            rendererField.setVisible(false);
                            rendererField.setDisabled(true);
                        }
                        if(linkField){
                            linkField.setVisible(false);
                            linkField.setDisabled(true);
                        }
                    }
                }
            }
        });
    };
    
    Instance.addListItem= function(processName, parent, fieldName){
        var itemsGroup= Ext.getCmp(processName+"_"+parent+fieldName);
        if(itemsGroup.itemTop<MAX_LIST_ITEMS){
            var itemField= Ext.getCmp(processName+"_"+parent+fieldName+"["+itemsGroup.itemTop+"]");
            var rendererField= Ext.getCmp(processName+"_"+parent+fieldName+"["+itemsGroup.itemTop+"]Renderer");
            var linkField= Ext.getCmp(processName+"_"+parent+fieldName+"["+itemsGroup.itemTop+"]Link");
            
            itemsGroup.itemTop+= 1;
            itemField.setVisible(true);
            itemField.setDisabled(false);
            if(itemField.query){
                itemField.query('.field').forEach(function(c){
                    var visible= true;
                    var upFieldset=c.up('fieldset');
                    while(upFieldset!==undefined && visible===true){
                        visible=upFieldset.isVisible();
                        upFieldset= upFieldset.up('fieldset');
                    };
                    c.setDisabled(!c.isVisible() || !visible);
                });
            }
            if(rendererField){
                rendererField.setVisible(true);
                rendererField.setDisabled(false);
            }
            if(linkField){
                linkField.setVisible(true);
                linkField.setDisabled(false);
            }
        }
    };
    
    Instance.removeListItem= function(processName, parent, fieldName){
        var itemsGroup= Ext.getCmp(processName+"_"+parent+fieldName);
        if(itemsGroup.itemTop>0){
            itemsGroup.itemTop-= 1;
            var itemField= Ext.getCmp(processName+"_"+parent+fieldName+"["+itemsGroup.itemTop+"]");
            var rendererField= Ext.getCmp(processName+"_"+parent+fieldName+"["+itemsGroup.itemTop+"]Renderer");
            var linkField= Ext.getCmp(processName+"_"+parent+fieldName+"["+itemsGroup.itemTop+"]Link");
            
            itemField.setVisible(false);
            itemField.setDisabled(true);
            if(itemField.query){
                itemField.query('.field').forEach(function(c){
                    c.setDisabled(true);
                });
            }
            if(rendererField){
                rendererField.setVisible(false);
                rendererField.setDisabled(true);
            }
            if(linkField){
                linkField.setVisible(false);
                linkField.setDisabled(true);
            }
        }
    };
    
    Instance.urlGridRender= function(value, p, record){
        if(value){
            return "<a target='_blank' href='"+value+"'>"+value+"</a>";
        }else{
            return value;
        }
    };
    
    Instance.passwordGridRender= function(value, p, record){
        if(value){
            return "*****";
        }else{
            return value;
        }
    };
    
    Instance.imageGridRender= function(value, p, record){
        if(value){
            return '<img style="max-height: 200px;" src="'+value+'" />';
        }else{
            return value;
        }
    };
    
    Instance.audioGridRender= function(value, p, record){
        if(value){
            return '<audio style="width:100%" src="'+value+'" preload="auto" controls>'+
                   '    Your browser does not support the video tag.'+
                   '</audio>';
        }else{
            return value;
        }
    };
    
    Instance.durationGridRender= function(value, p, record){
        if(value && !isNaN(value)){
            var seconds= Math.ceil(Number(value)/1000);
            var minutes= Math.floor(seconds/60);
            var hours= Math.floor(minutes/60);
            var ad_minutes= minutes-(hours*60);
            var ad_seconds= seconds-(ad_minutes*60)-(hours*60*60);
            return hours+":"+ad_minutes+":"+ad_seconds;
        }else{
            return value;
        }
    };
    
    Instance.priceGridRender= function(value, p, record){
        if(value && !isNaN(value)){
            return "$ "+Number(value).priceFormat(2);
        }else{
            return value;
        }
    };
    
    Instance.fileSizeGridRender= function(value, p, record){
        if(value && !isNaN(value)){
            return Instance.getFileSizeText(Number(value));
        }else{
            return value;
        }
    };
    
    Instance.numbererGridRender= function(value, metaData, record, rowIdx, colIdx, dataSource, view){
        metaData.style="text-align:left;color:#666666;";
        return (isNaN(record.index))?"":(record.index+1);
    };
    
    Instance.percentageGridRender = function (value, metaData, record) {
        var id = Ext.id();
        Ext.defer(function () {
            if(util.getHtml(id)!==null){
                Ext.widget('progressbar', {
                    renderTo: id,
                    value: value / 100,
                    width: "100%",
                    text: value + " %"
                });
            }
        }, 50);
        return Ext.String.format('<div id="{0}"></div>', id);
    };
    
    Instance.colorGridRender = function (v, metaData, record) {
        if(v){
            return '<div class="x-color-picker-box" style="background-color:'+v+';">'+
                    v+'</div>';
        }else{
            return v;
        }
    };
    
    Instance.conditionalColorGridRender = function (v, metaData, record, rowIndex, colIndex, store) {
        if(v){
            var background= "";
            var color= "";
            if('conditionalColor' in parentExtView.gridComponent.columns[colIndex-1]){
                var conditionalColor= parentExtView.gridComponent.columns[colIndex-1].conditionalColor;
                conditionalColor.forEach(function (item, index) {
                    var match= false;
                    if('eq' in item) match=(v===item.eq);
                    else if('lk' in item) match=(v.indexOf(item.lk)!==-1);
                    else if('lt' in item) match=(v<item.lt);
                    else if('lte' in item) match=(v<=item.lte);
                    else if('gt' in item) match=(v>item.gt);
                    else if('gte' in item) match=(v>=item.gte);
                    if(match){
                        background= ('bg' in item)?'background-color:'+item.bg+';':background;
                        color= ('c' in item)?'color:'+item.c+';':color;
                    }
                });
            }
            return '<div style="'+background+color+'padding:2px;text-align:center;">'+
                    v+'</div>';
        }else{
            return v;
        }
    };
    
    Instance.onOffGridRender = function (v, metaData, record) {
        var check=(v===true)?"checked":"unchecked";
        return '<label class="on_off '+check+'"></label>';
    };
    
    Instance.fileRender= function(value, field){
        Instance.setLinkFieldValue(field, value);
        if(value){
            return "<a target='_blank' href='"+value+"'>"+value+"</a>";
        }else{
            return value;
        }
    };
    
    Instance.pdfRender= function(value, field){
        Instance.setLinkFieldValue(field, value);
        if(value){
            return '<a id="linkFile" href="'+value+'" target="_blank">'+value+'</a>'+
                   '<iframe src="'+value+'" frameborder="0" width="100%" height="100%"></iframe>';
        }else{
            return value;
        }
    };
    
    Instance.textEditorRender= function(value, field){
        if(value){
            return '<a id="linkFile" href="'+value+'" target="_blank">'+value+'</a>'+
                   '<iframe src="${serverDomain.applicationContext}${serverDomain.adminContext}${serverDomain.adminPath}/webFile/ajax/plainTextEditor.htm?fileUrl='+value+'&extractButton=1" frameborder="0" width="100%" height="100%"></iframe>';
        }else{
            return value;
        }
    };
    
    Instance.imageRender= function(value, field) {
        Instance.setLinkFieldValue(field, value);
        if(value){
            return '<a href="'+value+'" target="_blank">'+
                   '<img style="max-width:100%" src="'+value+'"></a>';
        }else{
            return "";
        }
    };
    
    Instance.downloadRender= function(value, field) {
        var fileName= value.split('/').pop();
        if(value){
            return '<h2>'+fileName+'</h2>'+
                   '<a href="'+value+'" target="_blank">'+
                   '<img title="Descargar" style="max-width:150%" src="/libimg/icon_types/download.png" />'+
                   '</a>';
        }else{
            return "";
        }
    };
    
    Instance.videoYoutubeRender= function(value, field) {
        Instance.setLinkFieldValue(field, value);
        var videoId= util.getParameter(value, "v");
        if(videoId!==null){
            return '<iframe width="528" height="287" src="https://www.youtube.com/embed/'+videoId+'" frameborder="0" allowfullscreen></iframe>';
        }else{
            return "";
        }
    };
    
    Instance.onOffRender= function(value, field){
        var checkboxId= field.id.replaceAll("Renderer", "");
        var check= (value===true || value==="true")?"checked":"unchecked";
        Ext.getCmp(checkboxId).setValue(value);
        var forAttr= checkboxId+'-inputEl';
        var onclickAttr= "util.switchClassElement(this,'checked','unchecked')";
        if(Ext.getCmp(checkboxId).readOnly){
            forAttr='';
            onclickAttr='javascript:void(0)';
        }
        return '<label for="'+forAttr+'" class="on_off '+check+'" onclick="'+onclickAttr+'"></label>';
    };
    
    Instance.videoFileUploadRender= function(value, field) {
        Instance.setLinkFieldValue(field, value);
        if(value){
            return '<iframe frameborder="0" width="100%" height="100%" src="'+value+'" allowfullscreen></iframe>';
        }else{
            return "";
        }
    };
    
    Instance.audioFileUploadRender= function(value, field) {
        Instance.setLinkFieldValue(field, value);
        if(value){
            return '<audio style="width:500px" src="'+value+'" preload="auto" controls>'+
                   '    Your browser does not support the video tag.'+
                   '</audio>';
        }else{
            return "";
        }
    };
    
    Instance.fileSizeRender= function(value, field) {
        Instance.setLinkFieldValue(field, value);
        if(value){
            return "<b>"+Instance.getFileSizeText(Number(value))+"</b>";
        }else{
            return "";
        }
    };
    
    Instance.googleMapsRender= function(value, field) {
        Instance.setLinkFieldValue(field, value);
        setTimeout(function(){
            try{
                googleMaps.load(field.name, value);
            }catch(e){
                console.error(e);
            }
        },1000);
        return '<div class="googleMaps">'+
               '    <input id="'+field.name+'Address" type="text" size="50" placeholder="Bogot&aacute; Colombia" />'+
               '    <input type="button" value="Buscar" onclick="googleMaps.showAddress(\''+field.name+'\')" />'+
               '    <div id="'+field.name+'Map" style="width: 100%; height: 400px"></div>'+
               '</div>';
    };
    
    Instance.setLinkFieldValue= function(field, value){
        //setTimeout(function(){
        try{
            var linkFieldId= (field.id+"").replaceAll("Renderer", "Link");
            var fieldId= (field.id+"").replaceAll("Renderer", "");
            if(Ext.getCmp(linkFieldId)!==undefined){
                Ext.getCmp(linkFieldId).setValue((value)?value:"");
            }else if(Ext.getCmp(fieldId)!==undefined){
                Ext.getCmp(fieldId).setValue((value)?value:"");
            }
        }catch(e){
            console.error(e);
        }
        //},1000);
    };
    
    Instance.multiFileRender= function(value, field) {
        if(value){
            var extension= value.split('.').pop().toLowerCase();
            var htmlView= "";
            switch(extension){
                case "mp4":
                case "ogg":
                    htmlView= Instance.videoFileUploadRender(value, field);
                    break;
                case "mp3":
                    htmlView= Instance.audioFileUploadRender(value, field);
                    break;
                case "gif":
                case "jpg":
                case "jpeg":
                case "png":
                    htmlView= Instance.imageRender(value, field);
                    break;
                case "pdf":
                    htmlView= Instance.pdfRender(value, field);
                    break;
                default:
                    if(extension in util.PLAIN_EXTENSIONS){
                        htmlView= Instance.textEditorRender(value, field);
                    }else{
                        htmlView= Instance.downloadRender(value, field);
                    }
                    break;
            }
            try{
                setTimeout(function(){
                    htmlView= "<style>#linkFile{display:none}</style>"+
                              "<div style='margin:0px;text-align: center; height: 99%;'>"+
                                htmlView+
                              "</div>";
                    util.setHtml("webFileDetail-innerCt", htmlView);
                },10);
            }catch(e){
                console.log(e);
            }
            return Instance.fileRender(value, field);
        }else{
            return "";
        }
    };
    
    Instance.getFileSizeText= function(bytes){
        if(bytes<1024){
           return bytes.toFixed(2) + " bytes";
        }
        var kb= bytes/1024;
        if(kb<1024){
           return kb.toFixed(2) + " KB";
        }
        var mb= kb/1024;
        if(mb<1024){
            return mb.toFixed(2) + " MB";
        }
        var gb= mb/1024;
        if(gb<1024){
            return gb.toFixed(2) + " GB";
        }
        var tb= gb/1024;
        if(tb<1024){
            return tb.toFixed(2) + " TB";
        }
        var pb= tb/1024;
        return pb.toFixed(2) + " PB";
    };
    
    Instance.getLoadingContent= function(){
        var loadingDiv=
        '<div class="x-mask-msg x-layer x-mask-msg-default x-border-box" style="right: auto; z-index: 19001; top: 35%; left: 44%;">'+
        '    <div class="x-mask-msg-inner">'+
        '      <div class="x-mask-msg-text">Loading...</div>'+
        '    </div>'+
        '</div>';

        return loadingDiv;
    };
    
    Instance.processFailure= function(response){
        if(response.status===403){
            Ext.MessageBox.show({
                msg: 'Cargando...',
                width:200,
                wait:true,
                waitConfig: {interval:200}
            });
            userAuthentication.replicateAuthentication(function(replicationResult){
                if(replicationResult.replicated || replicationResult.userData===null){
                    location.reload();
                }else{
                    Instance.showErrorMessage(Instance.error403);
                }
            });
        }else{
            Instance.showErrorMessage(Instance.errorGeneral);
        }
    };
    
    Instance.showErrorMessage= function(errorMsg){
        Ext.MessageBox.show({
            title: 'ERROR REMOTO',
            msg: errorMsg,
            icon: Ext.MessageBox.ERROR,
            buttons: Ext.Msg.OK
        });
    };
    
    
    Instance.init();
}
</script>