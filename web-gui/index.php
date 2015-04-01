<?php

include("tasks.php");
include("functions.php");

?><!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>COALAS Web Frontend</title>

    <base href="/" />

    <!-- Bootstrap core CSS -->
    <link href="css/bootstrap.min.css" rel="stylesheet" />
    <!-- Bootstrap select -->
    <link href="css/bootstrap-select.min.css" rel="stylesheet" />

    <link href="css/gui.css" rel="stylesheet" />

</head>

<body>

<nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
    <div class="container">
        <div class="navbar-header">
            <a class="navbar-brand" href="#">COALAS UI</a>
        </div>
        <div class="navbar-info navbar-header pull-right">
            <a class="navbar-brand">Current Process Time: 04:03</a>
        </div>
    </div>
</nav>
<div class="container">

    <div class="row">
        <div class="col-md-4">
            <h3 id="header-control">Control</h3>

            <form role="form" id="control-form">

                <div class="form-group" id="form-group-task">
                    <label for="task">Task:</label>
                    <select id="task" class="selectpicker form-control"><?php

                        foreach($tasks as $id=>$task){
                            echo "\n                        <option value=\"" . $id . "\">" . $task["text"] . "</option>";
                        }

                        ?>
                    </select>
                </div>

                <div class="form-group" id="form-group-port">
                    <label for="port">Port:</label>
                    <select id="port" class="selectpicker form-control"><?php

                        outputSerialPortList();

                        ?>
                    </select>
                </div>
                <div class="form-group" id="form-group-baudrate">
                    <label for="baudrate">Baud Rate:</label>
                    <input type="number" id="baudrate" class="form-control" placeholder="Optional - defaults to 115200" />
                </div>

                <div class="form-group" id="form-group-buttons">
                    <button type="submit" class="btn btn-primary form-control">Run</button>
                    <br />
                    <div style="font-size: 0.85em; font-style: italic; padding-top: 8px; text-align: center">This may cause the wheelchair firmware to restart,<br />do not use while in motion.</div>
                </div>
            </form>
        </div>
        <div class="col-md-4">
            <h3 id="header-logs">Log Files</h3>

            <ul class="list-group" id="list-log-files-empty">
                <!--  This is the only list item by default -->
                <li class="list-group-item">No Log Files</li>
            </ul>
            <ul class="list-group" id="list-log-files">
                <!--  This row is hidden by default. It is just a template used to convert a javascript log object into HTML -->
                <li class="list-group-item" id="listitem-log-file-template">
                    <div class="btn-group pull-right">
                        <button type="button" class="btn btn-default btn-download" title="Download Log File">
                            <span class="glyphicon glyphicon-download-alt"></span>
                            <span class="sr-only">Download Log File</span>
                        </button>
                        <button type="button" class="btn btn-default btn-trash" title="Delete Log File">
                            <span class="glyphicon glyphicon-trash"></span>
                            <span class="sr-only">Delete Log File</span>
                        </button>
                    </div>
                    <div class="filename">FILENAME</div>
                    <div style="font-size: 0.8em"><span class="age"><time datetime="00000000"></time></span> | <span class="filesize">FILESIZE</span></div>
                </li>
            </ul>

        </div>
    </div>

</div><!-- /.container -->

<!-- Log File Deletion Dialog -->
<div class="modal fade" id="dialog-delete-log" tabindex="-1" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Confirm Delete</h4>
            </div>
            <div class="modal-body">Are you sure you want to delete the log file:<br /><strong class="filename"></strong></div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-danger">Delete Log File</button>
            </div>
        </div>
    </div>
</div>
<!-- Task Submission Confirmation Dialog -->
<div class="modal fade" id="dialog-task-confirmation" tabindex="-1" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Confirm Action</h4>
            </div>
            <div class="modal-body">Are you sure?</div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-danger">Yes</button>
            </div>
        </div>
    </div>
</div>


<!-- Bootstrap core JavaScript
================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
<script src="js//jquery-1.11.1.min.js"></script>
<script src="js/bootstrap.min.js"></script>
<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
<script src="js/ie10-viewport-bug-workaround.js"></script>

<script type="text/javascript" src="js/bootstrap-select.min.js"></script>
<script type="text/javascript" src="js/jquery.timeago.js"></script>

<!-- Load a JSON array of all the existing log files into variable named logFiles -->
<script type="text/javascript" src="log_files.php?action=list"></script>

<script type="text/javascript">

    tasks = <?php echo json_encode($tasks); ?>;


    function refreshLogFileList(){

        $.get("log_files.php?action=list&format=json", function(response){

            if(response.status == "okay"){
                // Get the new list of log files
                logFiles = response.log_files;
                // Output the list again
                outputLogFileList();
            } else {
                // Getting the log files failed for some reason - refresh the page
                window.location.reload(true);
            }

        }, "json");
    }

    function outputLogFileList(){
        // Fill the log file list
        if(logFiles.length > 0){
            // If there are some log files, hide the "empty" list and show the main list before filling it up
            $('#list-log-files-empty').hide();
            $('#list-log-files').show();

            // Empty the logFile list
            $('#list-log-files').find('li:gt(0)').remove();

            var logFile;
            var logFileList;
            var logElement;
            var logElementTemplate;

            logFileList = $('#list-log-files');
            logElementTemplate = $('#listitem-log-file-template');
            logElementTemplate.show();

            for(var x in logFiles){
                logFile = logFiles[x];
                logElement = logElementTemplate.clone();

                // Set the correct parameters
                logElement.find('.filename').html(logFile.filename);
                logElement.find('time').attr("datetime", new Date(logFile.timestamp * 1000).toISOString());
                logElement.find('.filesize').html(formatFilesize(logFile.filesize));

                logFileList.append(logElement);
            }

            // Make sure the template list item is not visible
            logElementTemplate.hide();
        } else {
            // If there are no log files, hide the log file list
            $('#list-log-files').hide();
        }
    }

    outputLogFileList();

    $('.selectpicker').selectpicker();

    $('#list-log-files').on("click", "button", function(src){

        var btn = $(this);

        if(btn.hasClass("btn-trash")){
            $('#dialog-delete-log .filename').text(getLogFilename(this));
            $('#dialog-delete-log .btn-danger').text("Delete Log File");
            $('#dialog-delete-log').modal();
        } else if(btn.hasClass("btn-download")){
            window.location.href = 'log_files.php?action=download&filename=' + getUrlSafeLogFilename(this);
        }
    });
    
    $('#dialog-delete-log .btn-danger').click(function(){

        $(this).text("Deleting Log File...");
        $.get('log_files.php', {action : 'delete', filename : $('#dialog-delete-log .filename').text()}, function(response){

            // Hide the dialog box
            $('#dialog-delete-log').modal('hide');

            // If deleting failed, show an alert
            if(response.status == 'error'){
                alert(response.message);
            } else {
                // If delete was successful, refresh the log file list
                refreshLogFileList();
            }
        }, 'json');

    })

    $('time').timeago();

    function getLogFilename(element){

        return $(element).closest("li").find(".filename").text();
    }

    function getUrlSafeLogFilename(element){

        return encodeURIComponent(getLogFilename(element));
    }

    $('#task').change(displayCorrectControlFields);

    function displayCorrectControlFields(){

        // Get the task definition for the selected task
        var task = getTask();

        // Hide all form groups in the control form
        $('#control-form .form-group').hide();

        //Show the task group, submission button and then any other required groups
        $('#form-group-task').show();
        $('#form-group-buttons').show();

        var field;
        for(var x in task.fields){
            field = task.fields[x]
            $('#form-group-' + field).show();
        }
    }

    $('#control-form').submit(function(){

        // Check whether or not there is already a process running - if so we should cancel the existing task
        if(processRunning === true){
            $('#form-group-buttons .btn-primary').text("Stopping Process...");
            // Send the command to stop the process - assume it was sent successfully
            $.get("process_control.php?action=stop");
        } else {
            // Check which task we're submitting
            var task = getTask();

            // ...and whether or not it needs confirmation
            if(task.confirmation){
                // Set default values for confirmation message and button text
                var confirmationMessage = "Are you sure?";
                var confirmationButtonText = "Yes";
                // If it does need confirmation, show a dialog
                if(task.confirmation.message){
                    confirmationMessage = task.confirmation.message;
                }
                if(task.confirmation.buttonText){
                    confirmationButtonText = task.confirmation.buttonText;
                }
                // Display the comfirmation message and button text
                $('#dialog-task-confirmation .modal-body').html(confirmationMessage);
                $('#dialog-task-confirmation .btn-danger').html(task.confirmation.buttonText);

                $('#dialog-task-confirmation').modal();
            } else {
                // If we don't need confirmation, submit the task
                submitTask();
            }
        }

        // Prevent the form submitting by normal means, otherwise the page will reload
        return false;
    });

    $('#dialog-task-confirmation .btn-danger').click(function(){
        // Close the confirmation window
        $('#dialog-task-confirmation').modal('hide');

        submitTask();
    })

    function getTask(){

        var id = $('#task').val();
        var task = tasks[id];
        task.id = id;

        return task;
    }

    function submitTask(){

        var task = getTask();
        var args = {
            action : 'start',
            task : task.id
        };

        var field;
        for(var x in task.fields){
            field = task.fields[x]
            args[field] = $('#' + field).val();
        }

        $.get('process_control.php', args);
    }

    function setControlFormAppearance(processRunning){

        if(processRunning){
            $('.form-group').hide();
            $('#form-group-buttons').show();
            $('#form-group-buttons .btn-primary').text("Stop Process").addClass("btn-danger").blur();
        } else {
            displayCorrectControlFields();
            $('#form-group-buttons .btn-primary').text("Run").removeClass("btn-danger");
        }
    }

    function onProcessComplete(response){

        endCurrentProcessTimer(response.terminated);
        setControlFormAppearance(false);
        // Indicate that the currently running process is complete
        processRunning = false;

        // Check for a new log file
        refreshLogFileList();

        // Start a listener for any other processes to start
        listenForProcessStart();
    }

    function onProcessStart(response){

        // Start the timer with the correct start time
        startNewProcessTimer(response.process_status.start_time * 1000);
        // Display the form correctly
        setControlFormAppearance(true);

        // Start a listener for the completion of the current task
        listenForProcessComplete();
    }

    function listenForProcessStart(){

        // Start a listener, waiting for a process to start
        $.get("process_control.php?action=await-start", onProcessStart, "json");
    }

    function listenForProcessComplete(){

        // Launch a listener to wait for the process to complete
        $.get("process_control.php?action=await-completion", onProcessComplete, "json");
    }

    function formatFilesize(filesize, round){

        // round is an optional parameter - default value is 1
        if(typeof(round) === 'undefined'){round = 1};

        var suffixes = ["B", "KB", "MB", "GB"];
        var suffixIndex = 0;

            while(filesize >= 1024){
                filesize /= 1024;
            suffixIndex++;
        }

        return Math.round(filesize, round) + ' ' + suffixes[suffixIndex];
    }

    var processRunning = false;
    var currentProcessStartTime;
    var currentProcessTimer;

    function startNewProcessTimer(startTime){

        processRunning = true;

        $('.navbar-info').show();

        if(startTime == null){
            currentProcessStartTime = new Date().getTime();
        } else {
            currentProcessStartTime = startTime;
        }

        currentProcessTimer = setInterval(function(){updateCurrentProcessTime();}, 300);

        updateCurrentProcessTime();
    }

    function endCurrentProcessTimer(forceStopped){

        processRunning = false;
        currentProcessStartTime = null;
        clearInterval(currentProcessTimer);

        if(forceStopped){
            var navbarText = "Process Stopped";
        } else {
            var navbarText = "Process Complete";
        }
        $('.navbar-info .navbar-brand').text(navbarText);
    }

    function updateCurrentProcessTime(){

        var now = new Date().getTime();
        var diff = ((now - currentProcessStartTime) / 1000);

        // Display the difference as mins:secs (mm:ss format)
        var mins = Math.floor(diff / 60);
        var secs = Math.floor(diff - (mins * 60));
        var diffDisplay = mins.pad(2) + ":" + secs.pad(2);

        $('.navbar-info .navbar-brand').text("Process Running: " + diffDisplay);

    }

    Number.prototype.pad = function(size) {
        var s = String(this);
        while (s.length < (size || 2)) {s = "0" + s;}
        return s;
    }

    // Check to see if a process is already running
    $.get("process_control.php?action=status", function(response){

        // If so,
        if(response.process_status.running){
            // Start a process timer
            onProcessStart(response);
        } else {
            // And if not, launch a listener that will return when a new process begins
            listenForProcessStart();
        }

    }, "json");

    displayCorrectControlFields();

</script>
</body>
</html>
