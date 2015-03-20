package simplycook.marinedos.com.simplycook;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import simplycook.marinedos.com.simplycook.Utils.ConnexionManager;


/** @brief	The activity that manage the AddFromSearchFragment. */
public class ComparaisonAddFromSearch extends ActionBarActivity {

    /** @brief	The index of the button where you want to add the user for comparaison. */
    public int index;

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
        setContentView(R.layout.comparaison_add_from_search_activity);
        if (savedInstanceState == null) {
            Fragment fragment = new ComparaisonAddFromSearchFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }

		// Get the index of the button from the bundle passed when we create this activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            index = extras.getInt("index");
        }
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
