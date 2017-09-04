<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Text Editor</title>
        <style>
            body{margin:0px;}
            #contentEditor {width: 99%; height: 450px; white-space: pre !important;}
            #contentEditor:focus { background: transparent; }
            #message {color:#15428B; font-weight: bold; font-size: 12px; margin-left: 10px; margin-top: 10px;}
        </style>
        <script type="text/javascript"  src="/js/libs/jquery/jquery-3.1.0.min.js"></script>
        <script src="/js/util/Util.js"></script>
        <script>
            function PlainTextExtEditor() {
                
                var Instance = this;
                
                var util= new Util();

                Instance.init = function () {
                    $(document).ready(function () {
                        Instance.fileUrl= util.getParameter(document.URL, "fileUrl");
                        Instance.loadContentFile();
                        Instance.configureTabKeyDown();
                        
                        $("#saveButton").on("click", function(){
                            Instance.saveContentFile();
                        });
                        
                        $("#reloadButton").on("click", function(){
                            Instance.loadContentFile();
                        });
                        
                        $("#selectAllButton").on("click", function(){
                            Instance.selectAll();
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
                    $("#message").text("Cargado...");
                    $("#fileLink").attr("href",Instance.fileUrl);
                    $("#fileLink").html(decodeURIComponent(Instance.fileUrl));
                    $.ajax({
                        url: "/rest/webFile/getContentFile.htm",
                        timeout: 20000,
                        type: "POST",
                        data: "fileUrl="+Instance.fileUrl,
                        cache: false,
                        dataType: "html",
                        error: function (xhr, status) {
                            console.log(xhr.status);
                        },
                        success: function (data, status) {
                            $("#contentEditor").val(data);
                            console.log($("#contentEditor").text());
                            $("#message").text("Contenido cargado");
                        }
                    });
                };
                
                this.saveContentFile= function(){
                    $("#message").text("Guardando...");
                    var content= $("#contentEditor").val();
                    $.ajax({
                        url: "/rest/webFile/setContentFile.htm",
                        timeout: 20000,
                        type: "POST",
                        data: "fileUrl="+Instance.fileUrl+"&content="+encodeURIComponent(content),
                        cache: false,
                        dataType: "html",
                        error: function (xhr, status) {
                            console.log(xhr.status);
                        },
                        success: function (data, status) {
                            $("#message").text(data);
                        }
                    });
                };
                
                this.selectAll= function() {
                    var tempval=document.getElementById("contentEditor");
                    tempval.focus();
                    tempval.select();
                };
                
                this.configureTabKeyDown= function(){
                    var textareaEditor= document.getElementById("contentEditor");
                    textareaEditor.onkeydown= function(e){
                        if(e.keyCode===9 || e.which===9){
                            e.preventDefault();
                            var s = this.selectionStart;
                            this.value = this.value.substring(0,this.selectionStart) + "\t" + this.value.substring(this.selectionEnd);
                            this.selectionEnd = s+1; 
                        }
                    };
                };

                Instance.init();
            }
            var plainTextExtEditor= new PlainTextExtEditor();
        </script>
    </head>
    <body>
        <textarea id="contentEditor"></textarea>
        <br>
        <input id="saveButton" type="button" value="Guardar" />
        <input id="reloadButton" type="button" value="Recargar" />
        <input id="selectAllButton" type="button" value="Seleccionar Todo" />
        <div id="message"></div>
    </body>
</html>
