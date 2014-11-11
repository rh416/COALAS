package uk.ac.kent.coalas.pwc.gui;

import org.apache.log4j.FileAppender;
import org.apache.log4j.RollingFileAppender;

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

            filename = String.format("./%s.log", format.format(d));
        }

        super.setFile(filename);
    }
}
