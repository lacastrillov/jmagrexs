<script>

function CommonExtView(parentExtController, parentExtView, model){
    
    var Instance= this;
    
    var util= new Util();
    
    
    Instance.init= function(){
        if(model!==null){
            Instance.modelNameCombobox= "ComboboxModelIn"+model;
            Instance.combobox={};
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
        }
    };
    
    Instance.getSimpleCombobox= function(fieldName, fieldTitle, component, dataArray){
        var data=[];
        data.push({value:"",text:"-"});
        dataArray.forEach(function(item) {
            if((item+"").indexOf(':')!==-1){
                var itemValue= item.split(':');
                data.push({value:itemValue[0],text:itemValue[1]});
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
            //id: component+'Combobox'+fieldName+'In'+model,
            name: fieldName,
            editable: false,
            store: store,
            displayField: 'text',
            valueField: 'value',
            queryMode: 'local',
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
    
    Instance.getSimpleMultiselect= function(fieldName, fieldTitle, dataArray){
        var data=[];
        dataArray.forEach(function(item) {
            if((item+"").indexOf(':')!==-1){
                var itemValue= item.split(':');
                data.push({value:itemValue[0],text:itemValue[1]});
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
            allowBlank: true,
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
    
    Instance.getRadioGroup= function(fieldName, fieldTitle, dataArray){
        var data=[];
        dataArray.forEach(function(item) {
            if((item+"").indexOf(':')!==-1){
                var itemValue= item.split(':');
                data.push({name: fieldName, inputValue:itemValue[0], boxLabel:itemValue[1]});
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
    
    Instance.urlRender= function(value, p, record){
        if(value){
            return "<a target='_blank' href='"+value+"'>"+value+"</a>";
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
    
    Instance.numbererGridRender= function(value, metaData, record, rowIdx, colIdx, dataSource, view){
        metaData.style="text-align:left;color:#666666;";
        return (isNaN(record.index))?"":(record.index+1);
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
                   '<iframe src="<%=request.getContextPath()%>/webFile/ajax/plainTextEditor.htm?fileUrl='+value+'" frameborder="0" width="100%" height="100%"></iframe>';
        }else{
            return value;
        }
    };
    
    Instance.imageRender= function(value, field) {
        Instance.setLinkFieldValue(field, value);
        if(value){
            return '<a href="'+value+'" target="_blank">'+
                   '<img style="max-width:150%" src="'+value+'"></a>';
        }else{
            return "";
        }
    };
    
    Instance.downloadRender= function(value, field) {
        var fileName= value.split('/').pop();
        if(value){
            return '<h2>'+fileName+'</h2>'+
                   '<a href="'+value+'" target="_blank">'+
                   '<img title="Descargar" style="max-width:150%" src="/img/icon_types/download.png" />'+
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
    
    Instance.videoFileUploadRender= function(value, field) {
        Instance.setLinkFieldValue(field, value);
        if(value){
            return '<video style="width:528px;height:297px" controls>'+
                   '    <source src="'+value+'" type="video/'+value.split('.').pop()+'">'+
                   '    Your browser does not support the video tag.'+
                   '</video>';
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
    
    Instance.googleMapsRender= function(value, field) {
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
        setTimeout(function(){
            try{
                var parentForm= field.up('form');
                var linkFieldId= parentForm.itemId + "_" + field.name + "LinkField";
                Ext.getCmp(linkFieldId).setValue((value)?value:"");
            }catch(e){
                console.error(e);
            }
        },1000);
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
                case "html":
                case "vm":
                case "php":
                case "java":
                case "jsp":
                case "js":
                case "txt":
                case "properties":
                case "css":
                case "csv":
                case "xml":
                case "json":
                case "conf":
                    htmlView= Instance.textEditorRender(value, field);
                    break;
                default:
                    htmlView= Instance.downloadRender(value, field);
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