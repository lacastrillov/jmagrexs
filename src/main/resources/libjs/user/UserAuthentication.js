/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


function UserAuthentication() {

    var Instance = this;
    
    Instance.context= "";
    
    Instance.MODULES= [];

    Instance.init = function () {
        $(document).ready(function () {
            
            $("#j_username, #j_password").keypress(function(e) {
                if(e.which === 13) {
                    Instance.authenticate("formLogin");
                }
            });
            
            var olvideContrasena = util.getParameter(document.URL, "olvideContrasena");
            if (olvideContrasena === "1") {
                Instance.changeForm("changePasswordDiv");
            }
        });
    };
    
    Instance.authenticate= function(idForm){
        Instance.userData=null;
        Ext.MessageBox.show({
            msg: 'Autenticando...',
            width:200,
            wait:true,
            waitConfig: {interval:200}
        });
        Instance.preAuthenticate(0, $("#"+idForm).serialize(), Instance.MODULES, function(){
            $("#"+idForm).submit();
        });
    };
    
    Instance.ajaxAuthenticate = function (idForm, callback) {
        Instance.userData=null;
        var all_modules= Instance.MODULES;
        all_modules.push("");
        Instance.preAuthenticate(0, $("#"+idForm).serialize(), all_modules, function(){
            callback(Instance.userData);
        });
    };
    
    Instance.preAuthenticate= function(index, formData, modules, callback){
        if(index<modules.length){
            $.ajax({
                url: modules[index]+"/account/ajax/authenticate",
                timeout: 20000,
                type: "POST",
                data: formData,
                cache: false,
                dataType: "json",
                success: function (data, status) {
                    Instance.userData= data;
                    Instance.preAuthenticate(index+1, formData, modules, callback);
                },
                error: function (xhr, status) {
                    console.log(xhr.status);
                }
            });
        }else{
            callback();
        }
    };
    
    Instance.logout= function(){
        Instance.userData=null;
        Ext.MessageBox.show({
            msg: 'Cerrando Sesi&oacute;n...',
            width:200,
            wait:true,
            waitConfig: {interval:200}
        });
        Instance.preLogout(0, Instance.MODULES, function(){
            location.href=Instance.context+"/security_logout";
        });
    };
    
    Instance.ajaxLogout= function (callback) {
        Instance.userData=null;
        var all_modules= Instance.MODULES;
        all_modules.push("");
        Instance.preLogout(0, all_modules, function(){
            callback();
        });
    };
    
    Instance.preLogout= function(index, modules, callback){
        if(index<modules.length){
            $.ajax({
                url: modules[index]+"/security_logout",
                timeout: 5000,
                type: "GET",
                cache: false,
                success: function (data, status) {
                    Instance.preLogout(index+1, modules, callback);
                },
                error: function (xhr, status) {
                    console.log(xhr.status);
                }
            });
        }else{
            callback();
        }
    };
    
    Instance.replicateAuthentication= function(callback){
        Instance.userData= null;
        var all_modules= Instance.MODULES;
        all_modules.push("");
        Instance.sessionModules= {};
        Instance.scanActiveSessions(0, all_modules, function(){
            var totalOffModules= 0;
            var offModules= [];
            for(var key in Instance.sessionModules){
                var sessionModule= Instance.sessionModules[key];
                if(sessionModule.session){
                    Instance.userData= sessionModule;
                }else{
                    totalOffModules++;
                    offModules.push(key.replaceAll("sm_",""));
                }
            }
            if(Instance.userData!==null && totalOffModules>0){
                Instance.preAuthenticate(0, "basicAuthorization="+Instance.userData.ba, offModules, function(){
                    callback({"replicated":true, "userData": Instance.userData});
                });
            }else{
                callback({"replicated":false, "userData": Instance.userData});
            }
        });
    };
    
    Instance.scanActiveSessions= function(index, modules, callback){
        if(index<modules.length){
            $.ajax({
                url: modules[index]+"/account/ajax/userInSession",
                timeout: 5000,
                type: "GET",
                cache: false,
                dataType: "json",
                success: function (data, status) {
                    Instance.sessionModules["sm_"+modules[index]]=data;
                    Instance.scanActiveSessions(index+1, modules, callback);
                },
                error: function (xhr, status) {
                    console.log(xhr.status);
                }
            });
        }else{
            callback();
        }
    };

    Instance.changePassword = function () {
        $("#message").html("Enviando...");
        var contrasena = $("#contrasena").val();
        var contrasenaControl = $("#contrasenaControl").val();
        if (contrasena === contrasenaControl) {
            $.ajax({
                url: $("#changePasswordForm").attr("action"),
                timeout: 20000,
                type: "POST",
                data: $("#changePasswordForm").serialize(),
                cache: false,
                dataType: "html",
                success: function (data, status) {
                    $("#message").html(data);
                },
                error: function (xhr, status) {
                    console.log(xhr.status);
                }
            });
        } else {
            $("#message").html("Las contrase&ntilde;as no coinciden...");
            return false;
        }
    };

    Instance.resetPassword = function () {
        $("#message").html("Enviando...");
        var correoElectronico = $("#correoElectronico").val();
        if (correoElectronico !== "") {
            $.ajax({
                url: $("#changePasswordForm").attr("action"),
                timeout: 20000,
                type: "POST",
                data: $("#changePasswordForm").serialize(),
                cache: false,
                dataType: "html",
                success: function (data, status) {
                    $("#message").html(data);
                },
                error: function (xhr, status) {
                    console.log(xhr.status);
                }
            });
        } else {
            $("#message").html("Ingrese un correo electronico");
            console.log("ingrese");
        }
    };

    Instance.changeForm = function (classForm) {
        $(".loginDiv").hide();
        $(".changePasswordDiv").hide();
        $("." + classForm).show();
    };

    Instance.init();
}