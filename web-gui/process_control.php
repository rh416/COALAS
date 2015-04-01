<?php

define("PIPE_INDEX_STDIN", 0);
define("PIPE_INDEX_STDOUT", 1);
define("PIPE_INDEX_STDERR", 2);

include("tasks.php");
include("functions.php");

header("Content-Type: text/plain");

// Store path to wheelchair interface
$process_control_filepath = "process_control";
$process_input_filepath = "process_input";
$process_output_filepath = "process_output";
$process_status_filepath = "process_status";

// Get request variables
$action = get("action");
$port = get("port");
$baud_rate = get("baud_rate");
$task = get("task");

// Create the default reponse
$response = array(
    "status"=>"error",
    "message"=>""
);

function read_pipe($handle){

    $handle_info = fstat($handle);

    if($handle_info["size"] > 0){
        return fread($handle, $handle_info["size"]);
    } else {
        return "";
    }
}

if($action == "start"){

    $process_status = array(
        "running"=>false,
        "duration"=>0
    );

    $process_start_time = microtime(true);

    $process_is_running = true;
    $process_terminated = false;

    // Get the desired command from the available options
    $process_command = $tasks[$task]["command"];

    // Add any additional arguments if they have been supplied
    if($port && $port != ""){
        $process_command .= " -p " . $port;
        // If a port has been given, save it to a cookie to it's remembered for next time
        $expireTime = time() + 60 * 60 * 24 * 150; // Make the cookie expire in 150 days
        setcookie(COOKIE__LAST_SERIAL_PORT_USED, $port, $expireTime);
    }
    if ($baud_rate && $baud_rate != ""){
        $process_command .= " -b " . $baud_rate;
    }

    // Create an array of pipes to be used for IO to the launched application
    $descriptorspec = array(
        PIPE_INDEX_STDIN => array("pipe", "r"),     // stdin is a pipe that the child will read from
        PIPE_INDEX_STDOUT => array("pipe", "w"),    // stdout is a pipe that the child will write to
        PIPE_INDEX_STDERR => array("pipe", "w")     // stderr is a pipe that the child will write to
    );

    // Start the process
    $process_handle = proc_open($process_command, $descriptorspec, $pipes);

    // Create an output buffer
    $buffer = array(
        "stdout"=>"",
        "stderr"=>""
    );

    // Clear previous output
    if(file_exists($process_output_filepath)){
        unlink($process_output_filepath);
    }

    // Keep this script alive until either the process finishes or we need it to stop
    while($process_is_running){
        // Reset the max execution timeout
        set_time_limit(10);

        // Read any available data from the process - need to look at this more closely, check whether there is actually data
        $buffer_stdout = read_pipe($pipes[PIPE_INDEX_STDOUT]);
        $buffer_stderr = read_pipe($pipes[PIPE_INDEX_STDERR]);

        // Add the data to the overall buffer
        $buffer["stdout"] .= $buffer_stdout;
        $buffer["stderr"] .= $buffer_stderr;

        // If any new output data is available, append it to the output file
        if($buffer_stdout != "" || $buffer_stderr != ""){
            // Get the existing data, or create an empty array if no data exists
            if(file_exists($process_output_filepath)){
                $process_output = json_decode(file_get_contents($process_output_filepath), true);
            } else {
                $process_output = array("stdout"=>"", "stderr"=>"");
            }
            // Append the new data
            $process_output["stdout"] .= $buffer_stdout;
            $process_output["stderr"] .= $buffer_stderr;

            // Write this data back out to the file
            file_put_contents($process_output_filepath, json_encode($process_output));
        }

        // Check whether the process is still running, quit the loop if not
        $status = proc_get_status($process_handle);
        if($status["running"] === false){
            $process_is_running = false;
        }

        // Read the process_control file to check whether we need to stop the process
        // Check that it exists first though
        if(file_exists($process_control_filepath)){
            $process_action = json_decode(file_get_contents($process_control_filepath), true);

            // If we need to stop the process, close all associated pipes, then the process and then quit the loop
            if(isset($process_action["action"]) && $process_action["action"] == "stop"){
                proc_terminate($process_handle);
                $process_terminated = true;
            }

            // Delete the control file
            unlink($process_control_filepath);
        }

        // Check whether an input file exists, and if so send the input to the process
        if(file_exists($process_input_filepath)){
            $process_input = json_decode(file_get_contents($process_input_filepath), true);

            // Write the desired data in
            fwrite($pipes[PIPE_INDEX_STDIN], $process_input["stdin"]);

            // Delete the file
            unlink($process_input_filepath);
        }

        // Collect some info about the process
        $process_status["running"] = $status["running"];
        $process_status["start_time"] = $process_start_time;
        $process_status["duration"] = microtime(true) - $process_start_time;
        $process_status["terminated"] = $process_terminated;
        // Output that information to file
        file_put_contents($process_status_filepath, json_encode($process_status));

        // Pause the execution of this script for 100ms - will not affect the running of the process,
        //      it just reduces load on the webserver
        usleep(100000);
    }

    fclose($pipes[PIPE_INDEX_STDIN]);
    fclose($pipes[PIPE_INDEX_STDOUT]);
    fclose($pipes[PIPE_INDEX_STDERR]);

    proc_close($process_handle);

    $response["status"] = "okay";

} else if ($action == "stop"){

    // We can't directly stop the process, so we will write a file that the processing loop above checks regularly
    $process_action = array("action"=>"stop");

    // Output the desired action
    if(file_put_contents($process_control_filepath, json_encode($process_action)) !== false){
        $response["status"] = "okay";
        $response["message"] = "Process control file created - process should stop soon";
    } else {
        $response["message"] = "Could not create process control file";
    }
} else if($action == "status"){
    // Default process status
    $response["process_status"] = array(
        "running"=>false
    );

    $response["status"] = "okay";

    // If the status file exists, read it
    if(file_exists($process_status_filepath)){
        $response["process_status"] = json_decode(file_get_contents($process_status_filepath), true);
    }
} else if($action == "await-completion"){
    // Loop indefinitely
    while(true){
        // Read the process status file if it exists
        if(file_exists($process_status_filepath)){
            $status = json_decode(file_get_contents($process_status_filepath), true);

            // If the process isn't running, set the response and break out of the loop
            if($status["running"] != true){
                $response["status"] = "okay";
                $response["process_complete"] = true;
                $response["terminated"] = $status["terminated"];
                // Break out of the loop
                break;
            }
        }
        // Reset the max execution timeout
        set_time_limit(10);
        // Pause for 500ms before the next iteration
        usleep(500000);
    }
} else if($action == "await-start"){
    // Loop indefinitely
    while(true){
        // Read the process status file if it exists (if it doesn't exist no process can be running, so jump straight to the delay)
        if(file_exists($process_status_filepath)){
            $status = json_decode(file_get_contents($process_status_filepath), true);

            // If the process is running, set the response and break out of the loop
            if($status["running"] == true){
                $response["status"] = "okay";
                $response["process_status"] = $status;
                // Break out of the loop
                break;
            }
        }
        // Reset the max execution timeout
        set_time_limit(10);
        // Pause for 500ms before the next iteration
        usleep(500000);
    }
} else if($action == "read" || $action == "peek"){

    $output = array("stdout"=>"", "stderr"=>"");

    $response["status"] = "okay";

    if(file_exists($process_output_filepath)){
        $output = json_decode(file_get_contents($process_output_filepath), true);

        if($action == "read"){
            unlink($process_output_filepath);
        }
    }

    $response["output"] = $output;
}

echo json_encode($response);