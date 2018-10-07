package com.dendriticspine.denhikage2;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements ActionBar.TabListener {

	ToggleButton brSelAll;
	ToggleButton brSelS1;
	ToggleButton brSelS2;
	ToggleButton brSelS3;
	ToggleButton lrSelAll;
	ToggleButton lrSelS1;
	ToggleButton lrSelS2;
	ToggleButton lrSelS3;
	ToggleButton lrSelS45;
	String targetChannel = "0";
	String targetCommand = "x";
	String fullCommand;
	String webAddress = "http://x.x.x.x/";
	
    // provides fragments for each section, keeps all fragments in memory 
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;		// ViewPager hosts the section contents

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up action bar
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // adapter that returns fragment for each primary section of the activity
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // set up ViewPager w/the sections adapter
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // when swiping between sections, select corresponding tab
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // for each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // create a tab with text corresponding to the page title 
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handles action bar item clicks 
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // when given tab is selected, switch to corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    // returns a fragment corresponding to one of the sections/tabs/pages.
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // instantiates the fragment for the given page
            switch (position) {
            case 0:
                return LivingRoomFragment.newInstance(0);
            case 1:
                return BedroomFragment.newInstance(1);
            default:
            	return null;
            }
        }

        @Override
        public int getCount() {    
            return 2; // show 2 pages
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.lr).toUpperCase(l);
                case 1:
                    return getString(R.string.br).toUpperCase(l);
            }
            return null;
        }
    }

    public static class BedroomFragment extends Fragment {
        // represents the section number for this fragment
        private static final String ARG_SECTION_NUMBER = "2";

        // returns a new instance of this fragment for the given section number
        public static BedroomFragment newInstance(int sectionNumber) {
            BedroomFragment fragment = new BedroomFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public BedroomFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
    
    public static class LivingRoomFragment extends Fragment {
    	// represents the section number for this fragment
        private static final String ARG_SECTION_NUMBER = "1";

        // returns a new instance of this fragment for the given section number
        public static LivingRoomFragment newInstance(int sectionNumber) {
            LivingRoomFragment fragment = new LivingRoomFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public LivingRoomFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_lr, container, false);
            return rootView;
        }
    }
    
    private class SendDenhikageCommand extends AsyncTask<URL, Void, String> {
    	protected String doInBackground(URL... urls) {
    		try {
				HttpURLConnection urlc = (HttpURLConnection) urls[0].openConnection();
				urlc.setRequestProperty("Connection", "close");
				urlc.setConnectTimeout(1000 * 30); // mTimeout is in seconds
				urlc.connect();
			    if (urlc.getResponseCode() == 200) {
			        return null;
			    }
			} catch (MalformedURLException e1) {
			    e1.printStackTrace();
			} catch (IOException e) {
			    e.printStackTrace();
			}
			return null;
    	}
    	protected void onPostExecute(String result) {
    	}
 	}
    
    public void brSelectAll(View view) {
        brSelS1 = (ToggleButton) findViewById(R.id.brs1);
        brSelS2 = (ToggleButton) findViewById(R.id.brs2);
        brSelS3	= (ToggleButton) findViewById(R.id.brs3); 
    	brSelS1.setChecked(false);
    	brSelS2.setChecked(false);
    	brSelS3.setChecked(false);
    	targetChannel = "1";
    }
    
    public void brSelectS1(View view) {
    	brSelAll = (ToggleButton) findViewById(R.id.brall);
    	brSelS2 = (ToggleButton) findViewById(R.id.brs2);
    	brSelS3	= (ToggleButton) findViewById(R.id.brs3); 
    	brSelAll.setChecked(false);
    	brSelS2.setChecked(false);
    	brSelS3.setChecked(false);
    	targetChannel = "2";
    }

    public void brSelectS2(View view) {
    	brSelAll = (ToggleButton) findViewById(R.id.brall);
    	brSelS1 = (ToggleButton) findViewById(R.id.brs1);
    	brSelS3	= (ToggleButton) findViewById(R.id.brs3); 
    	brSelAll.setChecked(false);
    	brSelS1.setChecked(false);
    	brSelS3.setChecked(false);
    	targetChannel = "3";
    }

    public void brSelectS3(View view) {
    	brSelAll = (ToggleButton) findViewById(R.id.brall);
    	brSelS1 = (ToggleButton) findViewById(R.id.brs1);
    	brSelS2 = (ToggleButton) findViewById(R.id.brs2);
    	brSelAll.setChecked(false);
    	brSelS1.setChecked(false);
    	brSelS2.setChecked(false);
    	targetChannel = "4";
    }
    
    public void brCommandUp(View view) throws MalformedURLException {
    	targetCommand = "u";
    	fullCommand = webAddress + "c" + targetChannel + targetCommand;
    	URL address = new URL(fullCommand);
    	new SendDenhikageCommand().execute(address);
    }
    
    public void brCommandDown(View view) throws MalformedURLException {
    	targetCommand = "d";
    	fullCommand = webAddress + "c" + targetChannel + targetCommand;
    	URL address = new URL(fullCommand);
    	new SendDenhikageCommand().execute(address);
    }

    public void brCommandStop(View view) throws MalformedURLException {
    	targetCommand = "s";
    	fullCommand = webAddress + "c" + targetChannel + targetCommand;
    	URL address = new URL(fullCommand);
    	new SendDenhikageCommand().execute(address);
    }

    public void lrSelectAll(View view) {
        lrSelS1 = (ToggleButton) findViewById(R.id.lrs1);
        lrSelS2 = (ToggleButton) findViewById(R.id.lrs2);
        lrSelS3	= (ToggleButton) findViewById(R.id.lrs3);
        lrSelS45 = (ToggleButton) findViewById(R.id.lrs45); 
    	lrSelS1.setChecked(false);
    	lrSelS2.setChecked(false);
    	lrSelS3.setChecked(false);
    	lrSelS45.setChecked(false);
    	targetChannel = "1";
    }
    
    public void lrSelectS1(View view) {
    	lrSelAll = (ToggleButton) findViewById(R.id.lrall);
    	lrSelS2 = (ToggleButton) findViewById(R.id.lrs2);
    	lrSelS3	= (ToggleButton) findViewById(R.id.lrs3); 
    	lrSelS45 = (ToggleButton) findViewById(R.id.lrs45);
    	lrSelAll.setChecked(false);
    	lrSelS2.setChecked(false);
    	lrSelS3.setChecked(false);
    	lrSelS45.setChecked(false);
    	targetChannel = "2";
    }

    public void lrSelectS2(View view) {
    	lrSelAll = (ToggleButton) findViewById(R.id.lrall);
    	lrSelS1 = (ToggleButton) findViewById(R.id.lrs1);
    	lrSelS3	= (ToggleButton) findViewById(R.id.lrs3); 
    	lrSelS45 = (ToggleButton) findViewById(R.id.lrs45);
    	lrSelAll.setChecked(false);
    	lrSelS1.setChecked(false);
    	lrSelS3.setChecked(false);
    	lrSelS45.setChecked(false);
    	targetChannel = "3";
    }

    public void lrSelectS3(View view) {
    	lrSelAll = (ToggleButton) findViewById(R.id.lrall);
    	lrSelS1 = (ToggleButton) findViewById(R.id.lrs1);
    	lrSelS2 = (ToggleButton) findViewById(R.id.lrs2);
    	lrSelS45 = (ToggleButton) findViewById(R.id.lrs45);
    	lrSelAll.setChecked(false);
    	lrSelS1.setChecked(false);
    	lrSelS2.setChecked(false);
    	lrSelS45.setChecked(false);
    	targetChannel = "4";
    }
    
    public void lrSelectS45(View view) {
    	lrSelAll = (ToggleButton) findViewById(R.id.lrall);
    	lrSelS1 = (ToggleButton) findViewById(R.id.lrs1);
    	lrSelS2 = (ToggleButton) findViewById(R.id.lrs2);
    	lrSelS3	= (ToggleButton) findViewById(R.id.lrs3); 
    	lrSelAll.setChecked(false);
    	lrSelS1.setChecked(false);
    	lrSelS2.setChecked(false);
    	lrSelS3.setChecked(false);
    	targetChannel = "5";
    }
    
    public void lrCommandUp(View view) throws MalformedURLException {
//    	targetCommand = "u";
//    	fullCommand = webAddress + "c" + targetChannel + targetCommand;
//    	URL address = new URL(fullCommand);
//    	new SendDenhikageCommand().execute(address);
    }
    
    public void lrCommandDown(View view) throws MalformedURLException {
//    	targetCommand = "d";
//    	fullCommand = webAddress + "c" + targetChannel + targetCommand;
//    	URL address = new URL(fullCommand);
//    	new SendDenhikageCommand().execute(address);
    }

    public void lrCommandStop(View view) throws MalformedURLException {
//    	targetCommand = "s";
//    	fullCommand = webAddress + "c" + targetChannel + targetCommand;
//    	URL address = new URL(fullCommand);
//    	new SendDenhikageCommand().execute(address);
    }
}
