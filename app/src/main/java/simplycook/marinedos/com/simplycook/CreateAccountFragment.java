package simplycook.marinedos.com.simplycook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marine on 03/11/2014.
 */
public class CreateAccountFragment extends Fragment{
    private Button mCreateAccount_btn;
    private TextView mFirstName_input, mLastName_input, mEmail_input, mPassword_input;
    private CreateAccountTask mCreateAccountTask;
    private View mCreateAccountContent, mCreateAccountLoader;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.create_account_fragment, container, false);
    }

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
        mCreateAccount_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean goOn = true;

                Anim.show(context, mCreateAccountLoader);
                Anim.hide(context, mCreateAccountContent);

                // check if all input are full
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

                if (goOn) {
                    // Launch create account task
                    mCreateAccountTask = new CreateAccountTask();
                    mCreateAccountTask.execute();
                } else {
                    // Hide loader, show content for next try
                    Anim.show(context, mCreateAccountContent);
                    Anim.hide(context, mCreateAccountLoader);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mCreateAccountTask != null){
            mCreateAccountTask.cancel(true);
        }
    }

    class CreateAccountTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            final Context context = getActivity();

            final Firebase ref = new Firebase("https://simplycook.firebaseio.com");
            String email = mEmail_input.getText().toString();
            String password = mPassword_input.getText().toString();

            // Try to create User
            ref.createUser(email, password, new Firebase.ResultHandler() {
                @Override
                public void onSuccess() {
                    // Create user in database
                    Map<String, String> newUser = new HashMap<String, String>();
                    newUser.put("firstName", mFirstName_input.getText().toString());
                    newUser.put("lastName", mLastName_input.getText().toString());
                    newUser.put("email", mEmail_input.getText().toString());

                    Firebase newRef = ref.child("/users/").push();
                    newRef.setValue(newUser, mEmail_input.getText().toString());

                    Anim.hide(context, mCreateAccountLoader);

                    // Go back to login page
                    Intent intent = new Intent(getActivity(), ConnectActivity.class);
                    startActivity(intent);

                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    System.out.println("Error code : " + firebaseError.getCode());
                    String message = "";
                    switch(firebaseError.getCode()){
                        case -18:
                            message = context.getString(R.string.errorMessage_emailAlreadyUsed);
                            mEmail_input.setError(context.getString(R.string.errorMessage_emailAlreadyUsed));
                            mEmail_input.setText("");
                    }

                    Anim.hide(context, mCreateAccountLoader);
                    Anim.show(context, mCreateAccountContent);

                    // Show dialog to display error
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
    }
}
