package simplycook.marinedos.com.simplycook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Arrays;

import simplycook.marinedos.com.simplycook.Utils.Anim;
import simplycook.marinedos.com.simplycook.Utils.ConnexionManager;

public class ConnectFragment extends Fragment{
    // Elements
    private LoginButton authButton;
    private View mLoginContent;
    private View mLoginLoader;
    private View mPopupView;
    private AlertDialog mDialog;
    private LogInTask mLoginTask;

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

        // If user is already connect with Firebase, disconnect
        ConnexionManager.disconnect(getActivity());

        // Facebook authentification button
        authButton = (LoginButton) view.findViewById(R.id.authButton);
        authButton.setFragment(this);
        authButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        // Hide loader, show content
        mLoginContent = view.findViewById(R.id.login_content);
        mLoginLoader = view.findViewById(R.id.login_loader);
        Anim.hide(getActivity(), mLoginLoader);
        Anim.show(getActivity(), mLoginContent);

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
            Anim.hide(getActivity(), mLoginContent);
            Anim.show(getActivity(), mLoginLoader);
            ConnexionManager.connectFirebaseWithFacebook(session, getActivity());

        } else if (state.isClosed()) {
            ConnexionManager.disconnect(getActivity());
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
                    ConnexionManager.searchAndStroreUser();
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    Anim.show(getActivity(), mLoginContent);
                    Anim.hide(getActivity(), mLoginLoader);

                    Context context = getActivity();
                    System.out.println("Firebase error : " + firebaseError.getCode());
                    System.out.println("Firebase error : " + firebaseError.getMessage());
                    int codeError = firebaseError.getCode();
                    String message = "";
                    switch(codeError){
                        case -15:
                            message = context.getString(R.string.errorMessage_incorrectEmail); break;
                        case -16 :
                            message = context.getString(R.string.errorMessage_incorrectPassword); break;
                        case -17 :
                            message = context.getString(R.string.errorMessage_userDoesNotExist); break;
                        case -24:
                            message = context.getString(R.string.errorMessage_needInternetConnection); break;
                    }
                    Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                }
            });

            return null;
        }
    }

}
