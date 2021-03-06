<script>

function EntityExtInit(){
    
    var Instance= this;
    
    Instance.init= function(){
        Ext.Loader.setConfig({enabled: true});

        Ext.Loader.setPath('Ext.ux', ExtJSLib+'/examples/ux');
        
        Instance.setAjaxTimeout(240000);

        Ext.require([
            'Ext.tip.QuickTipManager',
            'Ext.container.ButtonGroup',
            'Ext.container.Viewport',
            'Ext.layout.*',
            'Ext.form.field.Trigger',
            'Ext.form.Panel',
            'Ext.form.Label',
            'Ext.grid.*',
            'Ext.data.*',
            'Ext.menu.*',
            'Ext.tree.*',
            'Ext.selection.*',
            'Ext.tab.Panel',
            'Ext.util.Format',
            'Ext.util.History',
            'Ext.ux.form.MultiSelect',
            'Ext.ux.layout.Center',
            'Ext.ux.GroupTabPanel',
            'Ext.window.MessageBox'
        ]);
        
        //
        // This is the main layout definition.
        //
        Ext.onReady(function(){

            Ext.tip.QuickTipManager.init();

            Ext.History.init();

            Instance.homeExtViewport= new ${entityName}ExtViewport();

            Instance.homeExtViewport.renderViewport();

            //Debe ser siempre la ultima linea**************************
            mvcExt.setHomeRequest("/${entityRef}");
            mvcExt.processFirtsRequest();
        });
    };
    
    Instance.setAjaxTimeout= function(value){
        Ext.Ajax.timeout= value;
        Ext.override(Ext.form.Basic, { timeout: Ext.Ajax.timeout / 1000 });
        Ext.override(Ext.data.proxy.Server, { timeout: Ext.Ajax.timeout });
        Ext.override(Ext.data.Connection, { timeout: Ext.Ajax.timeout });
    };
    
    Instance.init();
}
</script>