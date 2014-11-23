package simplycook.marinedos.com.simplycook;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.firebase.client.Firebase;

public class ConnectActivity extends FragmentActivity {
    private ConnectFragment connectFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init Firebase
        Firebase.setAndroidContext(this);

        if (savedInstanceState == null) {
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
