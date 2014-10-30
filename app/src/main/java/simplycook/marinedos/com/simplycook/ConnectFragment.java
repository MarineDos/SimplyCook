package simplycook.marinedos.com.simplycook;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marine on 25/10/2014.
 */
public class ConnectFragment extends Fragment{
    // Elements
    private LoginButton authButton;

    private static final String TAG = "ConnectFragment";
    private UiLifecycleHelper uiHelper;
    private final Firebase ref = new Firebase("https://simplycook.firebaseio.com");
    private final Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.connect, container, false);

        authButton = (LoginButton) view.findViewById(R.id.authButton);
        authButton.setFragment(this);
        authButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

     // Facebook login in / out
    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {

        if (state.isOpened()) {
            // Logged in
            Log.i(TAG, "Logged in...");
            //authButton.setVisibility(View.INVISIBLE);

            // Request user data
            Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

                @Override
                public void onCompleted(final GraphUser user, final Response response) {
                    if (user != null) {

                        // Connect with Firebase
                        ref.authWithOAuthToken("facebook", session.getAccessToken(), new Firebase.AuthResultHandler() {
                            @Override
                            public void onAuthenticated(AuthData authData) {
                                // The Facebook user is now authenticated with Firebase
                                Log.i(TAG, "Logged with Firebase");

                                // Check if user is in database
                                final String id = (String)(authData.getProviderData().get("id"));
                                ref.child("/users/")
                                        .startAt(id)
                                        .endAt(id)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        System.out.println(snapshot.getValue());

                                        boolean exists = (snapshot.getValue() != null);
                                        if(!exists){
                                            System.out.println("Unexisting user : " + id);
                                            // Create user in Firebase
                                            System.out.println("1st : " + user.getFirstName());
                                            System.out.println("last : " + user.getLastName());
                                            System.out.println("last : " + user.getProperty("email"));

                                            Map<String, String> newUser = new HashMap<String, String>();
                                            newUser.put("firstName", user.getFirstName());
                                            newUser.put("lastName", user.getLastName());
                                            newUser.put("email", user.getProperty("email").toString());

                                            Firebase newRef = ref.child("/users/").push();
                                            newRef.setValue(newUser, id);


                                        }else{
                                            System.out.println("User already exists : " + id);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {
                                    }
                                });
                            }

                            @Override
                            public void onAuthenticationError(FirebaseError firebaseError) {
                                // There was an error
                                Log.i(TAG, "Logging with Firebase make an error");
                            }
                        });
                    }
                }
            });
        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");

            // Unlog from Firebase
            ref.unauth();
        }
    }

    // For Facebook Login
    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();

        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed()) ) {
            onSessionStateChange(session, session.getState(), null);
        }

        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

}
