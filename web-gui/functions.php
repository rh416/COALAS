<?php

define("COOKIE__LAST_SERIAL_PORT_USED", "LastSerialPortUsed");

function get($var, $default = null){

    if(isset($_GET[$var])){
        return $_GET[$var];
    } else {
        return $default;
    }
}

function post($var, $default = null){

    if(isset($_POST[$var])){
        return $_POST[$var];
    } else {
        return $default;
    }
}

function outputSerialPortList(){

    $lastUsedPort = $_COOKIE[COOKIE__LAST_SERIAL_PORT_USED];

    $serialPorts = array();

    // If on Windows, just list COM# from 1 to 20
    if(isOnWindows()){
        for($i = 0; $i < 20; $i++){
            $serialPorts[] = "COM" . ($i + 1);
        }
    } else {

    }

    foreach($serialPorts as $port){
        $selectedStr = "";
        if($port == $lastUsedPort){
            $selectedStr = " selected=\"true\"";
        }
        echo "<option" . $selectedStr . ">" . $port . "</option>\n";
    }
}

function isOnWindows(){
    return !(strtoupper(substr(PHP_OS, 0, 3)) === 'WIN');
}