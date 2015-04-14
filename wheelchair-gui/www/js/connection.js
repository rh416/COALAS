/**
 * Created by Richard on 10/04/15.
 */

var connection = {

    connected : false,
    ports : [],

    onConnectCallbacks : [],
    onDisconnectCallbacks : [],

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
                setConnected(response.is_connected);
            },
            successCallback,
            errorCallback);
    },

    disconnect : function(successCallback, errorCallback){

        api("/serial-disconnect",
            function(response){
                setConnected(response.is_connected);
            },
            successCallback,
            errorCallback);
    },

    setConnected : function(connected){

        // Only do anything if the connected state has changed
        if(connection.connected !== connected){
            // Record the new state
            connection.connected = connected;

            // Iterate over both sets of callbacks and execute them
            for(var x in connection.onConnectCallbacks){
                connection.onConnectCallbacks[x]();
            }
            for(var x in connection.onDisconnectCallbacks){
                connection.onDisconnectCallbacks[x]();
            }
        }
    },

    onConnect : function(callback){

        connection.onConnectCallbacks.push(callback);
    },

    onDisconnect : function(callback){

        connection.onDisconnectCallbacks.push(callback);
    }
}
