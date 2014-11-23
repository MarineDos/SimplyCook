package simplycook.marinedos.com.simplycook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marine on 25/10/2014.
 */
public class ConnectFragment extends Fragment{
    // Elements
    private LoginButton authButton;
    private View mLoginContent;
    private View mLoginLoader;
    private  View mPopupView;
    private AlertDialog mDialog;
    private LogInTask mLoginTask;

    private static final String TAG = "ConnectFragment";
    private UiLifecycleHelper uiHelper;
    private Button mLogin_btn, mNew_account_btn;
    private final Firebase ref = new Firebase("https://simplycook.firebaseio.com");
    private final Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.connect, container, false);

        // Facebook authentification button
        authButton = (LoginButton) view.findViewById(R.id.authButton);
        authButton.setFragment(this);
        authButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        mLoginContent = view.findViewById(R.id.login_content);
        mLoginLoader = view.findViewById(R.id.login_loader);

        // Hide loader, show content
        Anim.hide(getActivity(), mLoginLoader);

        // Normal login button
        mLogin_btn = (Button) view.findViewById(R.id.btn_login);
        mLogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // Display a login popup
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();

                mPopupView = inflater.inflate(R.layout.login_dialog, null);

                builder.setView(mPopupView)
                        .setMessage(R.string.messageLoginPopup)
                        .setTitle(R.string.titleLoginPopup)
                        .setPositiveButton(R.string.positiveLoginPopup, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Anim.show(getActivity(), mLoginLoader);
                                Anim.hide(getActivity(), mLoginContent);

                                // Launch log in task
                                mLoginTask = new LogInTask();
                                mLoginTask.execute();
                            }
                        })
                        .setNegativeButton(R.string.negativeLoginPopup, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                mDialog = builder.create();
                mDialog.show();

                // Prevent positive button to dismiss popup
                /*Button theButton = mDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                theButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });*/

            }
        });

        // New account button
        mNew_account_btn = (Button) view.findViewById(R.id.btn_create_account);
        mNew_account_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change activity to "create an account"
                Intent intent = new Intent(getActivity(), CreateAccountActivity.class);
                startActivity(intent);
            }
        });

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

            // Request user data
            Request.newMeRequest(session, new Request.GraphUserCallback() {

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

                                                boolean exists = (snapshot.getValue() != null);
                                                if(!exists){
                                                    // Create user
                                                    Map<String, String> newUser = new HashMap<String, String>();
                                                    newUser.put("firstName", user.getFirstName());
                                                    newUser.put("lastName", user.getLastName());
                                                    newUser.put("email", user.getProperty("email").toString());

                                                    Firebase newRef = ref.child("/users/").push();
                                                    newRef.setValue(newUser, id);

                                                    // Change activity to home page
                                                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                                                    startActivity(intent);
                                                }else{
                                                    System.out.println("User already exists : " + id);

                                                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                                                    startActivity(intent);
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
            }).executeAsync();
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
        if(mLoginTask != null){
            mLoginTask.cancel(true);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    class LogInTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            // Log in the user

            EditText email_input = (EditText) mPopupView.findViewById(R.id.identifiant);
            String email = email_input.getText().toString();
            EditText password_input = (EditText) mPopupView.findViewById(R.id.password);
            String password = password_input.getText().toString();

            ref.authWithPassword(email, password, new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    // Change activity to home page
                    Anim.show(getActivity(), mLoginContent);
                    Anim.hide(getActivity(), mLoginLoader);
                    mDialog.cancel();
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    Anim.show(getActivity(), mLoginContent);
                    Anim.hide(getActivity(), mLoginLoader);

                    Context context = getActivity();
                    System.out.println("Firebase error : " + firebaseError.getCode());
                    int codeError = firebaseError.getCode();
                    String message = "";
                    switch(codeError){
                        case -16:
                            message = context.getString(R.string.errorMessage_incorrectPassword);
                    }
                    Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
                    toast.show();

                }
            });

            return null;
        }
    }

}
