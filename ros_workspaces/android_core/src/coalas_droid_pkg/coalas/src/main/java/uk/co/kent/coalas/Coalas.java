package uk.co.kent.coalas;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import org.ros.android.NodeMainExecutorService;

import java.util.Locale;

import uk.co.kent.coalas.chair.PotentialFields;
import uk.co.kent.coalas.chair.joystick.Commands;
import uk.co.kent.coalas.main.JoystickView;
import uk.co.kent.coalas.main.io.CoalasIO;
import uk.co.kent.coalas.main.io.SettingsDialogue;

public class Coalas extends ActionBarActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    public static ToastHandler toastHandler;

    public static final String PREFERENCES = "CoalasPreferences";
    public static SharedPreferences settings;

    public static class ToastHandler extends Handler {

        private static Context appContext;
        private static Toast toaster;

        ToastHandler(Context context) {
            appContext = context;
            toaster = Toast.makeText(appContext, "", Toast.LENGTH_SHORT);
        }

        public void toast(final String message) {
            toastHandler.post(new Runnable() {
                public void run() {
                    toaster.setText(message);
                    toaster.show();
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        settings = getSharedPreferences(PREFERENCES, 0);
        toastHandler = new ToastHandler(getApplicationContext());
//        CoalasIO.getInstance(getApplicationContext()).connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CoalasIO.getInstance().connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CoalasIO.getInstance().disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // Create and show the dialog.
            SettingsDialogue newFragment = SettingsDialogue.newInstance(
                    getApplicationContext());
            newFragment.show(getFragmentManager(), "Settings");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
                case 3:
                    return getString(R.string.title_section4).toUpperCase(l);
                case 4:
                    return getString(R.string.title_section5).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            switch ((int) getArguments().get(ARG_SECTION_NUMBER)) {
                case 1:
                    View rootView = inflater.inflate(R.layout.joystick_view, container, false);
                    JoystickView joystick = new JoystickView(rootView.getContext());
                    CheckBox joyToggle = (CheckBox) rootView.findViewById(R.id.joystick_toggle_straight);
                    joyToggle.setOnCheckedChangeListener(joystick);
                    LinearLayout joystickCanvas = (LinearLayout) rootView.findViewById(R.id.joystick_canvas_view);
                    joystickCanvas.addView(joystick);
                    return rootView;
                case 2:
                    View view = inflater.inflate(R.layout.potential_field_view, container, false);
                    PotentialFields potentialFieldListener = new PotentialFields(view);
                    SeekBar seekBar = (SeekBar) view.findViewById(R.id.front_left_field_bar);
                    seekBar.setOnSeekBarChangeListener(potentialFieldListener);
                    seekBar = (SeekBar) view.findViewById(R.id.front_right_field_bar);
                    seekBar.setOnSeekBarChangeListener(potentialFieldListener);
                    seekBar = (SeekBar) view.findViewById(R.id.left_side_field_bar);
                    seekBar.setOnSeekBarChangeListener(potentialFieldListener);
                    seekBar = (SeekBar) view.findViewById(R.id.right_side_field_bar);
                    seekBar.setOnSeekBarChangeListener(potentialFieldListener);
                    return view;
                case 3:
                    View commands = inflater.inflate(R.layout.commands_view, container, false);
                    Commands.populateView(commands);
                    return commands;
                case 4:
                    return inflater.inflate(R.layout.navigation_view, container, false);
                case 5:
                    return inflater.inflate(R.layout.data_feedback, container, false);
                default:
                    return null;
            }
        }
    }

    /**
     * Called when the activity is first created.
     */
}
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
//    }

//    public Coalas() {
//        // The RosActivity constructor configures the notification title and ticker
//        // messages.
////        super("Pubsub Tutorial", "Pubsub Tutorial");
//        NodeMainExecutorService service = new NodeMainExecutorService();
//        service.startMaster(false);
//        service.execute(new CoalasAndroidNode(), null);
//
//    }

//    @Override
//    protected void init(NodeMainExecutor nodeMainExecutor) {
//        CoalasAndroidNode droidNode = new CoalasAndroidNode();
//
//        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(
//                InetAddressFactory.newNonLoopback().getHostAddress());
//        nodeConfiguration.setMasterUri(getMasterUri());
//
//        nodeMainExecutor.execute(droidNode, nodeConfiguration);
//
//
//    }
//}
