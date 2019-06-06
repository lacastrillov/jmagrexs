<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>${fileName} - Text Editor</title>
        <style>
            body{margin:0px; padding-top: 2px; background: #ebebeb;}
            #message {color:#15428B; font-size: 13px; margin-left: 527px; margin-top: -17px;}
            #editor {position: absolute; top: 34px; right: 0; bottom: 0; left: 0; font-size: 14px;}
        </style>
        <script src="<%=request.getContextPath()%>/libjs/jquery/jquery-3.1.0.min.js"></script>
        <script src="<%=request.getContextPath()%>/libjs/util/Util.js"></script>
        <script src="${serverDomain.domain}:8080/ace-builds/src-noconflict/ace.js" type="text/javascript" charset="utf-8"></script>
        <script>
            function PlainTextExtEditor() {
                
                var Instance = this;
                
                var util= new Util();
                
                Instance.init = function () {
                    $(document).ready(function () {
                        Instance.fileUrl= "${fileUrl}";
                        var ext = ((Instance.fileUrl!==null)?Instance.fileUrl.substr(Instance.fileUrl.lastIndexOf('.') + 1).toLowerCase():"");
                        
                        Instance.editor = ace.edit("editor");
                        Instance.editor.setTheme("ace/theme/eclipse");
                        Instance.editor.session.setMode("ace/mode/text");
                        if(ext in util.PLAIN_EXTENSIONS){
                            Instance.editor.session.setMode(util.PLAIN_EXTENSIONS[ext]);
                            console.log(util.PLAIN_EXTENSIONS[ext]);
                        }
                        
                        Instance.loadContentFile();
                        
                        $("#saveButton").on("click", function(){
                            Instance.saveContentFile();
                        });
                        $("#reloadButton").on("click", function(){
                            Instance.loadContentFile();
                        });
                        $("#selectAllButton").on("click", function(){
                            Instance.editor.selectAll();
                        });
                        $("#downloadButton").on("click", function(){
                            util.downloadURI(Instance.fileUrl, "Descargar archivo");
                        });
                        $("#extractButton").on("click", function(){
                            util.openInNewTab(document.URL);
                        });
                        
                        $(window).keypress(function(event) {
                            if (!(event.which === 115 && event.ctrlKey) && !(event.which === 19)) return true;
                            Instance.saveContentFile();
                            event.preventDefault();
                            return false;
                        });
                    });
                };
                
                this.loadContentFile= function(){
                    Instance.setMessage("Cargado...");
                    $("#fileLink").attr("href",Instance.fileUrl);
                    $("#fileLink").html(decodeURIComponent(Instance.fileUrl));
                    $.ajax({
                        url: "${serverDomain.applicationContext}${serverDomain.restContext}/rest/webFile/readFile.htm",
                        timeout: 20000,
                        type: "POST",
                        data: "fileUrl="+Instance.fileUrl,
                        cache: false,
                        dataType: "html",
                        error: function (xhr, status) {
                            console.log(xhr.status);
                        },
                        success: function (data, status) {
                            Instance.editor.setValue(data);
                            Instance.editor.clearSelection();
                            Instance.setMessage("Contenido cargado");
                        }
                    });
                };
                
                this.saveContentFile= function(){
                    Instance.setMessage("Guardando...");
                    var content= Instance.editor.getValue();
                    $.ajax({
                        url: "${serverDomain.applicationContext}${serverDomain.restContext}/rest/webFile/writeFile.htm",
                        timeout: 20000,
                        type: "POST",
                        data: "fileUrl="+Instance.fileUrl+"&content="+encodeURIComponent(content),
                        cache: false,
                        dataType: "html",
                        error: function (xhr, status) {
                            console.log(xhr.status);
                        },
                        success: function (data, status) {
                            Instance.setMessage(data);
                        }
                    });
                };
                
                this.setMessage= function(message){
                    $("#message").text(message);
                    $("#message").show();
                    setTimeout(function(){
                        $("#message").hide();
                    },2000);
                };
                
                Instance.init();
            }
            var plainTextExtEditor= new PlainTextExtEditor();
        </script>
    </head>
    <body>
        <input id="saveButton" type="button" value="Guardar" />
        <input id="reloadButton" type="button" value="Recargar" />
        <input id="selectAllButton" type="button" value="Seleccionar Todo" />
        <input id="extractButton" type="button" value="Extraer" />
        <input id="downloadButton" type="button" value="Descargar" />
        <div id="message"></div>
        <div id="editor"></div>
    </body>
</html>
