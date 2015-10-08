package uk.co.kent.coalas.chair;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import uk.co.kent.coalas.R;
import uk.co.kent.coalas.chair.joystick.Commands;

/**
 * Created by coalas-kent on 08/04/15.
 */
public class PotentialFields implements SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {

    public enum PotentialFieldParams {
        FRONT_LEFT_FIELD(R.id.front_left_field_bar, R.id.front_left_txt, RemoteMsgs.FRONT_LEFT_PF) {

        },
        FRONT_RIGHT_FIELD(R.id.front_right_field_bar, R.id.front_right_text, RemoteMsgs.FRONT_RIGHT_PF) {

        }, LEFT_FIELD(R.id.left_side_field_bar, R.id.left_side_text, RemoteMsgs.LEFT_PF) {

        }, RIGHT_FIELD(R.id.right_side_field_bar, R.id.right_side_text, RemoteMsgs.RIGHT_PF) {

        };
        private final int barID;
        private final int textID;
        private int progress;
        private static boolean areActive = true;
        private RemoteMsgs message;

        PotentialFieldParams(int barID, int textID, RemoteMsgs message) {
            this.barID = barID;
            this.textID = textID;
            this.message = message;
            progress = 0;
        }

        public int getProgress() {
            return progress;
        }

        public static boolean areActive() {
            return areActive;
        }

        public static void setActive(boolean val) {
            areActive = val;
            Commands.POTENT_FIELDS.send(areActive);
        }

        public void setProgress(int progress) {
            this.progress = progress;
            message.sendData(progress);
        }

        public static int setProgress(int seekBarID, int value) {
            for (PotentialFieldParams param : values()) {
                if (seekBarID == param.barID) {
                    param.setProgress(value);

                    return param.textID;
                }
            }
            return -1;
        }
    }

    private View parent;

    public PotentialFields(View parent) {
        this.parent = parent;
        CheckBox check = (CheckBox) parent.findViewById(R.id.pontential_field_toggle);
        check.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton button, boolean isChecked) {
        PotentialFieldParams.setActive(isChecked);
        for (PotentialFieldParams param : PotentialFieldParams.values()) {
            parent.findViewById(param.barID).setEnabled(isChecked);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int textID = PotentialFieldParams.setProgress(seekBar.getId(), progress);
        TextView txt = (TextView) parent.findViewById(textID);
        txt.setText(progress + "%");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
