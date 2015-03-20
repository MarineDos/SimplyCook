package simplycook.marinedos.com.simplycook;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.firebase.client.Firebase;

/** @brief	The activity that manage user log in and connection. */
public class ConnectActivity extends FragmentActivity {

    /** @brief	The connect fragment. */
    private ConnectFragment connectFragment;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init Firebase
        Firebase.setAndroidContext(this);

        if ( savedInstanceState == null ) {
            // Add the fragment on initial activity setup
            connectFragment = new ConnectFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, connectFragment)
                    .commit();
        } else {
            // Or set the fragment from restored state info
            connectFragment = (ConnectFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);
        }
    }
}
