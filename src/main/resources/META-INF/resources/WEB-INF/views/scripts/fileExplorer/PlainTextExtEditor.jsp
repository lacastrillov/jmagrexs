<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Text Editor</title>
        <style>
            body{margin:0px; padding-top: 2px; background: #ebebeb;}
            #message {color:#15428B; font-size: 13px; margin-left: 414px; margin-top: -17px;}
            #editor {position: absolute; top: 26px; right: 0; bottom: 0; left: 0; font-size: 14px;}
        </style>
        <script src="<%=request.getContextPath()%>/libjs/jquery/jquery-3.1.0.min.js"></script>
        <script src="<%=request.getContextPath()%>/libjs/util/Util.js"></script>
        <script src="${serverDomain.domain}:8080/ace-builds/src-noconflict/ace.js" type="text/javascript" charset="utf-8"></script>
        <script>
            function PlainTextExtEditor() {
                
                var Instance = this;
                
                var util= new Util();
                
                Instance.extensions={
                    "abap":"ace/mode/abap",                 "abc":"ace/mode/abc",
                    "as":"ace/mode/actionscript",           "ada":"ace/mode/ada",
                    "conf":"ace/mode/apache_conf",          "adoc":"ace/mode/asciidoc",
                    "asl":"ace/mode/asl",                   "asm":"ace/mode/assembly_x86",
                    "ahk":"ace/mode/autohotkey",            "apx":"ace/mode/apex",
                    "bat":"ace/mode/batchfile",             "bro":"ace/mode/bro",
                    "c":"ace/mode/c_cpp",                   "cpp":"ace/mode/c_cpp",
                    "h":"ace/mode/c_cpp",                   "c9s":"ace/mode/c9search",
                    "cr":"ace/mode/cirru",                  "clj":"ace/mode/clojure",
                    "cob":"ace/mode/cobol",                 "coffee":"ace/mode/coffee",
                    "cfm":"ace/mode/coldfusion",            "cs":"ace/mode/csharp",
                    "csd":"ace/mode/csound_document",       "csds":"ace/mode/csound_score",
                    "css":"ace/mode/css",                   "curly":"ace/mode/curly",
                    "d":"ace/mode/d",                       "dart":"ace/mode/dart",
                    "diff":"ace/mode/diff",                 "dockerfile":"ace/mode/dockerfile",
                    "dot":"ace/mode/dot",                   "drl":"ace/mode/drools",
                    "edifact":"ace/mode/edifact",           "eff":"ace/mode/eiffel",
                    "ejs":"ace/mode/ejs",                   "exs":"ace/mode/elixir",
                    "elm":"ace/mode/elm",                   "erl":"ace/mode/erlang",
                    "forth":"ace/mode/forth",               "f":"ace/mode/fortran",
                    "fs":"ace/mode/fsharp",                 "fsl":"ace/mode/fsl",
                    "ftl":"ace/mode/ftl",                   "gcode":"ace/mode/gcode",
                    "ghe":"ace/mode/gherkin",               "gitignore":"ace/mode/gitignore",
                    "glsl":"ace/mode/glsl",                 "gob":"ace/mode/gobstones",
                    "go":"ace/mode/golang",                 "graphql":"ace/mode/graphqlschema",
                    "groovy":"ace/mode/groovy",             "haml":"ace/mode/haml",
                    "handlebars":"ace/mode/handlebars",     "hs":"ace/mode/haskell",
                    "cabal":"ace/mode/haskell_cabal",       "haxe":"ace/mode/haxe",
                    "hjson":"ace/mode/hjson",               "html":"ace/mode/html",
                    "html_elixir":"ace/mode/html_elixir",   "html_ruby":"ace/mode/html_ruby",
                    "ini":"ace/mode/ini",                   "io":"ace/mode/io",
                    "jack":"ace/mode/jack",                 "jade":"ace/mode/jade",
                    "java":"ace/mode/java",                 "js":"ace/mode/javascript",
                    "json":"ace/mode/json",                 "jsoniq":"ace/mode/jsoniq",
                    "jsp":"ace/mode/jsp",                   "jssm":"ace/mode/jssm",
                    "jsx":"ace/mode/jsx",                   "jl":"ace/mode/julia",
                    "kt":"ace/mode/kotlin",                 "latex":"ace/mode/latex",
                    "less":"ace/mode/less",                 "liquid":"ace/mode/liquid",
                    "lisp":"ace/mode/lisp",                 "lsc":"ace/mode/livescript",
                    "logiql":"ace/mode/logiql",             "lsl":"ace/mode/lsl",
                    "lua":"ace/mode/lua",                   "lp":"ace/mode/luapage",
                    "cfs":"ace/mode/lucene",                "makefile":"ace/mode/makefile",
                    "md":"ace/mode/markdown",               "mask":"ace/mode/mask",
                    "m":"ace/mode/matlab",                  "maz":"ace/mode/maze",
                    "mel":"ace/mode/mel",                   "mix":"ace/mode/mixal",
                    "mush":"ace/mode/mushcode",             "mysql":"ace/mode/mysql",
                    "nix":"ace/mode/nix",                   "nsis":"ace/mode/nsis",
                    "mm":"ace/mode/objectivec",             "mli":"ace/mode/ocaml",
                    "pas":"ace/mode/pascal",                "pl":"ace/mode/perl",
                    "pl6":"ace/mode/perl6",                 "pgsql":"ace/mode/pgsql",
                    "phplb":"ace/mode/php_laravel_blade",   "php":"ace/mode/php",
                    "pp":"ace/mode/puppet",                 "pig":"ace/mode/pig",
                    "ps1":"ace/mode/powershell",            "praat":"ace/mode/praat",
                    "prolog":"ace/mode/prolog",             "properties":"ace/mode/properties",
                    "proto":"ace/mode/protobuf",            "py":"ace/mode/python",
                    "r":"ace/mode/r",                       "rzr":"ace/mode/razor",
                    "rdoc":"ace/mode/rdoc",                 "red":"ace/mode/red",
                    "rhtml":"ace/mode/rhtml",               "rst":"ace/mode/rst",
                    "rb":"ace/mode/ruby",                   "rs":"ace/mode/rust",
                    "sass":"ace/mode/sass",                 "scad":"ace/mode/scad",
                    "scala":"ace/mode/scala",               "scm":"ace/mode/scheme",
                    "scss":"ace/mode/scss",                 "sh":"ace/mode/sh",
                    "sjs":"ace/mode/sjs",                   "slim":"ace/mode/slim",
                    "smarty":"ace/mode/smarty",             "snippet":"ace/mode/snippets",
                    "soy":"ace/mode/soy_template",          "spc":"ace/mode/space",
                    "sql":"ace/mode/sql",                   "sqls":"ace/mode/sqlserver",
                    "styl":"ace/mode/stylus",               "svg":"ace/mode/svg",
                    "swift":"ace/mode/swift",               "tcl":"ace/mode/tcl",
                    "tf":"ace/mode/terraform",              "tex":"ace/mode/tex",
                    "txt":"ace/mode/text",                  "textile":"ace/mode/textile",
                    "toml":"ace/mode/toml",                 "tsx":"ace/mode/tsx",
                    "twig":"ace/mode/twig",                 "ts":"ace/mode/typescript",
                    "vala":"ace/mode/vala",                 "vbs":"ace/mode/vbscript",
                    "vm":"ace/mode/velocity",               "v":"ace/mode/verilog",
                    "vhdl":"ace/mode/vhdl",                 "vf":"ace/mode/visualforce",
                    "wollok":"ace/mode/wollok",             "xml":"ace/mode/xml",
                    "xq":"ace/mode/xquery",                 "yml":"ace/mode/yaml",
                    "django":"ace/mode/django"
                };

                Instance.init = function () {
                    $(document).ready(function () {
                        Instance.fileUrl= util.getParameter(document.URL, "fileUrl");
                        var ext = ((Instance.fileUrl!==null)?Instance.fileUrl.substr(Instance.fileUrl.lastIndexOf('.') + 1).toLowerCase():"");
                        
                        Instance.editor = ace.edit("editor");
                        Instance.editor.setTheme("ace/theme/eclipse");
                        Instance.editor.session.setMode("ace/mode/text");
                        if(ext in Instance.extensions){
                            Instance.editor.session.setMode(Instance.extensions[ext]);
                            console.log(Instance.extensions[ext]);
                        }
                        
                        Instance.loadContentFile();
                        
                        $("#saveButton").on("click", function(){
                            Instance.saveContentFile();
                        });
                        $("#reloadButton").on("click", function(){
                            Instance.loadContentFile();
                        });
                        $("#selectAllButton").on("click", function(){
                            Instance.selectAll();
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
                    $("#message").text("Cargado...");
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
                            $("#message").text("Contenido cargado");
                        }
                    });
                };
                
                this.saveContentFile= function(){
                    $("#message").text("Guardando...");
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
                            $("#message").text(data);
                        }
                    });
                };
                
                this.selectAll= function() {
                    Instance.editor.selectAll();
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
