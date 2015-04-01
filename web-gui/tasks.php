<?php

/**
 * Created by PhpStorm.
 * User: Richard
 * Date: 15/01/15
 * Time: 16:13
 */

include("directories.php");

$gui_jar_path = "ping 192.168.0.1 -n 10";
$gui_jar_path = "dir /s D: &";
//$gui_jar_path = $directory_home . "sysiass-wheelchair-gui";

// An array containing all available tasks - this method ensures that arbitrary commands cannot be sent (either accidentally or maliciously)
$tasks = array(
    "monitor"=>array(
        "text"=>"Monitor Joystick",
        "command"=>$gui_jar_path . " -m MONITOR_JOYSTICK",
        "command"=>$gui_jar_path,
        "fields"=>array("port", "baudrate")
    ),
    "shutdown"=>array(
        "text"=>"Shutdown Raspberry Pi",
        "command"=>"sudo shutdown -h now",
        "confirmation"=>array(
            "message"=>"You are about to shutdown the Raspberry Pi",
            "buttonText"=>"Okay, shut it down"
        ),
    )
);