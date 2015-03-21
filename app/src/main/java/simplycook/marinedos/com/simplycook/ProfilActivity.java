package simplycook.marinedos.com.simplycook;


import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import simplycook.marinedos.com.simplycook.Utils.ConnexionManager;
import simplycook.marinedos.com.simplycook.Utils.tabsSwipeUserProfil.TabsPagerAdapter;

/** @brief	A profil activity. */
public class ProfilActivity extends FragmentActivity implements ActionBar.TabListener {
	
    /** @brief	The ViewPager. */
    private ViewPager viewPager;
    /** @brief	The adapter for the ViewPager. */
    private TabsPagerAdapter mAdapter;
    /** @brief	The action bar. */
    private ActionBar actionBar;
    /** @brief	Identifier for the user in firebase. */
    public String userFirebaseId;
	
	/**
     * @brief	Function called when activity is first created. 
     * 			Initialize the activity, create the fragment.
     *
     * @param	savedInstanceState	If the activity is being re-initialized after previously being
     * 								shut down then this Bundle contains the data it most recently
     * 								supplied in onSaveInstanceState(Bundle). Note: Otherwise it is
     * 								null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil_activity);
        String[] tabs = { getResources().getString(R.string.title_histastes), getResources().getString(R.string.title_suggest_tastes) };
        // Get user from the bundle passed in the intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
			// Fill the userFirebaseId's member
            userFirebaseId = extras.getString("firebaseId");
            System.out.println(userFirebaseId);
        }

        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        //actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }

        /* on swiping the viewpager make respective tab selected*/
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    /**
     * @brief	Initialize the contents of the Activity's standard options menu. You should place
     * 			your menu items in to menu. This is only called once, the first time the options menu
     * 			is displayed.
     *
     * @param	menu	The options menu in which you place your items.
     *
     * @return	true for the menu to be displayed, false otherwise.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

	/**
     * @brief	This hook is called whenever an item in your options menu is selected.
     *
     * @param	item	The menu item that was selected.
     *
     * @return	false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_disconnect) {
            ConnexionManager.disconnect(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
