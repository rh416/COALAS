<?php

include("directories.php");
include("functions.php");

// Get request variables
$filename = get("filename");
$action = get("action");
$format = get("format");

// Listing all the log files doesn't require a lot of whats below. Just return after we've done it
if($action == 'list'){
    $log_files = array();

    // Look in the current directory for any *.log files and list them in JSON format
    foreach(new DirectoryIterator($directory_logs) as $item){
        // Ignore anything that isn't a file
        if ($item->isFile()){
            // Check whether the file is *.log file
            if(pathinfo($item->getFilename(), PATHINFO_EXTENSION) == "log"){
                $log_files[] = array(
                    "filename"=>$item->getFilename(),
                    "filesize"=>$item->getSize(),
                    "timestamp"=>$item->getMTime()
                );
            }
        }
    }

    // DirectoryIterator returns files in filename order. This is the opposite of what we want to display, so reverse the results
    $log_files = array_reverse($log_files);

    if($format == "json"){
        $response = array("chairStatus"=>"okay", "log_files"=>$log_files);
        echo json_encode($response);
    } else {
        // Output the list as a variable for inclusion via <script /> tag
        echo "var logFiles = " . json_encode($log_files) . ";";
    }

    return;
}

// Sanitise filename
$filename = basename($filename);
$filepath = $directory_logs . $filename;

// Build default response
$response = array(
    "chairStatus"=>"error",
    "message"=>"No action specified",
    "filename"=>$filename
);

if(file_exists($filepath)){
    if($action == "download"){
        header("Content-Description: File Transfer");
        header("Content-Type: application/octet-stream");
        header("Content-Disposition: attachment; filename=\"$filename\"");

        readfile ($filepath);
        return;
    } else if($action == "delete"){
        if(unlink($filepath)){
            $response["chairStatus"] = "okay";
            $response["message"] = "Log file deleted successfully";
        } else {
            $response["message"] = "Deleting the specified log file failed.";
        }
    }
} else {
    $response["message"] = "The specified log file doesn't exist";
}

echo json_encode($response);