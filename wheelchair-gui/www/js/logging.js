/**
 * Created by Richard on 10/04/15.
 */

var logging = {

    isLogging : false,
    currentLoggingFilename : '',
    currentLoggingStartTimestamp : 0,

    // Array to store a list of log files
    files : [],

    // Refresh the list of files from the wheelchair
    refresh : function(successCallback, errorCallback){

        api("/logging-list", logging.handleListRefresh, successCallback, errorCallback);
    },

    forceRefresh : function(successCallback, errorCallback){

        api("/logging-list/force", logging.handleListRefresh, successCallback, errorCallback);
    },

    startLogging : function(filename, successCallback, errorCallback){

        apiWithData("/logging-start",
            {filename : filename},
            function(response){

                logging.isLogging = true;
                logging.currentLoggingFilename = response.logging_filename;
                logging.currentLoggingStartTimestamp = response.logging_start_timestamp;
            },
            successCallback,
            errorCallback);
    },

    stopLogging : function(successCallback, errorCallback){

        api("/logging-end", logging.handleListRefresh, successCallback, errorCallback);
    },

    recordEvent : function(eventText, successCallback, errorCallback){

        apiWithData("/logging-event", {description : eventText}, null, successCallback, errorCallback);
    },

    handleListRefresh : function(response){

        // Clear the current array
        logging.files = [];

        // Get the new list of log files
        for(var x in response.log_files){
            logging.files[x] = {
                filename : response.log_files[x].logging_filename,
                filesize : response.log_files[x].logging_filesize
            };
        }
    }
};