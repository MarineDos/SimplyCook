package simplycook.marinedos.com.simplycook;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;


public class CreateAccountActivity extends ActionBarActivity {

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
