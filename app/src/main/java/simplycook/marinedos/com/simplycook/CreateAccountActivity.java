package simplycook.marinedos.com.simplycook;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;


/** @brief	The create account activity. Manage the account creation. */
public class CreateAccountActivity extends ActionBarActivity {

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
        setContentView(R.layout.create_account_activity);
        if(savedInstanceState == null) {
            // Create fragment
            Fragment fragment = new CreateAccountFragment();

            // Load fragment in the Activity
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.create_account_activity, fragment)
                    .commit();
        }
    }
}
