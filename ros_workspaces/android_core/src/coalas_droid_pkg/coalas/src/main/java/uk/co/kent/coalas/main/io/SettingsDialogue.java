package uk.co.kent.coalas.main.io;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import uk.co.kent.coalas.R;
import uk.co.kent.coalas.Coalas;

/**
 * Created by coalas-kent on 14/04/15.
 */
public class SettingsDialogue extends DialogFragment {
    private static SettingsDialogue INSTANCE;
    private static Context context;

    /**
     * Create a new instance
     */
    public static SettingsDialogue newInstance(Context context) {
        SettingsDialogue.context = context;
        INSTANCE = new SettingsDialogue();
        return INSTANCE;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sett_view, container, false);
        final Button okButton = (Button) v.findViewById(R.id.ipOk_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // initiate new connection
                IPFields.commitFields();
                CoalasIO.getInstance().reconnect();
                dismiss();
            }
        });
        Button cancelButton = (Button) v.findViewById(R.id.ipCancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        IPFields.initFields(v);
        return v;
    }

    public enum IPFields implements TextWatcher, View.OnFocusChangeListener {
        IP_FIELD_1(R.id.ip_address_field1, 192),
        IP_FIELD_2(R.id.ip_address_field2, 168),
        IP_FIELD_3(R.id.ip_address_field3, 0),
        IP_FIELD_4(R.id.ip_address_field4, 1);

        private static IPFields[] vals = values();
        private final int ID;
        private final String defaultVal;
        private static final int size = values().length - 1;
        EditText ip_field;

        IPFields(int iD, int defaultVal) {
            ID = iD;
            this.defaultVal = defaultVal + "";
        }

        public IPFields next() {
            return vals[(this.ordinal() + 1) % vals.length];
        }

        public static String getIP() {
            // flag to check if IP was loaded from settings
            boolean hasIP = true;
            StringBuilder sb = new StringBuilder(15);
            for (IPFields field : values()) {
                if (sb.length() > 0)
                    sb.append('.');
                String val = Coalas.settings.getString(field.ID + "", "-");
                hasIP &= !val.equals("-");
                sb.append(hasIP? val : field.defaultVal);
            }
            String ip = sb.toString();
            if (!hasIP)
                Coalas.toastHandler.toast("No IP in history. /n Default to: " + ip);
            return ip;
        }

        static void commitFields() {
            SharedPreferences.Editor p = Coalas.settings.edit();
            for (IPFields field : values()) {
                p.putString(field.ID + "", field.ip_field.getText().toString());
            }
            p.commit();
        }

        static void initFields(View view) {
            for (IPFields field : values()) {
                field.ip_field = (EditText) view.findViewById(field.ID);
                String val = Coalas.settings.getString(field.ID + "", "-");
                field.ip_field.setText(val.equals("-") ? field.defaultVal : val);
                field.ip_field.addTextChangedListener(field);
                field.ip_field.setOnFocusChangeListener(field);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String str = s.toString();
            int length = str.length();
            if (str.equals(defaultVal))
                return;
            switch (length) {
                case 0:
                    return;
                case 1:
                    if (ID == R.id.ip_address_field1 && str.equals("0")) {
                        Coalas.toastHandler.toast("IP can't start with 0");
                        ip_field.setText(defaultVal);
                        ip_field.requestFocus();
                        return;
                    }
                    break;
                case 2:
                    if (str.startsWith("0")) {
                        s.replace(0, 1, "");
                    }
                    break;
                case 3:
                    if (Integer.valueOf(str) > 255) {
                        Coalas.toastHandler.toast("Value exceeds 255");
                        ip_field.setText("255");
                        ip_field.requestFocus();
                        return;
                    }
                    if (ID != R.id.ip_address_field4) {
                        next().ip_field.requestFocus();
                    }
                    break;
            }
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus && ip_field.length() == 0) {
                ip_field.setText(defaultVal);
            }
        }
    }
}
