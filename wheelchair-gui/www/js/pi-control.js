/**
 * Created by Richard on 10/04/15.
 */

var pi = {

    system_time_at_retrieval : 0,
    system_time_retrieved : 0,
    systemTimeFormat : "D-MMM-YYYY HH:mm:ss (ZZ)",
    hasAskedToUpdateTime : false,
    // Maximum time offset (in seconds) between Raspberry Pi and current system before a warning is shown
    maxSystemTimeOffset : 10,

    // Store a reference to the display update timer
    displayUpdateTimer : null,

    shutdown : function(successCallback, errorCallback){

        api("/rpi/shutdown", null, successCallback, errorCallback);
    },

    checkSystemTime : function(successCallback, errorCallback){

        api("/rpi/get-time",
            function(response){
                pi.recordSyncedTime(response);
            },
            successCallback,
            errorCallback);
    },

    systemTime : function(){

        return parseInt(pi.system_time_at_retrieval + ((new Date().getTime() / 1000) - pi.system_time_retrieved));
    },

    systemTimeMoment : function(){

        return moment.unix(pi.systemTime());
    },

    synchroniseTime : function(successCallback, errorCallback){

        apiWithData("/rpi/set-time",
            {
                newDateTime : moment().format("X")      // Setting the time needs to be sent a Unix timestamp
            },
            function(response){
                pi.recordSyncedTime(response);
            },
            successCallback,
            errorCallback);
    },

    recordSyncedTime : function(response){

        pi.system_time_at_retrieval = parseInt(response.time / 1000);
        pi.system_time_retrieved = parseInt(new Date().getTime() / 1000);
    }
}