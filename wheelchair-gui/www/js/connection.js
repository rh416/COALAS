/**
 * Created by Richard on 10/04/15.
 */

var connection = {

    connected : false,
    isBootComplete : false,
    ports : [],

    onConnectCallbacks : [],
    onDisconnectCallbacks : [],
    onBootCompleteCallbacks : [],

    loadAvailablePorts : function(successCallback, errorCallback){

        // Load the list of serial ports
        api("list-ports",
            function(response){
                connection.ports = response.ports;
            },
            successCallback,
            errorCallback);
    },

    connect : function(port, baud, successCallback, errorCallback){

        apiWithData("/serial-connect",
            {
                port : port,
                baud_rate : baud
            },
            function(response){
                connection.setConnected(response.is_connected);
            },
            successCallback,
            errorCallback);
    },

    disconnect : function(successCallback, errorCallback){

        api("/serial-disconnect",
            function(response){
                connection.setConnected(response.is_connected);
            },
            successCallback,
            errorCallback);
    },

    setConnected : function(connected){

        // Only do anything if the connected state has changed
        if(connection.connected !== connected){
            // Record the new state
            connection.connected = connected;

            // Iterate over the correct set of callbacks and execute them
            if(connected){
                for(var x in connection.onConnectCallbacks){
                    connection.onConnectCallbacks[x]();
                }
            } else {
                for(var x in connection.onDisconnectCallbacks){
                    connection.onDisconnectCallbacks[x]();
                }
            }
        }
    },

    setBootComplete : function(isBootComplete){

        // Only do anything if the state has changed
        if(connection.isBootComplete !== isBootComplete){
            // Record the new state
            connection.isBootComplete = isBootComplete;

            // Iterate over the boot callbacks and execute them
            for(var x in connection.onBootCompleteCallbacks){
                connection.onBootCompleteCallbacks[x]();
            }
        }
    },

    onConnect : function(callback){

        connection.onConnectCallbacks.push(callback);
    },

    onDisconnect : function(callback){

        connection.onDisconnectCallbacks.push(callback);
    },

    onBootComplete : function(callback){

        connection.onBootCompleteCallbacks.push(callback);
    }
}
