package simplycook.marinedos.com.simplycook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import simplycook.marinedos.com.simplycook.Utils.Anim;
import simplycook.marinedos.com.simplycook.Utils.ConnexionManager;

/** @brief	The create account fragment. Manage the account creation. */
public class CreateAccountFragment extends Fragment{

    /** @brief	The create account button. */
    private Button mCreateAccount_btn;
    private TextView mFirstName_input, mLastName_input, mEmail_input, mPassword_input;
    /** @brief	The create account task. */
    private CreateAccountTask mCreateAccountTask;
    private View mCreateAccountContent, mCreateAccountLoader;
    /** @brief	The login task. */
    private LogInTask mLoginTask;
    /** @brief	The reference for the firebase (BDD). */
    private final Firebase ref = new Firebase("https://simplycook.firebaseio.com");
	
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
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.create_account_fragment, container, false);
    }

	/**
     * @brief	Called when the fragment's activity has been created and this fragment's view hierarchy instantiated.
     *
     * @param	savedInstanceState	If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final View rootView = getView();
        final Context context = getActivity();

        // Hide loader, show content
        mCreateAccountContent = rootView.findViewById(R.id.create_account_content);
        mCreateAccountLoader = rootView.findViewById(R.id.create_account_loader);
        Anim.hide(context, mCreateAccountLoader);

        // Create account button
        mCreateAccount_btn = (Button) rootView.findViewById(R.id.btn_create_new_account);
		// Set on click listener
        mCreateAccount_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean goOn = true;

				// When click, show loader, hide content
                Anim.show(context, mCreateAccountLoader);
                Anim.hide(context, mCreateAccountContent);

                // Check if all input are full to create the account, if not set goOn to false
                mFirstName_input = (TextView) rootView.findViewById(R.id.firstName);
                if (mFirstName_input.getText().toString().trim().equals("")) {
                    mFirstName_input.setError(context.getString(R.string.errorFirstNameRequired));
                    goOn = false;
                }

                mLastName_input = (TextView) rootView.findViewById(R.id.lastName);
                if (mLastName_input.getText().toString().trim().equals("")) {
                    mLastName_input.setError(context.getString(R.string.errorLastNameRequired));
                    goOn = false;
                }

                mEmail_input = (TextView) rootView.findViewById(R.id.email);
                if (mEmail_input.getText().toString().trim().equals("")) {
                    mEmail_input.setError(context.getString(R.string.errorEmailRequired));
                    goOn = false;
                }

                mPassword_input = (TextView) rootView.findViewById(R.id.password);
                if (mPassword_input.getText().toString().trim().equals("")) {
                    mPassword_input.setError(context.getString(R.string.errorPasswordRequired));
                    goOn = false;
                }

				// If all fiels are full
                if (goOn) {
					// Create a create account task
                    mCreateAccountTask = new CreateAccountTask();
                    // Launch create account task
                    mCreateAccountTask.execute();
                } else {
                    // Hide loader, show content for next try
                    Anim.show(context, mCreateAccountContent);
                    Anim.hide(context, mCreateAccountLoader);
                }
            }
        });
    }

    /** @brief	Called when the fragment is no longer in use. */
    @Override
    public void onDestroy() {
        super.onDestroy();
		// Delete the create account task
        if(mCreateAccountTask != null){
            mCreateAccountTask.cancel(true);
        }
    }


    /** @brief	A task that manage an account creation. */
    class CreateAccountTask extends AsyncTask<Void, Void, Void>{

        /**
         * @brief	This method performs a computation on a background thread. The specified parameters
         * 			are the parameters passed to execute(Params...) by the caller of this task.
         * 			Try to create a user in database with a given mail and password.
         *
         * @param	params	The parameters of the task.
         *
         * @return	Void.
         */
        @Override
        protected Void doInBackground(Void... params) {
            final Context context = getActivity();
			// Get email and password for user creation
            final String email = mEmail_input.getText().toString();
            final String password = mPassword_input.getText().toString();

            // Try to create User in firebase
            ref.createUser(email, password, new Firebase.ResultHandler() {

                /** @brief	Executes the success action. */
                @Override
                public void onSuccess() {
                    // Create user in database with informations provide by forms
                    Map<String, String> newUser = new HashMap<String, String>();
                    newUser.put("firstName", mFirstName_input.getText().toString());
                    newUser.put("lastName", mLastName_input.getText().toString());
                    newUser.put("email", mEmail_input.getText().toString());

					// Update the reference of firebase with the new user
                    Firebase newRef = ref.child("/users/").push();
                    newRef.setValue(newUser, mEmail_input.getText().toString());

                    newUser.put("firebaseId", newRef.getKey());
                    newUser.put("connexionMode", "password");
					// Store the user in the ConnexionManager
                    ConnexionManager.storeUser(newUser);

					// Hide the loader
                    Anim.hide(context, mCreateAccountLoader);

                    // Go back to login page
                   // Intent intent = new Intent(getActivity(), ConnectActivity.class);
                   // startActivity(intent);

                    // Create and launch the login task with email and password
                    ArrayList<String> passing = new ArrayList<String>();
                    passing.add(email);
                    passing.add(password);
                    mLoginTask = new LogInTask();
                    mLoginTask.execute(passing);
                }

                /**
                 * @brief	Executes the error action.
                 *
                 * @param	firebaseError	The firebase message error.
                 */
                @Override
                public void onError(FirebaseError firebaseError) {
                    System.out.println("Error code : " + firebaseError.getCode());
                    String message = "";
					// Take care of the different error we can have
                    switch(firebaseError.getCode()){
                        // User's mail is already in the database
						case -18:
                            message = context.getString(R.string.errorMessage_emailAlreadyUsed);
                            mEmail_input.setError(context.getString(R.string.errorMessage_emailAlreadyUsed));
                            mEmail_input.setText("");
                            break;
						// No internet connection detected
                        case -24:
                            message = context.getString(R.string.errorMessage_needInternetConnection); break;
                    }

					// Hide loader and show content
                    Anim.hide(context, mCreateAccountLoader);
                    Anim.show(context, mCreateAccountContent);

                    // Create and show dialog to display error
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(message)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            });
            return null;
        }
    } // End of createAccountTask


    /** @brief	Task that try to log an user after its creation. */
    class LogInTask extends AsyncTask<ArrayList<String>, Void, Void>{

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
        protected Void doInBackground(ArrayList<String>... params) {
            // Get the parameters
            ArrayList<String> passed = params[0];
            String email = passed.get(0);
            String password = passed.get(1);

			// Try to login the user
            ref.authWithPassword(email, password, new Firebase.AuthResultHandler() {

                /**
                 * @brief	Executes the authenticated action.
                 *
                 * @param	authData	Information describing the authentication.
                 */
                @Override
                public void onAuthenticated(AuthData authData) {
                    // User is logged, change activity to home page
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    startActivity(intent);
                }

                /**
                 * @brief	Executes the authentication error action.
                 *
                 * @param	firebaseError	The firebase error.
                 */
                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    Context context = getActivity();
                    System.out.println("Firebase error : " + firebaseError.getCode());
                    System.out.println("Firebase error : " + firebaseError.getMessage());
                    int codeError = firebaseError.getCode();
                    String message = "";
					// Take care of error we have
                    switch(codeError){
						// Need an internet connection
                        case -24:
                            message = context.getString(R.string.errorMessage_needInternetConnection); break;
                    }
					// Create a toast to advertise and show it
                    Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

					// Restart the create account activity
                    Intent intent = new Intent(getActivity(), CreateAccountActivity.class);
                    startActivity(intent);
                }
            });

            return null;
        }
    }
}
