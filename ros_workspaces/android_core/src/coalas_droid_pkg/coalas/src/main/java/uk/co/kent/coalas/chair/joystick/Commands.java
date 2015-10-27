package uk.co.kent.coalas.chair.joystick;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import uk.co.kent.coalas.R;
import uk.co.kent.coalas.chair.ChairState;
import uk.co.kent.coalas.main.io.CoalasIO;
//import static uk.kent.coalas.ukcoalas.main.io.CoalasIO.write2Socket;

/**
 * Created by coalas-kent on 07/05/15.
 */
public enum Commands implements CompoundButton.OnCheckedChangeListener {

    SYSTEM("System ", "*%02x#", ChairState.Options.SYSTEM, true, false),
    //    DATA("Sensor data ", "%02x#", true, false),
    //    ENCODERS("Encoder data ", "%02x#", true, false),
    COLLISION("Collision avoidance ", "*%02x#", ChairState.Options.COLL_AVOID, true, false),
    REMOTE("Remote control ", "*%02x#", ChairState.Options.REMOTE, true, false),
    DEBUG("Debug mode ", "*%02x#", ChairState.Options.DEBUG, true, false),
    STRAIGHT_LINE("Move straight (cm) ", "M%d#", null, true, true),
    TURN("Turn at angle (deg\u00B0) ", "T%d#", null, true, true),
    POTENT_FIELDS("Potential Fields ", "F%02x#", null, false, false),;

    private final String name;
    private final String format;
    private final boolean onCmdView;
    private final boolean isInput;
    private static final ChairState chairState = ChairState.getInstance();
    private final ChairState.Options opt;

    Commands(String name, String format, ChairState.Options opt, boolean onCmdView, boolean input) {
        this.name = name;
        this.format = format;
        this.onCmdView = onCmdView;
        this.isInput = input;
        this.opt = opt;
    }

    public void send(boolean toggle) {
        if (opt == null)
            return;
        chairState.set(opt, toggle, ChairState.Platforms.ANDROID_APP);
        String str = String.format(format, chairState.getState());
        CoalasIO.getInstance().write2Socket(str);
    }

    public void send(int input) {
        CoalasIO.getInstance().write2Socket(String.format(format, input));
    }

    public static void populateView(View commands) {
        LinearLayout vertical_lay = (LinearLayout) commands.findViewById(R.id.commands_layout);
        Context context = commands.getContext();

        for (final Commands cmd : values()) {
            // only add control if command should be on the Commands view
            if (!cmd.onCmdView)
                continue;
            LinearLayout horizontal_lay = new LinearLayout(context);
            TextView tv = new TextView(context);
            tv.setText(cmd.name);
            horizontal_lay.addView(tv);
            if (!cmd.isInput) {
                ToggleButton toggle = new ToggleButton(context);
                toggle.setOnCheckedChangeListener(cmd);
                horizontal_lay.addView(toggle);
                vertical_lay.addView(horizontal_lay);
            } else {
                FrameLayout frame = new FrameLayout(context);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                frame.setLayoutParams(layoutParams);
                TextView hack = new TextView(context);
                hack.setText("00000000");
                hack.setTextAppearance(context, R.style.TextAppearance_AppCompat_Medium);
                hack.setVisibility(View.INVISIBLE);
                final EditText text = new EditText(context);
                text.setInputType(InputType.TYPE_CLASS_NUMBER);
                text.setText("0");
                text.setGravity(Gravity.CENTER);
                text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                text.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                frame.addView(hack);
                frame.addView(text);
                Button button = new Button(context);
                button.setText(R.string._send);
                button.setLayoutParams(layoutParams);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String inputStr = text.getText().toString();
                        inputStr.trim();
                        if (!inputStr.isEmpty()) {
                            cmd.send(Integer.parseInt(inputStr));
                        }
                    }
                });
                horizontal_lay.addView(frame);
                horizontal_lay.addView(button);
                vertical_lay.addView(horizontal_lay);
            }

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        send(isChecked);
    }
}
