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


/** @brief	The connect fragment that manage user connection and login. */
public class ConnectFragment extends Fragment{
    
    /** @brief	The authentication button. */
    private LoginButton authButton;
    /** @brief	The login content. */
    private View mLoginContent;
    /** @brief	The login loader. */
    private View mLoginLoader;
    /** @brief	The popup view. */
    private View mPopupView;
    /** @brief	The alert dialog. */
    private AlertDialog mDialog;
    /** @brief	The login task. */
    private LogInTask mLoginTask;

    /**
     * @brief	This class helps to create, automatically open (if applicable), save, and restore the
     * 			Active Session in a way that is similar to Android UI lifecycles. For facebook.
     */
    private UiLifecycleHelper uiHelper;
    /** @brief	The login button. */
    private Button mLogin_btn;
	/** @brief	The new account button. */
	private Button mNew_account_btn;
    /** @brief	The reference for firebase. */
    private final Firebase ref = new Firebase("https://simplycook.firebaseio.com");

    private final Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

	/**
     * @brief	Function executed when the view is created. Called to have the fragment instantiate
     * 			its user interface view.
     *
     * @param	inflater		  	The LayoutInflater object that can be used to inflate any views
     * 								in the fragment.
     * @param	container		  	If non-null, this is the parent view that the fragment's UI
     * 								should be attached to. The fragment should not add the view
     * 								itself, but this can be used to generate the LayoutParams of the
     * 								view.
     * @param	savedInstanceState	If non-null, this fragment is being re-constructed from a
     * 								previous saved state as given here.
     *
     * @return	The View for the fragment's UI, or null.
     */
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

            /**
             * @brief	Executes the click action.
             *
             * @param	v	The View to process.
             */
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

			/**
			 * @brief	Executes the click action.
			 *
			 * @param	v	The View to process.
			 */
			@Override
            public void onClick(View v) {
                // Change activity to "create an account"
                Intent intent = new Intent(getActivity(), CreateAccountActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    /**
     * @brief	Called to do initial creation of a fragment.
     *
     * @param	savedInstanceState	If the fragment is being re-created from a previous saved state,
     * 								this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    /**
     * @brief	Facebook login in / out.
     *
     * @param	session  	The session.
     * @param	state	 	The state.
     * @param	exception	The exception.
     */
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

    /**
     * @brief	Executes the resume action. Called when the fragment is visible to the user and
     * 			actively running. For Facebook Login.
     */
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

    /**
     * @brief	Receive the result from a previous call to startActivityForResult(Intent, int).
     *
     * @param	requestCode	The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     * @param	resultCode 	The integer result code returned by the child activity through its setResult().
     * @param	data	   	An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    /** @brief	Called when the Fragment is no longer resumed. */
    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    /** @brief	Called when the fragment is no longer in use. */
    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
        if(mLoginTask != null){
            mLoginTask.cancel(true);
        }
    }

    /**
     * @brief	Called to ask the fragment to save its current dynamic state, so it can later be
     * 			reconstructed in a new instance of its process is restarted. If a new instance of the
     * 			fragment later needs to be created, the data you place in the Bundle here will be
     * 			available in the Bundle given to onCreate(Bundle), onCreateView(LayoutInflater,
     * 			ViewGroup, Bundle), and onActivityCreated(Bundle).
     *
     * @param	outState	Bundle in which to place your saved state.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    /** @brief	Task that try to log an user. */
    class LogInTask extends AsyncTask<Void, Void, Void>{

        /**
         * @brief	This method performs a computation on a background thread. The specified parameters
         * 			are the parameters passed to execute(Params...) by the caller of this task. Try to
         * 			log an user with password and email.
         *
         * @param	params	The parameters of the task.
         *
         * @return	Void.
         */
        @Override
        protected Void doInBackground(Void... params) {
            // Log in the user

			// Get email input 
            EditText email_input = (EditText) mPopupView.findViewById(R.id.identifiant);
            // Get the value of email in this input
			String email = email_input.getText().toString();
			// Get password input
            EditText password_input = (EditText) mPopupView.findViewById(R.id.password);
            // Get the password value
			String password = password_input.getText().toString();

			// Try to log the user using firebase
            ref.authWithPassword(email, password, new Firebase.AuthResultHandler() {

				/**
				 * @brief	Executes the authenticated action.
				 *
				 * @param	authData	Information describing the authentication.
				 */
				@Override
                public void onAuthenticated(AuthData authData) {
                    // Show content, hide loader
                    Anim.show(getActivity(), mLoginContent);
                    Anim.hide(getActivity(), mLoginLoader);
                    mDialog.cancel();

					// Store the user in the connexion manager
                    ConnexionManager.searchAndStroreUser();

					// Change activity to home page
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

                /**
                 * @brief	Executes the authentication error action.
                 *
                 * @param	firebaseError	The firebase error.
                 */
                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
					// Show content, hide loader
                    Anim.show(getActivity(), mLoginContent);
                    Anim.hide(getActivity(), mLoginLoader);

                    Context context = getActivity();
                    System.out.println("Firebase error : " + firebaseError.getCode());
                    System.out.println("Firebase error : " + firebaseError.getMessage());
                    int codeError = firebaseError.getCode();
                    String message = "";
					// Take care of the message error we have
                    switch(codeError){
						// Email is incorrect
                        case -15:
                            message = context.getString(R.string.errorMessage_incorrectEmail); break;
                        // Password is incorrect
						case -16 :
                            message = context.getString(R.string.errorMessage_incorrectPassword); break;
                        // User doesn't exist
						case -17 :
                            message = context.getString(R.string.errorMessage_userDoesNotExist); break;
                        // No internet connection
						case -24:
                            message = context.getString(R.string.errorMessage_needInternetConnection); break;
                    }
					// Create and show a toast to advertise
                    Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }

            });

            return null;
        }
    }
}
