package simplycook.marinedos.com.simplycook;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import simplycook.marinedos.com.simplycook.Utils.ConnexionManager;


public class ComparaisonAddFromSearch extends ActionBarActivity {
    public int index;

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

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            index = extras.getInt("index");
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
