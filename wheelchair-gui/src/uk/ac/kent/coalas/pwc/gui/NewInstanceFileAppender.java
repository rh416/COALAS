package uk.ac.kent.coalas.pwc.gui;

import org.apache.log4j.FileAppender;
import org.apache.log4j.RollingFileAppender;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Richard on 11/11/2014.
 */
public class NewInstanceFileAppender extends RollingFileAppender {

    @Override
    public void setFile(String filename){

        filename = System.getProperty("logfile.name");

        // If the property logfile.name has not been set, generate a filename
        if(filename == null){

            // Set log file name
            Date d = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HHmmss");

            // Store a link to the logs folder
            File logFolder = new File("/.logs");

            // Ensure that the logs folder exists - if not, create it
            if(!Files.isDirectory(logFolder.toPath())){
                try {
                    Files.createDirectory(logFolder.toPath());
                } catch (IOException err){
                    err.printStackTrace();
                }
            }



            filename = String.format("./logs/%s.log", format.format(d));
        }

        super.setFile(filename);
    }
}
