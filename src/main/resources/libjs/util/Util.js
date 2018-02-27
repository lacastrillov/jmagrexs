/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

function Util() {

    var Instance = this;

    Instance.fieldRequired = "Campo obligatorio";
    Instance.fieldEmail = "Debe ser un correo v&aacute;lido";
    Instance.fieldLettersonly = "Campo de solo letras";
    Instance.fieldCardsonly = "Debe ser una tarjeta valida";
    Instance.fieldNumber = "Campo num&eacute;rico";

    Instance.init = function () {
        Instance.jssImported = {};
        Instance.indexMap= {};

        Instance.HtmlEntitiesMap = {
            "'": "&apos;", "<": "&lt;", ">": "&gt;",
            " ": "&nbsp;", "¡": "&iexcl;", "¢": "&cent;",
            "£": "&pound;", "¤": "&curren;", "¥": "&yen;",
            "¦": "&brvbar;", "§": "&sect;", "¨": "&uml;",
            "©": "&copy;", "ª": "&ordf;", "«": "&laquo;",
            "¬": "&not;", "®": "&reg;", "¯": "&macr;",
            "°": "&deg;", "±": "&plusmn;", "²": "&sup2;",
            "³": "&sup3;", "´": "&acute;", "µ": "&micro;",
            "¶": "&para;", "·": "&middot;", "¸": "&cedil;",
            "¹": "&sup1;", "º": "&ordm;", "»": "&raquo;",
            "¼": "&frac14;", "½": "&frac12;", "¾": "&frac34;",
            "¿": "&iquest;", "À": "&Agrave;", "Á": "&Aacute;",
            "Â": "&Acirc;", "Ã": "&Atilde;", "Ä": "&Auml;",
            "Å": "&Aring;", "Æ": "&AElig;", "Ç": "&Ccedil;",
            "È": "&Egrave;", "É": "&Eacute;", "Ê": "&Ecirc;",
            "Ë": "&Euml;", "Ì": "&Igrave;", "Í": "&Iacute;",
            "Î": "&Icirc;", "Ï": "&Iuml;", "Ð": "&ETH;",
            "Ñ": "&Ntilde;", "Ò": "&Ograve;", "Ó": "&Oacute;",
            "Ô": "&Ocirc;", "Õ": "&Otilde;", "Ö": "&Ouml;",
            "×": "&times;", "Ø": "&Oslash;", "Ù": "&Ugrave;",
            "Ú": "&Uacute;", "Û": "&Ucirc;", "Ü": "&Uuml;",
            "Ý": "&Yacute;", "Þ": "&THORN;", "ß": "&szlig;",
            "à": "&agrave;", "á": "&aacute;", "â": "&acirc;",
            "ã": "&atilde;", "ä": "&auml;", "å": "&aring;",
            "æ": "&aelig;", "ç": "&ccedil;", "è": "&egrave;",
            "é": "&eacute;", "ê": "&ecirc;", "ë": "&euml;",
            "ì": "&igrave;", "í": "&iacute;", "î": "&icirc;",
            "ï": "&iuml;", "ð": "&eth;", "ñ": "&ntilde;",
            "ò": "&ograve;", "ó": "&oacute;", "ô": "&ocirc;",
            "õ": "&otilde;", "ö": "&ouml;", "÷": "&divide;",
            "ø": "&oslash;", "ù": "&ugrave;", "ú": "&uacute;",
            "û": "&ucirc;", "ü": "&uuml;", "ý": "&yacute;",
            "þ": "&thorn;", "ÿ": "&yuml;", "Œ": "&OElig;",
            "œ": "&oelig;", "Š": "&Scaron;", "š": "&scaron;",
            "Ÿ": "&Yuml;", "ƒ": "&fnof;", "ˆ": "&circ;",
            "˜": "&tilde;", "Α": "&Alpha;", "Β": "&Beta;",
            "Γ": "&Gamma;", "Δ": "&Delta;", "Ε": "&Epsilon;",
            "Ζ": "&Zeta;", "Η": "&Eta;", "Θ": "&Theta;",
            "Ι": "&Iota;", "Κ": "&Kappa;", "Λ": "&Lambda;",
            "Μ": "&Mu;", "Ν": "&Nu;", "Ξ": "&Xi;",
            "Ο": "&Omicron;", "Π": "&Pi;", "Ρ": "&Rho;",
            "Σ": "&Sigma;", "Τ": "&Tau;", "Υ": "&Upsilon;",
            "Φ": "&Phi;", "Χ": "&Chi;", "Ψ": "&Psi;",
            "Ω": "&Omega;", "α": "&alpha;", "β": "&beta;",
            "γ": "&gamma;", "δ": "&delta;", "ε": "&epsilon;",
            "ζ": "&zeta;", "η": "&eta;", "θ": "&theta;",
            "ι": "&iota;", "κ": "&kappa;", "λ": "&lambda;",
            "μ": "&mu;", "ν": "&nu;", "ξ": "&xi;",
            "ο": "&omicron;", "π": "&pi;", "ρ": "&rho;",
            "ς": "&sigmaf;", "σ": "&sigma;", "τ": "&tau;",
            "υ": "&upsilon;", "φ": "&phi;", "χ": "&chi;",
            "ψ": "&psi;", "ω": "&omega;", "ϑ": "&thetasym;",
            "ϒ": "&Upsih;", "ϖ": "&piv;", "–": "&ndash;",
            "—": "&mdash;", "‘": "&lsquo;", "’": "&rsquo;",
            "‚": "&sbquo;", "“": "&ldquo;", "”": "&rdquo;",
            "„": "&bdquo;", "†": "&dagger;", "‡": "&Dagger;",
            "•": "&bull;", "…": "&hellip;", "‰": "&permil;",
            "′": "&prime;", "″": "&Prime;", "‹": "&lsaquo;",
            "›": "&rsaquo;", "‾": "&oline;", "⁄": "&frasl;",
            "€": "&euro;", "ℑ": "&image;", "℘": "&weierp;",
            "ℜ": "&real;", "™": "&trade;", "ℵ": "&alefsym;",
            "←": "&larr;", "↑": "&uarr;", "→": "&rarr;",
            "↓": "&darr;", "↔": "&harr;", "↵": "&crarr;",
            "⇐": "&lArr;", "⇑": "&UArr;", "⇒": "&rArr;",
            "⇓": "&dArr;", "⇔": "&hArr;", "∀": "&forall;",
            "∂": "&part;", "∃": "&exist;", "∅": "&empty;",
            "∇": "&nabla;", "∈": "&isin;", "∉": "&notin;",
            "∋": "&ni;", "∏": "&prod;", "∑": "&sum;",
            "−": "&minus;", "∗": "&lowast;", "√": "&radic;",
            "∝": "&prop;", "∞": "&infin;", "∠": "&ang;",
            "∧": "&and;", "∨": "&or;", "∩": "&cap;",
            "∪": "&cup;", "∫": "&int;", "∴": "&there4;",
            "∼": "&sim;", "≅": "&cong;", "≈": "&asymp;",
            "≠": "&ne;", "≡": "&equiv;", "≤": "&le;",
            "≥": "&ge;", "⊂": "&sub;", "⊃": "&sup;",
            "⊄": "&nsub;", "⊆": "&sube;", "⊇": "&supe;",
            "⊕": "&oplus;", "⊗": "&otimes;", "⊥": "&perp;",
            "⋅": "&sdot;", "⌈": "&lceil;", "⌉": "&rceil;",
            "⌊": "&lfloor;", "⌋": "&rfloor;", "⟨": "&lang;",
            "⟩": "&rang;", "◊": "&loz;", "♠": "&spades;",
            "♣": "&clubs;", "♥": "&hearts;", "♦": "&diams;"
        };
        Instance.configReplaceAll();
    };
    
    Instance.getIndex= function(name){
        if(name in Instance.indexMap){
            Instance.indexMap[name]+=1;
        }else{
            Instance.indexMap[name]=0;
        }
        return Instance.indexMap[name];
    };

    Instance.getPath = function (url) {
        var path = "";
        var index = url.indexOf("?");
        if (index !== -1) {
            path = url.substring(0, index);
        } else {
            path = url;
        }
        return path;
    };

    Instance.getParameters = function (url) {
        var valor = "";
        var index = url.indexOf("?");

        if (url.charAt(index) === "?") {
            valor = url.substring(index + 1, url.length);
        }

        return decodeURIComponent(valor);
    };

    Instance.getParameter = function (url, parameter) {
        var valor = "";
        var index = url.indexOf(parameter + "=") + parameter.length;

        if (url.charAt(index) === "=") {
            var result = url.indexOf("&", index);
            if (result === -1) {
                result = url.length;
            }
            ;
            valor = url.substring(index + 1, result);
        }

        return valor;
    };

    this.importJS = function (jsLib) {
        if (!(jsLib in Instance.jssImported)) {
            document.write('<script type="text/javascript" src="' + jsLib + '"></script>');
            Instance.jssImported[jsLib] = "1";
        }
    };

    this.setValue = function (id, value) {
        var s = document.getElementById(id);
        if (s !== null)
            s.value = value;
    };

    this.setHtml = function (id, html) {
        var s = document.getElementById(id);
        if (s !== null)
            s.innerHTML = html;
    };

    this.getHtml = function (id) {
        var s = document.getElementById(id);
        if (s !== null) {
            return s.innerHTML;
        }
        return null;
    };

    this.addUrlParameter = function (url, parameter, value) {
        if (url !== undefined) {
            var href = url;
            if (url.indexOf(parameter) !== -1) {
                href = Instance.removeUrlParameter(url, parameter);
            }
            if (href.indexOf("?") !== -1) {
                href += "&" + parameter + "=" + value;
            } else {
                href += "?" + parameter + "=" + value;
            }

            return href;
        }
        return "";
    };

    this.removeUrlParameter = function (url, parameter) {
        var value = Instance.getParameter(url, parameter);
        if (value !== '') {
            url = url.replace(parameter + "=" + value, "");
            url = url.replace("?&", "?").replace("&&", "&");
            var caracter = url.charAt(url.length - 1);
            if (caracter === "?" || caracter === "&") {
                url = url.substr(0, url.length - 1);
            }
        }

        return url;
    };

    this.replaceAll = function (texto, origen, remplazo) {
        var textoFinal = texto;
        if(textoFinal!==null){
            while (textoFinal.indexOf(origen) !== -1) {
                textoFinal = textoFinal.replace(origen, remplazo);
            }
        }
        return textoFinal;
    };
    
    this.configReplaceAll= function(){
        String.prototype.replaceAll = function(search, replacement) {
            var target = this;
            return target.replace(new RegExp(search, 'g'), replacement);
        };
    };
    
    this.checkAll= function(checked){
        var checkboxes = document.getElementsByTagName('input');
        if (checked) {
            for (var i = 0; i < checkboxes.length; i++) {
                if (checkboxes[i].type === 'checkbox') {
                    checkboxes[i].checked = true;
                }
            }
        } else {
            for (var i = 0; i < checkboxes.length; i++) {
                if (checkboxes[i].type === 'checkbox') {
                    checkboxes[i].checked = false;
                }
            }
        }
    };
    
    this.createForm= function(config){
        var form = document.createElement("form");
        if ('id' in config) {
            $(form).attr("id", config.id);
        }
        if ('action' in config) {
            $(form).attr("action", config.action);
        }
        if ('method' in config) {
            $(form).attr("method", config.method);
        }
        if ('target' in config) {
            $(form).attr("target", config.target);
        }
        if('data' in config){
            config.data.forEach(function(param){
                var input= '<input ';
                if('type' in param){
                    input+='type="'+param.type+'" ';
                }else{
                    input+='type="hidden" ';
                }
                if('id' in param){
                    input+='id="'+param.id+'" ';
                }
                if('name' in param){
                    input+='name="'+param.name+'" ';
                }
                if('value' in param){
                    input+='value="'+param.value+'" ';
                }
                input+=' />';
                $(form).append(input);
            });
        }
        document.body.appendChild(form);
        
        return form;
    };

    this.getCookie = function (name) {
        var nameEQ = name + "=";
        var ca = document.cookie.split(';');
        for (var i = 0; i < ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0) === ' ')
                c = c.substring(1, c.length);
            if (c.indexOf(nameEQ) === 0) {
                return c.substring(nameEQ.length, c.length);
            }
        }
        return null;
    };

    this.setCookie = function (name, value) {
        var argv = Instance.setCookie.arguments;
        var argc = Instance.setCookie.arguments.length;
        var expires = (argc > 2) ? argv[2] : null;
        var path = (argc > 3) ? argv[3] : null;
        var domain = (argc > 4) ? argv[4] : null;
        var secure = (argc > 5) ? argv[5] : false;
        document.cookie = name + "=" + escape(value) +
                ((expires === null) ? "" : ("; expires=" + expires.toGMTString())) +
                ((path === null) ? ";path=/" : (";path=" + path)) +
                ((domain === null) ? "" : ("; domain=" + domain)) +
                ((secure === true) ? "; secure" : "");
    };

    this.htmlEntitiesEncode = function (string) {
        string = string.replace(/&/g, '&amp;');
        string = string.replace(/"/g, '&quot;');
        for (var key in Instance.HtmlEntitiesMap) {
            var entity = Instance.HtmlEntitiesMap[key];
            var regex = new RegExp(key, 'g');
            string = string.replace(regex, entity);
        }
        return string;
    };

    this.htmlEntitiesDecode = function (string) {
        for (var key in Instance.HtmlEntitiesMap) {
            var entity = Instance.HtmlEntitiesMap[key];
            var regex = new RegExp(entity, 'g');
            string = string.replace(regex, key);
        }
        string = string.replace(/&quot;/g, '"');
        string = string.replace(/&amp;/g, '&');
        return string;
    };
    
    this.processTemplate= function(template, item){
        var result= template;
        var totalParams=0;
        var params= [];
        var found= false;
        for(var i=0; i<template.length; i++){
            if(found && template.charAt(i)!=="}"){
                params[totalParams]+=template.charAt(i);
            }
            if(template.charAt(i)==="{" && template.charAt(i-1)==="="){
                found=true;
                params[totalParams]="";
            }else if(template.charAt(i)==="}" && found){
                found=false;
                totalParams++;
            }
        }
        params.forEach(function(param){
            var toReplace= "={"+param+"}";
            var parts= param.split(".");
            var value;
            try{
                switch(parts.length){
                    case 1:
                        value= item[param];
                        break;
                    case 2:
                        value= item[parts[0]][parts[1]];
                        break;
                    case 3:
                        value= item[parts[0]][parts[1]][parts[2]];
                        break;
                    case 4:
                        value= item[parts[0]][parts[1]][parts[2]][parts[3]];
                        break;
                    case 5:
                        value= item[parts[0]][parts[1]][parts[2]][parts[3]][parts[4]];
                        break;
                }
            }catch(e){
            }
            if(value!==undefined){
                result= Instance.replaceAll(result, toReplace, value);
            }
        });
        
        return result;
    };
    
    this.objectToJSONMenu= function(object, expanded){
        var menuObj= {"text":"root",expanded: true,"children":[]};
        
        Instance.addSubmenu(menuObj.children, object, expanded);
        
        return menuObj;
    };
    
    this.addSubmenu= function(menuParent, object, expanded){
        for (var key in object) {
            var type= typeof(object[key]);
            var item={};
            var text= key;
            var id= key+"_"+Instance.getIndex(key);
            if(key.indexOf("::")!==-1){
                id= key.split("::")[0];
                text= key.split("::")[1];
            }
            
            if(type==="string"){
                item= {"text": text+" : "+"\""+object[key]+"\"", "leaf":true};
            }else if(type==="number" || type==="boolean"){
                item= {"text": text+" : "+""+object[key]+"", "leaf":true};
            }else if(type==="object"){
                if(Array.isArray(object)){
                    item= {"text":menuParent.length, "children": [], "expanded": expanded};
                }else{
                    item= {"id":id, "text":text, "children": [], "expanded": expanded};
                }
                Instance.addSubmenu(item.children, object[key], expanded);
            }
            menuParent.push(item);
        }
    };
    
    this.remakeJSONObject= function (json){
        var source= json;
        var finalObject= {};
        
        for (var key in source) {
            var value= source[key];
            Instance.assignValue(finalObject, key, value);
        }
        return finalObject;
    };
    
    this.assignValue= function(obj, key, value){
        //console.log(obj);
        //console.log(":::: ITER "+key+"="+value);
        if(key.indexOf(".")!==-1){
            //console.log("KEY 1 === ");
            var firstKey= key.substr(0, key.indexOf("."));
            var remainingKey= key.substr(key.indexOf(".")+1, key.length);
            //console.log("firstKey "+firstKey);
            //console.log("remainingKey "+remainingKey);
            if(firstKey.indexOf("[")!==-1 && firstKey.indexOf("]")!==-1){
                var secondKey= firstKey.substr(0, firstKey.indexOf("["));
                //console.log("secondKey "+secondKey);
                if(secondKey in obj === false){
                    obj[secondKey]= [];
                }
                var index= Number(firstKey.replace(secondKey,"").replace("[","").replace("]",""));
                //console.log("index "+index);
                if(obj[secondKey][index]===undefined){
                    obj[secondKey][index]={};
                }
                Instance.assignValue(obj[secondKey][index], remainingKey, value);
            }else{
                //console.log("obj[firstKey]");
                if(firstKey in obj === false){
                    obj[firstKey]= {};
                }
                Instance.assignValue(obj[firstKey], remainingKey, value);
            }
            
        }else if(key.indexOf("[")!==-1 && key.indexOf("]")!==-1){
            //console.log("KEY 2 === ");
            var firstKey= key.substr(0, key.indexOf("["));
            //console.log("firstKey "+firstKey);
            if(firstKey in obj === false){
                obj[firstKey]= [];
            }
            var index= Number(key.replace(firstKey,"").replace("[","").replace("]",""));
            //console.log("index "+index);
            obj[firstKey][index]=value;
        }else{
            //console.log("KEY 3 === ");
            obj[key]= value;
        }
    };
    
    this.unremakeJSONObject= function (json){
        var source= json;
        var finalObject= {};
        
        for (var key in source) {
            Instance.assignSingleLevelValue(key, finalObject, source[key]);
        }
        
        return finalObject;
    };
    
    this.assignSingleLevelValue= function(level, finalObject, object){
        var type= typeof(object);

        if(type==="string" || type==="number" || type==="boolean"){
            finalObject[level]= object;
        }else if(type==="object"){
            if(Array.isArray(object)){
                for(var i=0; i<object.length; i++){
                    Instance.assignSingleLevelValue(level+"["+i+"]", finalObject, object[i]);
                }
            }else{
                for (var key in object) {
                    Instance.assignSingleLevelValue(level+"."+key, finalObject, object[key]);
                }
                    
            }
        }
    };

    Instance.init();
}
var util = new Util();