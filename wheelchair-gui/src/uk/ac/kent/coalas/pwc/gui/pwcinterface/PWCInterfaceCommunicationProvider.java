package uk.ac.kent.coalas.pwc.gui.pwcinterface;

import jssc.SerialPortException;

/**
 * Created by rm538 on 07/08/2014.
 */
public interface PWCInterfaceCommunicationProvider {

    public boolean isAvailable();
    public void setPWCInterface(PWCInterface pwcInterface);
    public void write(String output);
    public void connect(String port) throws SerialPortException;
    public void connect(String port, int baud) throws SerialPortException;
    public void disconnect();
}
