/**
 * Created by Richard on 10/04/15.
 */

var tasks = {
    "monitor" : {
        "text" : "Monitor Joystick",
        "command" : "monitor-joystick",
        "fields" : [
            "port",
            "baudrate"
        ]
    },
    "shutdown" : {
        "text" : "Shutdown Raspberry Pi",
        "command" : "/rpi/shutdown",
        "confirmation" : {
            "message" : "You are about to shutdown the Raspberry Pi",
            "buttonText" : "Okay, shut it down"
        }
    }
}