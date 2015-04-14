/**
 * Created by Richard on 13/04/15.
 */

ui.connection = {

    init : function(){

        // Ensure the display is correct
        ui.connection.updateConnectDisconnectForm();

        // Request serial port list from chair
        connection.loadAvailablePorts(ui.connection.updateSerialPortList);

        // Create a listener to detect any status changes
        chairStatus.onStatusChange(ui.connection.updateConnectDisconnectForm);

        // Connect / Disconnect button
        $('#form-connection .btn-primary').click(function(){

            var connectBtn = $('#form-connection .btn-primary');

            if(connection.connected){
                dialog.show({
                    title : 'Confirm Disconnection',
                    message : 'Are you sure you want to disconnect from the wheelchair. You will not be able to reconnect without ending any logging sessions that are in progress',
                    positive : {
                        text : 'Yes, I\'m sure',
                        callback : function(){
                            connectBtn.text("Disconnecting...");
                            connection.disconnect(ui.connection.updateConnectDisconnectForm, ui.connection.updateConnectDisconnectForm);
                        }
                    },
                    negative : {
                        text : 'No, don\'t disconnect'
                    }
                });
            } else {
                dialog.show({
                    title : 'Confirm Connection',
                    message : 'Are you sure you want to connect to the wheelchair.<br /><strong>This will cause the firmware to restart and the wheelchair to stop suddenly if it is moving.</strong>',
                    positive : {
                        text : 'Yes, I\'m sure',
                        callback : function(){
                            connectBtn.text("Connecting...");
                            connection.connect($('#port').val(), $('#baudrate').val(),
                                ui.connection.updateConnectDisconnectForm,
                                function(response){
                                    alert("Connecting to the wheelchair failed!\n" + response.error.message);
                                    ui.connection.updateConnectDisconnectForm();
                                });
                        }
                    },
                    negative : {
                        text : 'No, don\'t connect'
                    }
                });
            }
        });
    },

    updateSerialPortList : function(){

        var portSelect = $("select#port");
        portSelect.html("");

        if(connection.ports.length > 0){
            for(var x in connection.ports){
                var port = connection.ports[x];
                portSelect.append("<option value='" + port + "'>" + port + "</option>");
            }
        } else {
            portSelect.append("<option>No Serial Ports Available</option>");
        }
        portSelect.selectpicker('refresh');
    },

    updateConnectDisconnectForm : function(){

        var connectButton = $("#form-connection .btn-primary");
        var connectionOptions = $("#form-connection-options");
        var resetWarning = $("#connection-warning");

        if(connection.connected){
            connectButton.text("Disconnect");
            connectButton.addClass("btn-danger");
            connectionOptions.hide();
            resetWarning.hide();
            $('.show-when-connected').show();
            $('.show-when-disconnected').hide();
        } else {
            connectButton.text("Connect");
            connectButton.removeClass("btn-danger");
            connectionOptions.show();
            resetWarning.show();
            $('.show-when-connected').hide();
            $('.show-when-disconnected').show();
        }
    }
}

ui.register("connection");
