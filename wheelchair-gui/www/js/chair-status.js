/**
 * Created by Richard on 11/04/15.
 */

var chairStatus = {

    onStatusChangeCallbacks : [],
    onConnectionLostCallbacks : [],

    check : function(){

        api("/status",
            function(response){
                chairStatus.updateStatus(response);
            });
    },

    detectChanges : function(){

        api("/status/detect-changes",
            function(response){
                chairStatus.updateStatus(response);
                // Automatically start a new monitoring request with the same callback
                chairStatus.detectChanges();
            });
    },

    // TODO: Implement disconnection notification - maybe use a pulse AJAX request

    updateStatus : function(response){

        connection.setConnected(response.is_connected);
        connection.setBootComplete(response.is_boot_complete);
        logging.setIsLogging(response.is_logging);
        logging.currentLoggingFilename = response.logging_filename;
        logging.currentLoggingStartTimestamp = response.logging_start_timestamp;

        for(var x in chairStatus.onStatusChangeCallbacks){
            chairStatus.onStatusChangeCallbacks[x]();
        }
    },

    onStatusChange : function(callback){

        chairStatus.onStatusChangeCallbacks.push(callback);
    },

    onConnectionLost : function(callback){

        chairStatus.onConnectionLostCallbacks.push(callback);
    }
}

// Check the chair status be default, and keep and eye on it
chairStatus.check();
chairStatus.detectChanges();