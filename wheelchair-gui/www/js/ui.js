var ui = {

    modules : [],

    register : function(module){

        // Add the given module to the modules array
        ui.modules.push(module);
    },

    init : function(){
        for(var x in ui.modules){
            var module = ui.modules[x];
            console.log("Found module: " + module);
            if(ui[module].init){
                ui[module].init();
            }
        }

        $(function(){
            $('.ripple').materialripple();
        });

    }

};

// Things to do on page load
$(document).ready(function(){
    // Setup the user interface
    ui.init();
});