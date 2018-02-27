/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


function Message(){
    
    var Instance = this;
    
    Instance.init = function () {
        
    };
    
    Instance.showMessage= function(title, message){
        Ext.MessageBox.show({
            title: title,
            msg: message,
            icon: Ext.MessageBox.INFO,
            buttons: Ext.Msg.OK
        });
    };
    
    Instance.init();
}