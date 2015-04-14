/**
 * Created by Richard on 13/04/15.
 */

ui.pi_control = {

    init : function(){
        // Get the current system time from the wheelchair
        pi.checkSystemTime(ui.pi_control.displayCurrentSystemTimes);

        // Display the current local time, and start the timer to update both sets of times
        ui.pi_control.displayCurrentSystemTimes();


        $('#btn-pi-shutdown').click(function(){

            // Show warning dialog
            dialog.show({
                title : 'Confirm Shutdown',
                message : 'Are you sure you want to shutdown the Raspberry Pi?',
                positive : {
                    text : 'Yep, shut it down',
                    callback : function(){
                        pi.shutdown(null, function(){alert("Request to shutdown failed. Please try again, then pull the power if required.")});
                    }
                },
                negative : {
                    text : 'No'
                }
            });

        })

        $('#pi-time-buttons .btn-primary').click(function(){

            pi.synchroniseTime(ui.pi_control.displayTimeSyncSuccess, function(){
                alert("Synchronising time failed!");
            });
        });
    },

    displayTimeSyncSuccess : function(){

        $('#pi-time-buttons .btn-primary').text("Time Synchronised Successfully");

        setInterval(function(){
            $('#pi-time-buttons .btn-primary').text("Synchronise Time");
        }, 1000);

    },

    displayCurrentSystemTimes : function(){

        // If system_time_at_retrieval is 0, it has not been retrieved
        if(pi.system_time_at_retrieval > 0){
            var t = pi.systemTimeMoment();

            // Update the displayed pi time - both on the main UI and in the dialog, if it is showing
            $('#pi-time').text(t.format(pi.systemTimeFormat));
            $('#pi-dialog-time').text(t.format(pi.systemTimeFormat));

            if(Math.abs(t - moment()) >= (pi.maxSystemTimeOffset * 1000)){
                // If the Pi system time too far out, display a warning
                $('#pi-time-container').addClass("alert-danger");

                if(!pi.hasAskedToUpdateTime){
                    // Prevent the dialog from showing more than once
                    pi.hasAskedToUpdateTime = true;
                    dialog.show({
                        title : 'System Time Incorrect',
                        message : 'The time on the Raspberry Pi <strong id="pi-dialog-time">' + t.format(pi.systemTimeFormat) + '</strong> appears to be incorrect<br />Would you like to reset it to match your system\'s current time and date?',
                        positive : {
                            text : 'Yes, set the time',
                            callback : function(){

                                pi.synchroniseTime(ui.pi_control.displayTimeSyncSuccess, function(){
                                    alert("Synchronising time failed!");
                                })
                            }
                        },
                        negative : {
                            text : 'No, the time is okay'
                        }
                    });
                }
            }
        }

        // Display the current system time
        $('#local-time').text(moment().format(pi.systemTimeFormat));

        // Refresh the display every second
        clearTimeout(pi.displayUpdateTimer);
        pi.displayUpdateTimer = setTimeout(ui.pi_control.displayCurrentSystemTimes, 300);
    }
}

ui.register("pi_control");
