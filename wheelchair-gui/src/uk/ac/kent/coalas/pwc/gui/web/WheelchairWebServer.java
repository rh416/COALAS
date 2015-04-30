package uk.ac.kent.coalas.pwc.gui.web;

import fi.iki.elonen.SimpleWebServer;

import jssc.SerialPortException;
import jssc.SerialPortList;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import uk.ac.kent.coalas.pwc.gui.WheelchairGUI;
import uk.ac.kent.coalas.pwc.gui.hardware.LogFile;
import uk.ac.kent.coalas.pwc.gui.pwcinterface.PWCInterface;
import uk.ac.kent.coalas.pwc.gui.pwcinterface.PWCInterfaceEvent;
import uk.ac.kent.coalas.pwc.gui.pwcinterface.PWCInterfaceEvent.EventType;
import uk.ac.kent.coalas.pwc.gui.pwcinterface.PWCInterfaceListener;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.List;

/**
 * Created by Richard on 01/04/2015.
 */
public class WheelchairWebServer extends SimpleWebServer implements PWCInterfaceListener {

    private static WheelchairWebServer webServer;

    private static final String COMMAND_LIST_PORTS = "/list-ports";
    private static final String COMMAND_SERIAL_CONNECT = "/serial-connect";
    private static final String COMMAND_SERIAL_DISCONNECT = "/serial-disconnect";
    private static final String COMMAND_STATUS = "/status";
    private static final String COMMAND_STATUS_DETECT_CHANGES = "/status/detect-changes";
    private static final String COMMAND_LOGGING_START = "/logging-start";
    private static final String COMMAND_LOGGING_END = "/logging-end";
    private static final String COMMAND_LOGS_LIST = "/logging-list";
    private static final String COMMAND_LOGS_LIST_FORCE = "/logging-list/force";
    private static final String COMMAND_LOGGING_EVENT = "/logging-event";
    private static final String COMMAND_RPI_GET_TIME = "/rpi/get-time";
    private static final String COMMAND_RPI_SET_TIME = "/rpi/set-time";
    private static final String COMMAND_RPI_SHUTDOWN = "/rpi/shutdown";
    private static final String COMMAND_USERS_GET = "/users/get";
    private static final String COMMAND_USERS_SAVE = "/users/save";
    private static final String COMMAND_INTERFACE_POST_EVENT = "/interface/post-event";


    private static final String JSON_FIELD_STATUS = "status";
    private static final String JSON_FIELD_MESSAGE = "message";
    private static final String JSON_FIELD_IS_CONNECTED = "is_connected";
    private static final String JSON_FIELD_IS_LOGGING = "is_logging";
    private static final String JSON_FIELD_LOGGING_FILENAME = "logging_filename";
    private static final String JSON_FIELD_LOGGING_FILESIZE = "logging_filesize";
    private static final String JSON_FIELD_LOGGING_START_TIMESTAMP = "logging_start_timestamp";
    private static final String JSON_FIELD_PI_TIME = "time";
    private static final String JSON_FIELD_USERS = "users";
    private static final String JSON_FIELD_TIMEDOUT = "request_timed_out";

    private static final File USERS_DATA_FILE = new File("./www/users.json");

    private int port;
    private Logger logger;

    private List<WebServerResponseReceiver> receivers = Collections.synchronizedList(new ArrayList<WebServerResponseReceiver>());

    public static WheelchairWebServer getInstance(){

        if(webServer == null){
            ArrayList rootDirs = new ArrayList();
            rootDirs.add(new File("./www").getAbsoluteFile());

            webServer = new WheelchairWebServer(null, 2015, rootDirs, true);

            // Register to receive events from the chair interface
            WheelchairGUI.getChairInterface().registerListener(webServer);
        }

        return webServer;
    }

    public static void reportException(int port, Exception e){

        System.out.println("An exception occurred when connection to port: " + port);
        //e.printStackTrace();
    }

    public WheelchairWebServer(String host, int port, List<File> rootDirs, boolean quiet){

        super(host, port, rootDirs, quiet);

        this.port = port;
        logger = Logger.getLogger(this.getClass());
    }

    public int getPort(){

        return this.port;
    }

    protected String getStr(IHTTPSession session, String field, String defaultValue){

        String param = session.getParms().get(field);

        if(param == null || "".equals(param)){
            return defaultValue;
        } else {
            return param;
        }
    }

    protected String getStr(IHTTPSession session, String field){

        return getStr(session, field, null);
    }

    protected int getInt(IHTTPSession session, String field, int defaultValue){

        String rawValue = getStr(session, field);

        if(rawValue == null){
            return -1;
        } else {
            try {
                return Integer.parseInt(rawValue);
            } catch (NumberFormatException e){
                return -1;
            }
        }
    }

    protected int getInt(IHTTPSession session, String field){

        return getInt(session, field, -1);
    }

    protected PWCInterface chairInterface(){

        return WheelchairGUI.getChairInterface();
    }

    @Override
    public Response serve(IHTTPSession session) {

        // Detect if a special command URL has been visited. If not, handle the request using the SimpleWebServer implementation
        switch (session.getUri().toLowerCase()){

            case COMMAND_LIST_PORTS:
                return listSerialPorts(session);

            case COMMAND_SERIAL_CONNECT:
                return connectToSerialPort(session);

            case COMMAND_SERIAL_DISCONNECT:
                return disconnectFromSerialPort(session);

            case COMMAND_STATUS:
                return checkStatus(session);

            case COMMAND_STATUS_DETECT_CHANGES:
                return detectStatusChange(session);

            case COMMAND_LOGGING_START:
                return loggingStart(session);

            case COMMAND_LOGGING_END:
                return loggingEnd(session);

            case COMMAND_LOGS_LIST:
                return loggingList(session);

            case COMMAND_LOGS_LIST_FORCE:
                return loggingListRefresh(session);

            case COMMAND_LOGGING_EVENT:
                return loggingEvent(session);

            case COMMAND_RPI_GET_TIME:
                return RPiGetTime(session);

            case COMMAND_RPI_SET_TIME:
                return RPiSetTime(session);

            case COMMAND_RPI_SHUTDOWN:
                return RPiShutdown(session);

            case COMMAND_USERS_GET:
                return UsersGetList(session);

            case COMMAND_USERS_SAVE:
                return UsersSaveList(session);

            case COMMAND_INTERFACE_POST_EVENT:
                return InterfacePostEvent(session);

            default:
                return super.serve(session);
        }
    }

    private void errorResponse(JSONObject response, String errorMessage){

        errorResponse(response, errorMessage, null);
    }

    private void errorResponse(JSONObject response, String errorMessage, Throwable err){

        response.put(JSON_FIELD_STATUS, "error");
        response.put(JSON_FIELD_MESSAGE, errorMessage);

        if(err != null){
            JSONObject jsonError = new JSONObject();

            jsonError.put("message", err.getMessage());
            jsonError.put("type", err.getClass().getName());
            jsonError.put("stack", err.getStackTrace());

            response.put("error", jsonError);
        }
    }



    private Response listSerialPorts(IHTTPSession session){

        JSONObject response = new JSONObject();

        response.put(JSON_FIELD_STATUS, "okay");

        JSONArray ports = new JSONArray();
        String[] portList = SerialPortList.getPortNames();

        for(String port : portList){
            ports.put(port);
        }
        response.put("ports", ports);

        return new JsonResponse(response);
    }



    private Response connectToSerialPort(IHTTPSession session){

        JSONObject response = new JSONObject();

        String portName = getStr(session, "port");
        int baudRate = getInt(session, "baud_rate");

        try {
            if(baudRate == -1){
                WheelchairGUI.getInstance().getPWCConnection().connect(portName);
            } else {
                WheelchairGUI.getInstance().getPWCConnection().connect(portName, baudRate);
            }

            response.put(JSON_FIELD_STATUS, "okay")
                    .put(JSON_FIELD_IS_CONNECTED, chairInterface().isConnected());

        } catch (SerialPortException se){
            if("Port busy".equals(se.getExceptionType())){
                errorResponse(response, "Connection failed - port busy");
                response.put("port_busy", true);
            } else {
                errorResponse(response, "Connection failed", se);
            }
        }

        return new JsonResponse(response);
    }

    private Response disconnectFromSerialPort(IHTTPSession session){

        JSONObject response = new JSONObject();

        WheelchairGUI.getInstance().getPWCConnection().disconnect();
        response.put(JSON_FIELD_STATUS, "okay")
                .put(JSON_FIELD_IS_CONNECTED, chairInterface().isConnected());

        return new JsonResponse(response);
    }

    private Response checkStatus(IHTTPSession session) {

        JSONObject response = new JSONObject();

        response.put(JSON_FIELD_STATUS, "okay")
                .put(JSON_FIELD_IS_CONNECTED, chairInterface().isConnected())
                .put(JSON_FIELD_IS_LOGGING, chairInterface().isLogging())
                .put(JSON_FIELD_LOGGING_FILENAME, chairInterface().getCurrentLoggingFilename())
                .put(JSON_FIELD_LOGGING_START_TIMESTAMP, chairInterface().getCurrentLoggingStartTime());

        return new JsonResponse(response);
    }


    private Response detectStatusChange(IHTTPSession session){

        JSONObject response = new JSONObject();

        PWCInterface chairInterface = chairInterface();

        boolean oldConnected = chairInterface.isConnected();
        boolean oldIsLogging = chairInterface.isLogging();
        String oldLoggingFilename = chairInterface.getCurrentLoggingFilename();
        long oldLoggingStartTimestamp = chairInterface.getCurrentLoggingStartTime();

        // Set the output
        response.put(JSON_FIELD_STATUS, "okay");

        // Loop forever
        while(true){

            // Get the new values
            boolean newConnected = chairInterface.isConnected();
            boolean newIsLogging = chairInterface.isLogging();
            String newLoggingFilename = chairInterface.getCurrentLoggingFilename();
            long newLoggingStartTimestamp = chairInterface.getCurrentLoggingStartTime();

            // Record them in our response - this will only get returned if something has actually changed
            response.put(JSON_FIELD_IS_CONNECTED, newConnected);
            response.put(JSON_FIELD_IS_LOGGING, newIsLogging);
            response.put(JSON_FIELD_LOGGING_FILENAME, newLoggingFilename);
            response.put(JSON_FIELD_LOGGING_START_TIMESTAMP, newLoggingStartTimestamp);

            // Check if anything has changed. If so, return the current response
            if(oldConnected != newConnected){
                break;
            }
            if(oldIsLogging != newIsLogging){
                break;
            }
            if(newLoggingFilename != null && !newLoggingFilename.equals(oldLoggingFilename)){
                break;
            }
            if(oldLoggingStartTimestamp != newLoggingStartTimestamp){
                break;
            }

            // Sleep for 100ms to prevent overloading the system
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }

        return new JsonResponse(response);
    }




    private Response loggingStart(IHTTPSession session){

        String logFilename = getStr(session, "filename");

        if(logFilename == null){
            JSONObject response = new JSONObject();
            errorResponse(response, "No log filename supplied");
            return new JsonResponse(response);
        }

        chairInterface().startLogging(logFilename);

        return waitForResponse(EventType.ACK, new WaitForResponseHandler() {

            @Override
            public Response onResponse(WebServerResponseReceiver receiver) {

                JSONOutput.put(JSON_FIELD_STATUS, "okay")
                        .put(JSON_FIELD_MESSAGE, "Logging started successfully")
                        .put(JSON_FIELD_LOGGING_FILENAME, chairInterface().getCurrentLoggingFilename())
                        .put(JSON_FIELD_LOGGING_START_TIMESTAMP, chairInterface().getCurrentLoggingStartTime());

                return new JsonResponse(JSONOutput);
            }
        });
    }

    private Response loggingEnd(IHTTPSession session){

        chairInterface().endLogging();
        return logFileListWaitAndResponse();
    }

    private Response loggingList(IHTTPSession session){

        // If this is the first time the list has been requested, force a refresh
        if(!chairInterface().hasRequestedLogFileList()){
            return loggingListRefresh(session);
        } else {
            return logFileListResponse();
        }
    }

    private Response loggingListRefresh(IHTTPSession session){

        chairInterface().refreshLogFileList();
        return logFileListWaitAndResponse();
    }

    private Response loggingEvent(IHTTPSession session){

        String eventDescription = getStr(session, "description");

        if(eventDescription == null){
            JSONObject response = new JSONObject();
            response.put(JSON_FIELD_STATUS, "error")
                    .put(JSON_FIELD_MESSAGE, "No event description was given");
            return new JsonResponse(response);
        }

        chairInterface().logEvent(eventDescription);

        return waitForResponse(EventType.ACK, new WaitForResponseHandler() {

            @Override
            public Response onResponse(WebServerResponseReceiver receiver) {

                JSONOutput.put(JSON_FIELD_STATUS, "okay")
                        .put(JSON_FIELD_MESSAGE, "Event recorded successfully");
                return new JsonResponse(JSONOutput);

            }
        });
    }





    private Response RPiGetTime(IHTTPSession session){

        return RPiTimeResponse(false);
    }

    private Response RPiSetTime(IHTTPSession session){

        JSONObject response = new JSONObject();

        String newDateTimeStr = getStr(session, "newDateTime");

        if(newDateTimeStr != null) {
            // Send the command to the system
            try {
                // We don't want to actually run the command on a Windows based system
                //  so look to see if the OS starts with "Windows" - if not, run the command
                if (!System.getProperty("os.name").startsWith("Windows")) {
                    // Format to set time based on Unix timestamp (seconds): date +%s -s @123456789
                    Process setTimeProcess = new ProcessBuilder("date", "+%s", "-s", "@" + newDateTimeStr).redirectErrorStream(true).start();
                    InputStream is = setTimeProcess.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);

                    String setTimeResponse = "";
                    String line;

                    while((line = br.readLine()) != null){
                        setTimeResponse += line + "\n";
                    }

                    if(setTimeResponse.trim().equalsIgnoreCase(newDateTimeStr)) {
                        // Raspberry Pi time was set okay - now send it to the wheelchair (if it's connected)
                        if (chairInterface().isConnected()) {
                            chairInterface().setSystemTime();
                            waitForResponse(EventType.ACK, new WaitForResponseHandler() {

                                @Override
                                public Response onResponse(WebServerResponseReceiver receiver) {

                                    return RPiTimeResponse(true);
                                }
                            });
                        } else {
                            return RPiTimeResponse(true);
                        }
                    } else {
                        response.put(JSON_FIELD_STATUS, "error")
                                .put(JSON_FIELD_MESSAGE, setTimeResponse.trim());
                    }
                } else {
                    return RPiTimeResponse(true);
                }
            } catch (IOException e) {
                errorResponse(response, "Setting the time failed", e);
            }
        } else {
            errorResponse(response, "No date / time string was provided");
        }

        return new JsonResponse(response);
    }

    private Response RPiTimeResponse(boolean wasSet){

        JSONObject response = new JSONObject();

        // If the chair is not connected, just let the user know that we set the time okay
        response.put(JSON_FIELD_STATUS, "okay")
                .put(JSON_FIELD_PI_TIME, System.currentTimeMillis());

        if(wasSet){
            response.put(JSON_FIELD_MESSAGE, "Time set successfully");
        }

        return new JsonResponse(response);
    }

    private Response RPiShutdown(IHTTPSession session){

        JSONObject response = new JSONObject();

        // Send the command to the system
        try {
            Runtime.getRuntime().exec("sudo shutdown -h now");

            response.put(JSON_FIELD_STATUS, "okay");
            response.put(JSON_FIELD_MESSAGE, "Pi shutting down");
        } catch (IOException e){
            errorResponse(response, "Request to shutdown failed", e);
        }

        return new JsonResponse(response);
    }




    private Response UsersGetList(IHTTPSession session){

        JSONObject response = new JSONObject();
        response.put(JSON_FIELD_STATUS, "okay");

        // If the users data file exists, return it, otherwise return an empty array
        if(USERS_DATA_FILE.exists()){
            try {
                String usersStr = new String(Files.readAllBytes(USERS_DATA_FILE.toPath()));
                response.put(JSON_FIELD_USERS, new JSONArray(usersStr));
            } catch (IOException e){
                errorResponse(response, "Users file could not be opened", e);
            } catch (Exception ex){
                errorResponse(response, "An error occurred", ex);
            }
        } else {
            response.put(JSON_FIELD_USERS, new JSONArray());
        }
        return new JsonResponse(response);
    }


    private Response UsersSaveList(IHTTPSession session){

        JSONObject response = new JSONObject();

        String users = getStr(session, "users");
        if(users != null){
            response.put(JSON_FIELD_STATUS, "okay");
            try{
                Files.write(USERS_DATA_FILE.toPath(), users.getBytes());
            } catch (IOException e){
                errorResponse(response, "Unable to save users file", e);
            }
        } else {
            errorResponse(response, "No users supplied");
        }

        return new JsonResponse(response);
    }


    private Response InterfacePostEvent(IHTTPSession session){

        String eventData = getStr(session, "data");

        JSONObject response = new JSONObject();

        if(eventData != null){
            chairInterface().parse(eventData );
            response.put(JSON_FIELD_STATUS, "okay")
                    .put(JSON_FIELD_MESSAGE, "Event data received");

        } else {
            response.put(JSON_FIELD_STATUS, "error")
                    .put(JSON_FIELD_MESSAGE, "No event data supplied");
        }

        return new JsonResponse(response);
    }



    private Response logFileListWaitAndResponse(){

        return waitForResponse(EventType.ACK, new WaitForResponseHandler() {

            @Override
            public Response onResponse(WebServerResponseReceiver receiver) {

                return logFileListResponse();
            }
        });
    }


    private Response logFileListResponse(){

        JSONObject response = new JSONObject();

        response.put(JSON_FIELD_STATUS, "okay");

        JSONArray logList = new JSONArray();

        Map<String, LogFile> logFiles = chairInterface().getLogFiles();

        for (LogFile logFile : logFiles.values()) {
            JSONObject logFileJSON = new JSONObject();
            logFileJSON.put(JSON_FIELD_LOGGING_FILENAME, logFile.getFilename());
            logFileJSON.put(JSON_FIELD_LOGGING_FILESIZE, logFile.getSize());
            logList.put(logFileJSON);
        }

        response.put("log_files", logList);

        return new JsonResponse(response);
    }

    private Response waitForResponse(PWCInterfaceEvent.EventType eventType, WaitForResponseHandler handler){

        return waitForResponse(EnumSet.of(eventType), handler);
    }

    private Response waitForResponse(EnumSet<PWCInterfaceEvent.EventType> eventTypes, WaitForResponseHandler handler) {

        return waitForResponseAndListen(eventTypes, null, handler);
    }

    private Response waitForResponseAndListen(PWCInterfaceEvent.EventType waitEventType, PWCInterfaceEvent.EventType listenEventType, WaitForResponseHandler handler){

        return waitForResponseAndListen(EnumSet.of(waitEventType), EnumSet.of(listenEventType), handler);
    }

    private Response waitForResponseAndListen(EventType waitEventType, EnumSet<EventType> listenEventTypes, WaitForResponseHandler handler){

        return waitForResponseAndListen(waitEventType, listenEventTypes, handler);
    }

    private Response waitForResponseAndListen(EnumSet<PWCInterfaceEvent.EventType> waitEventTypes,
                                              EnumSet<PWCInterfaceEvent.EventType> listenEventTypes,
                                              WaitForResponseHandler handler){

        // Create new receiver
        WebServerResponseReceiver receiver = new WebServerResponseReceiver(waitEventTypes, listenEventTypes);
        // Register the receiver with the WebServer
        addReceiver(receiver);

        // Loop while waiting for a response
        while(true){
            // If the response is taking too long, remove the receiver and break out of the loop
            if(receiver.hasTimedOut()){
                removeReceiver();
                break;
            }

            // See if an event of the type desired has arrived - if so, remove the receiver, process the event and return the response
            if(receiver.hasFinalEventArrived()){
                removeReceiver();
                return handler.onResponse(receiver);
            }

            // Sleep the thread for 100ms to reduce load while maintaining responsiveness
            try{
                Thread.sleep(100);
            } catch (InterruptedException e){
                break;
            }
        }

        return handler.onTimeout();
    }

    private void addReceiver(WebServerResponseReceiver receiver){

        receivers.add(receiver);
    }

    private void removeReceiver(){

        // Synchronise receivers
        synchronized (receivers){
            // Iterate over the list of receivers
            Iterator<WebServerResponseReceiver> i = receivers.iterator();
            while(i.hasNext()) {
                // If this receiver belongs to this thread, remove it and then quit the loop
                if (i.next().belongsToCurrentThread()) {
                    i.remove();
                    break;
                }
            }
        }
    }

    public void onPWCInterfaceEvent(PWCInterfaceEvent event){

        logger.info("PWCInterfaceEvent received: " + event.getType());

        // Synchronise receivers
        synchronized (receivers){
            // Iterate over the list of receivers
            Iterator<WebServerResponseReceiver> i = receivers.iterator();
            while(i.hasNext()){
                WebServerResponseReceiver receiver = i.next();
                // Remove any requests that have timed out
                if(receiver.hasTimedOut()){
                    i.remove();
                    continue;
                }
                // Find the first receiver that is waiting for an event of this type.
                // If one is found, add it and quit the loop
                if(receiver.isWaitingForEventType(event.getType())){
                    logger.info("Receiver is waiting for this event type - attach it");
                    receiver.attachFinalEvent(event);
                    break;
                } else if(receiver.isListeningForEventType(event.getType())){
                    logger.info("Receiver is listening for this event type - attach it");
                    receiver.attachListenEvent(event);
                    break;
                }
            }
        }
    }

    public class WaitForResponseHandler {

        protected JSONObject JSONOutput = new JSONObject();

        public WaitForResponseHandler(){

            // Set the default response
            JSONOutput.put("status", "error");
        }

        public Response onResponse(WebServerResponseReceiver receiver){

            PWCInterfaceEvent response = receiver.getFinalEvent();

            // Build up the response
            JSONOutput.put(JSON_FIELD_STATUS, "okay");
            JSONOutput.put(JSON_FIELD_MESSAGE, "This is the default implementation - you should probably override this!");
            JSONOutput.put("stack_trace", Arrays.toString(Thread.currentThread().getStackTrace()));
            JSONOutput.put("response_type", response.getType().name());

            return new JsonResponse(JSONOutput);
        }

        public Response onTimeout(){

            JSONOutput.put(JSON_FIELD_MESSAGE, "Request timed out").put(JSON_FIELD_TIMEDOUT, true);

            return new JsonResponse(JSONOutput);
        }
    }

    public class JsonResponse extends Response{

        public JsonResponse(JSONObject response){

            super(response.toString());
        }
    }

    public class WebServerResponseReceiver {

        private long DEFAULT_TIMEOUT_MS = 15000;

        private long sourceThreadId;
        private long startTimestamp;
        private EnumSet<PWCInterfaceEvent.EventType> waitEventTypes;
        private EnumSet<PWCInterfaceEvent.EventType> listenEventTypes;
        private PWCInterfaceEvent finalEvent;
        private List<PWCInterfaceEvent> listenEvents = new ArrayList<PWCInterfaceEvent>();

        public WebServerResponseReceiver(EnumSet<PWCInterfaceEvent.EventType> waitEventTypes, EnumSet<PWCInterfaceEvent.EventType> listenEventTypes){

            this.sourceThreadId = getThreadId();
            this.waitEventTypes = waitEventTypes;
            this.listenEventTypes = listenEventTypes;
            this.startTimestamp = System.currentTimeMillis();
        }

        public void attachFinalEvent(PWCInterfaceEvent event){

            this.finalEvent = event;
        }

        public void attachListenEvent(PWCInterfaceEvent event){

            this.listenEvents.add(event);
        }

        public boolean belongsToCurrentThread(){

            return sourceThreadId == getThreadId();
        }

        public boolean hasFinalEventArrived(){

            return (sourceThreadId ==  getThreadId()) && (finalEvent != null);
        }

        public boolean hasTimedOut(){

            return hasTimedOut(DEFAULT_TIMEOUT_MS);
        }

        public boolean hasTimedOut(long timeout){

            return System.currentTimeMillis() - startTimestamp > timeout;
        }

        public PWCInterfaceEvent getFinalEvent(){

            return finalEvent;
        }

        public List<PWCInterfaceEvent> getListenEvents(){

            return listenEvents;
        }

        public boolean isWaitingForEventType(PWCInterfaceEvent.EventType eventType){

            return waitEventTypes.contains(eventType);
        }

        public boolean isListeningForEventType(PWCInterfaceEvent.EventType eventType){

            return (listenEventTypes != null) && (listenEventTypes.contains(eventType));
        }

        private long getThreadId(){

            return Thread.currentThread().getId();
        }

    }
}
