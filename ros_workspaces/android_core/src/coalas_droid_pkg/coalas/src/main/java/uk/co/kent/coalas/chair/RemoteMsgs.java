package uk.co.kent.coalas.chair;

import java.util.regex.Pattern;

import uk.co.kent.coalas.main.io.CoalasIO;

/**
 * Created by coalas-kent on 01/04/15.
 */
public enum RemoteMsgs {

    JOYSTICK(":\\p{XDigit}{2}\\p{XDigit}{2}\\s?", ":%02x%02x#", 2) {
        protected void update(String message) {
            message = message.replaceAll("\n", "");
            message = message.substring(2);
            String[] strData = message.split(",");
            data[0] = Integer.parseInt(strData[0], 16);
            data[1] = Integer.parseInt(strData[1], 16);
            super.update(null);
        }
    },
    DIRECTIVE("\\*\\p{XDigit}{2}#\\s?", ":%02x#", 2) {
        protected void update(String message) {
            message = message.replaceAll("\n", "");
            message = message.substring(1,3);
            String[] strData = message.split(",");
            data[0] = Integer.parseInt(strData[0], 16);
            super.update(null);
        }
    },


//    RIGHT_ENCODER("ER:\\p{XDigit}{4}\\s?", "ER:%d#", 1) {
//        protected void update(String message) {
//            message = message.replaceAll("\n", "");
//            data[0] = Integer.parseInt(message.substring(3), 16);
//            super.update(null);
//        }
//    },
//
//    LEFT_ENCODER("EL:\\p{XDigit}{4}\\s?", "EL:%d#", 1) {
//        protected void update(String message) {
//            message = message.replaceAll("\n", "");
//            data[0] = Integer.parseInt(message.substring(3), 16);
//            super.update(null);
//        }
//    },

    FRONT_LEFT_PF("PFL:\\p{XDigit}{2}\\s?", "F00:%02x#", 1) {
    },
    FRONT_RIGHT_PF("PFR:\\p{XDigit}{2}\\s?", "F01:%02x#", 1) {

    },
    LEFT_PF("PL:\\p{XDigit}{2}\\s?", "F10:%02x#", 1) {

    },
    RIGHT_PF("PR:\\p{XDigit}{2}\\s?", "F11:%02x#", 1) {

    };

    public boolean hasNewData;
    protected int[] data;

    private Pattern pattern;
    private String format;

    RemoteMsgs(String patternStr, String format, int valNo) {
        if (valNo > 0) {
            this.pattern = Pattern.compile(patternStr);
            this.format = format;
            data = new int[valNo];
        }
    }

    /**
     * @param message - the string to compare
     * @return - true if message has matched an existing pattern, false
     * otherwise
     */
    public static boolean match(String message) {
        for (RemoteMsgs messagePatt : values())
            if (messagePatt.pattern.matcher(message).matches()) {
                messagePatt.update(message);
                return true;
            }
        return false;
    }

    public int[] getData() {
        hasNewData = false;
        return data;
    }

    protected void update(String message) {
        hasNewData = true;
    }

    public void sendData(Integer... data) {
        CoalasIO.getInstance().write2Socket(String.format(format, data));
    }

}

