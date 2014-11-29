package simplycook.marinedos.com.simplycook;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.client.Firebase;

import simplycook.marinedos.com.simplycook.Utils.ConnexionManager;


public class HomeActivity extends ActionBarActivity {

    private final Firebase ref = new Firebase("https://simplycook.firebaseio.com");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        if(savedInstanceState == null) {
            // Create fragment
            Fragment fragment = new HomeFragment();

            // Load fragment in the Activity
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.home_activity, fragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

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
