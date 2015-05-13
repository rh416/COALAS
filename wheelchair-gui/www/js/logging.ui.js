// Logging UI Control

ui.logging = {

    loggingTimer : null,

    init : function(){

        // Refresh the log file list when the chair boots
        connection.onBootComplete(function(){
            ui.logging.refreshList(true);
        });

        connection.onDisconnect(function(){
            ui.logging.updateLoggingUI();
        })

        // Ensure that the logging timer is being update
        ui.logging.updateLoggingTimer();

        // Create run numbers from 1 to 99
        var runNumber = $('#logging-run');
        for(var i = 1; i < 100; i++){
            runNumber.append("<option value=\"" + i + "\">" + i + "</option>")
        }
        runNumber.selectpicker("refresh");

        // Load the user list
        users.get(function(){
            ui.logging.outputUserList();
        }, function(){
            ui.logging.outputUserList();
        });

        // Create a listener to react to any status changes
        chairStatus.onStatusChange(ui.logging.updateLoggingUI);

        // Create a listener to update the log file list when logging complete
        logging.onLoggingComplete(function(){
            ui.logging.refreshList();
        })

        $('#logging-buttons .btn-primary').click(function(){

            if(logging.isLogging){
                logging.stopLogging(function(){

                    // Update the log file list
                    ui.logging.updateLoggingUI();
                });
            } else {
                // Build the filename
                var user = $('#logging-user').val();
                var run = $('#logging-run').val();
                var runtype = $('#logging-runtype').val();

                var filename = (user + padLeft(run, 2) + runtype + ".csv").toUpperCase();

                logging.startLogging(filename, ui.logging.updateLoggingUI, function(){

                    alert("It was not possible to start logging");
                });
            }
        });

        $('#logging-events button').click(function(){

            var eventButton = $(this);
            eventButton.addClass("btn-primary");

            var matches = eventButton.text().match(/\b(\w)/g); // Create an array of the first letter of each word
            var eventText = matches.join(''); // Join it back together, in a string

            logging.recordEvent(eventText,
                function(){
                    eventButton.addClass("btn-success");
                    setTimeout(function(){
                        eventButton.removeClass("btn-success");
                    }, 5000);
                }, function(){
                    eventButton.addClass("btn-danger");
                    alert("Event could not be saved");
                }
            );
        });

        $('#logging-user').change(function(){
            if($(this).val() == "new"){
                dialog.show({
                    title : 'Create New User',
                    message : '<div class="form-group">' +
                        '<label for="create-user-id">Please enter the new user id below:</label>' +
                        '<input type="text" id="create-user-id" class="form-control" />' +
                        '<div id="create-user-error"></div>' +
                        '</div>',
                    positive : {
                        text : 'Create New User',
                        callback : function(){

                            var newUserId = $('#create-user-id').val();

                            users.list.push(newUserId);
                            users.save(function(){

                                ui.logging.outputUserList(newUserId);
                            },
                            function(){
                                alert("New user could not be created");
                            });
                        }
                    },
                    negative : {
                        text : 'Cancel'
                    }
                });

                $('#create-user-id').on("keyup keypress keydown blur focus", ui.logging.validateNewUser);
                $('#dialog-task-confirmation .btn-danger').prop('disabled', true);
            } else if($(this).val() != ""){
                // If a user has been selected, reset the run information and re-enable start logging button
                $('#logging-run').val(1).selectpicker("refresh");
                $('#logging-runtype').val('A').selectpicker("refresh");
            }

            ui.logging.enableDisableStartLoggingButton();
        });

    },

    validateNewUser : function(){

        var val = $(this).val().trim();
        var illegalChars = [":", "/", "\\", ".", "?", "#", "<", ">", "$", "+", "%", "!", "`", "&", "*", "'", "|", "{", "}", "\"", "=", "@"];

        if(val ===''){
            ui.logging.showNewUserError("");
        } else if(val === 'new'){
            ui.logging.showNewUserError("New user cannot be called 'new'");
        } else if(stringContains(val, illegalChars)){
            ui.logging.showNewUserError("New user cannot contain any of the following: " + illegalChars.join("&nbsp;&nbsp;"));
        } else if(val.length > 5){
            ui.logging.showNewUserError("New user name cannot be longer than 5 characters");
        } else {
            $('#create-user-error').css("visibility", "hidden");
            $('#dialog-task-confirmation .btn-danger').prop("disabled", false);
        }
    },

    showNewUserError : function(message){

        $('#create-user-error').html(message).css("visibility", "visible").parent().addClass("has-feedback");
        $('#dialog-task-confirmation .btn-danger').prop("disabled", true);
    },

    refreshList : function(forced){

        if(forced === true){
            logging.forceRefresh(ui.logging.outputLogFileList);
        } else {
            logging.refresh(ui.logging.outputLogFileList);
        }
    },

    outputUserList : function(selectUser){

        var userSelect = $('#logging-user');
        userSelect.html("");

        if(users.list === null){
            userSelect.append("<option value=\"\">Loading Failed</option>");
        } else {
            userSelect.append("<option value=\"\">Select User</option>");
            for(var x in users.list){
                var user = users.list[x];
                userSelect.append("<option value=\"" + user + "\"" + ((user === selectUser) ? " selected" : "") + ">" + user + "</option>");
            }
        }

        userSelect.append("<option value=\"new\">New&hellip;</option>");
        userSelect.selectpicker("refresh");

        // Make sure the start logging button is in the correct state
        ui.logging.enableDisableStartLoggingButton();
    },

    // Output the list of log files to the screen
    outputLogFileList : function(){

        // Fill the log file list
        if(logging.files.length > 0){
            // If there are some log files, hide the "empty" list and show the main list before filling it up
            $('#list-log-files-empty').hide();
            $('#list-log-files').show();

            // Empty the logFile list
            $('#list-log-files').html("");

            var logFile;
            var logFileList;
            var logElement;
            var logElementTemplate;

            logFileList = $('#list-log-files');
            logElementTemplate = $('#listitem-log-file-template');
            logElementTemplate.show();

            logFileList.show();

            for(var x in logging.files){
                logFile = logging.files[x];
                logElement = logElementTemplate.clone();

                // Set the correct parameters
                logElement.find('.filename').html(logFile.filename);
                logElement.find('.filesize').html(formatFilesize(logFile.filesize));

                logFileList.append(logElement);
            }
        } else {
            // If there are no log files, hide the log file list
            $('#list-log-files').hide();
        }
    },

    updateLoggingTimer : function(){

        if(logging.currentLoggingStartTimestamp > 0){
            var logTimestamp = moment.unix(logging.currentLoggingStartTimestamp / 1000);
            var loggingDuration = moment.utc(pi.systemTimeMoment().diff(logTimestamp));
            var loggingDurationOutput = $('#logging-timer');
            var format = "mm:ss";

            if(loggingDuration >= 0){
                if(parseInt(loggingDuration.format("X")) > 3600){
                    format = "hh:mm:ss";
                }
                loggingDurationOutput.text(loggingDuration.format(format));
            } else {
                loggingDurationOutput.text("00:00");
            }
        }

        clearTimeout(ui.logging.loggingTimer);
        ui.logging.loggingTimer = setTimeout(ui.logging.updateLoggingTimer, 1000);
    },

    updateLoggingUI : function(){

        // Update the current log file information
        $('#logging-current-filename').text(logging.currentLoggingFilename);
        ui.logging.updateLoggingTimer();

        // Hide and display the relevant parts
        if(connection.connected && logging.isLogging){
            $('.show-when-logging').show();
            $('.show-when-not-logging').hide();
            btnText($('#logging-buttons .btn-primary'), "Finish Logging").addClass("btn-danger");
        } else {
            $('.show-when-logging').hide();
            $('.show-when-not-logging').show();
            btnText($('#logging-buttons .btn-primary'), "Start Logging").removeClass("btn-danger");
        }

        // Make sure the start logging button is in the correct state
        ui.logging.enableDisableStartLoggingButton();
    },

    enableDisableStartLoggingButton : function(){

        var buttonDisabled = true;

        if(logging.isLogging){
            // Always enable the button when logging
            buttonDisabled = false;
        } else {
            var selectedUser = $('#logging-user').val().trim();

            if(selectedUser == '' || selectedUser == 'new'){
                buttonDisabled = true
            } else {
                buttonDisabled = false;
            }
        }

        $('#logging-buttons .btn-primary').prop("disabled", buttonDisabled);
    }
}

ui.register("logging");