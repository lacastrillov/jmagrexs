<%@ page language="java" contentType="application/javascript; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:if test="${false}">
<script>
</c:if>
function UserAuthentication() {

    var Instance = this;
    
    Instance.applicationContext= "${serverDomain.applicationContext}";
    
    Instance.portalContext= "${serverDomain.portalContext}";
    
    Instance.MODULES= ${serverDomain.modulesJson};
    
    Instance.ALL_MODULES= Instance.MODULES;

    Instance.init = function () {
        Instance.ALL_MODULES.push(Instance.portalContext);
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
    
    Instance.getAllModules= function(){
        if(Instance.ALL_MODULES===null){
            Instance.ALL_MODULES = Instance.MODULES;
            Instance.ALL_MODULES.push(Instance.portalContext);
        }
        return Instance.ALL_MODULES;
    };
    
    Instance.authenticate= function(idForm){
        Instance.userData=null;
        Ext.MessageBox.show({
            msg: 'Autenticando...',
            width:200,
            wait:true,
            waitConfig: {interval:200}
        });
        Instance.preAuthenticate(0, $("#"+idForm).serialize(), Instance.MODULES, function(data){
            if(data.success){
                $("#"+idForm).submit();
            }else{
                Ext.MessageBox.show({
                    title: 'ERROR',
                    msg: data.message,
                    icon: Ext.MessageBox.ERROR,
                    buttons: Ext.Msg.OK
                });
            }
        });
    };
    
    Instance.ajaxAuthenticate = function (idForm, callback) {
        Instance.userData=null;
        Instance.preAuthenticate(0, $("#"+idForm).serialize(), Instance.ALL_MODULES, callback);
    };
    
    Instance.preAuthenticate= function(index, formData, modules, callback){
        if(index<modules.length){
            $.ajax({
                url: Instance.applicationContext+modules[index]+"/account/ajax/authenticate",
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
                    callback({success:false, message:"Error de servidor"});
                }
            });
        }else{
            callback(Instance.userData);
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
            location.href=Instance.applicationContext+Instance.portalContext+"/security_logout";
        });
    };
    
    Instance.ajaxLogout= function (callback) {
        Instance.userData=null;
        Instance.preLogout(0, Instance.ALL_MODULES, callback);
    };
    
    Instance.preLogout= function(index, modules, callback){
        if(index<modules.length){
            $.ajax({
                url: Instance.applicationContext+modules[index]+"/account/ajax/logout",
                timeout: 5000,
                type: "GET",
                cache: false,
                success: function (data, status) {
                    Instance.preLogout(index+1, modules, callback);
                },
                error: function (xhr, status) {
                    console.log(xhr.status);
                    callback(false);
                }
            });
        }else{
            callback(true);
        }
    };
    
    Instance.replicateAuthentication= function(callback){
        Instance.userData= null;
        Instance.sessionModules= {};
        Instance.scanActiveSessions(0, Instance.ALL_MODULES, function(){
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
                url: Instance.applicationContext+modules[index]+"/account/ajax/userInSession",
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
<c:if test="${false}">
</script>
</c:if>