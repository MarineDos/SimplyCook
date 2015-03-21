package simplycook.marinedos.com.simplycook;

import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import simplycook.marinedos.com.simplycook.Utils.ConnexionManager;
import simplycook.marinedos.com.simplycook.Utils.DeviceInformation;


/** @brief	The comparaison activity. Manage the comparaison fragment. */
public class ComparaisonActivity extends ActionBarActivity {

    /**
     * @brief	Function called when activity is first created. Initialize the activity, create the
     * 			fragment.
     *
     * @param	savedInstanceState	If the activity is being re-initialized after previously being
     * 								shut down then this Bundle contains the data it most recently
     * 								supplied in onSaveInstanceState(Bundle). Note: Otherwise it is
     * 								null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comparaison_activity);
        if (savedInstanceState == null) {
            Fragment fragment = new ComparaisonFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }

        getActionBar().setDisplayHomeAsUpEnabled(true);
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
