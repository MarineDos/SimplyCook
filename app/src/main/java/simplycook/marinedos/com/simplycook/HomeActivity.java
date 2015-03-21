package simplycook.marinedos.com.simplycook;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import simplycook.marinedos.com.simplycook.Utils.Service.SuggestNotificationService;


public class HomeActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        if(savedInstanceState == null) {
            // Create fragment
            Fragment fragment = new HomeFragment();
            fragment.setHasOptionsMenu(true);
            fragment.setMenuVisibility(true);

            // Load fragment in the Activity
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.home_activity, fragment)
                    .commit();
        }
        Intent intent = new Intent(this, SuggestNotificationService.class);
        startService(intent);
    }


    /*@Override
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
    }*/
}
