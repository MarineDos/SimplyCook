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

/**
 * Created by Marine on 25/10/2014.
 */
public class ConnectFragment extends Fragment{
    // Elements
    private TextView userInfoTextView;
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
        authButton.setReadPermissions(Arrays.asList("user_location", "user_birthday", "user_likes", "user_status"));

        userInfoTextView = (TextView) view.findViewById(R.id.userInfoTextView);

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
            userInfoTextView.setVisibility(View.VISIBLE);
            //authButton.setVisibility(View.INVISIBLE);

            // Request user data
            Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

                @Override
                public void onCompleted(final GraphUser user, Response response) {
                    if (user != null) {
                        // Display the parsed user info
                        userInfoTextView.setText(buildUserInfoDisplay(user));

                        // Connect with Firebase
                        ref.authWithOAuthToken("facebook", session.getAccessToken(), new Firebase.AuthResultHandler() {
                            @Override
                            public void onAuthenticated(AuthData authData) {
                                // The Facebook user is now authenticated with Firebase
                                Log.i(TAG, "Logged with Firebase");

                                // Check if user is in database
                                String url = "/users/" + authData.getProviderData().get("id");
                                ref.child(url).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        boolean exists = (snapshot.getValue() != null);
                                        if(!exists){
                                            System.out.println("Unexisting user");
                                            // Create user in Firebase

                                        }else{
                                            System.out.println("User already exists");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {
                                    }
                                });
                                System.out.println("Token : " + authData.getToken());
                                System.out.println("Uid : " + authData.getUid());
                                System.out.println("ProviderData : " + authData.getProviderData().get("id"));
                                System.out.println("Name : " + authData.getProviderData().get("displayName"));
                                System.out.println("Birthday : " + user.getBirthday());
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
            userInfoTextView.setVisibility(View.INVISIBLE);

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

    // Fetch Facebook data
    private String buildUserInfoDisplay(GraphUser user) {
        StringBuilder userInfo = new StringBuilder("");

        // Example: typed access (name)
        // - no special permissions required
        userInfo.append(String.format("Name: %s\n\n",
                user.getName()));

        // Example: typed access (birthday)
        // - requires user_birthday permission
        userInfo.append(String.format("Birthday: %s\n\n",
                user.getBirthday()));

        // Example: partially typed access, to location field,
        // name key (location)
        // - requires user_location permission
        userInfo.append(String.format("Location: %s\n\n",
                user.getLocation().getProperty("name")));

        // Example: access via property name (locale)
        // - no special permissions required
        userInfo.append(String.format("Locale: %s\n\n",
                user.getProperty("locale")));

        // Example: access via key for array (languages)
        // - requires user_likes permission
        JSONArray languages = (JSONArray)user.getProperty("languages");
        if (languages.length() > 0) {
            ArrayList<String> languageNames = new ArrayList<String> ();
            for (int i=0; i < languages.length(); i++) {
                JSONObject language = languages.optJSONObject(i);
                // Add the language name to a list. Use JSON
                // methods to get access to the name field.
                languageNames.add(language.optString("name"));
            }
            userInfo.append(String.format("Languages: %s\n\n",
                    languageNames.toString()));
        }

        return userInfo.toString();
    }
}
