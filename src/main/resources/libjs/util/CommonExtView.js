/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


function CommonExtView(){
    
    var Instance= this;
    
        
    Instance.init= function(){
        Instance.errorGeneral= "Error de servidor";
        Instance.error403= "Usted no tiene permisos para realizar esta operaci&oacute;n";
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
        var gb= kb/1024;
        if(gb<1024){
            return gb.toFixed(2) + " GB";
        }
        var tb= gb/1024;
        return tb.toFixed(2) + " TB";
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