package simplycook.marinedos.com.simplycook;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import simplycook.marinedos.com.simplycook.Utils.ConnexionManager;


public class ComparaisonChoiceActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comparaison_choice_activity);
        if (savedInstanceState == null) {
            Fragment fragment = new ComparaisonChoiceFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ComparaisonChoiceFragment fragment = (ComparaisonChoiceFragment)getSupportFragmentManager().findFragmentById(R.id.container);
        fragment.updateList();

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
