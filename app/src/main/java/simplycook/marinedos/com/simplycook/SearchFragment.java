package simplycook.marinedos.com.simplycook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import simplycook.marinedos.com.simplycook.Utils.FavorisArrayAdapter;
import simplycook.marinedos.com.simplycook.Utils.User;
import simplycook.marinedos.com.simplycook.Utils.UsersManager;

/** @brief	The search fragment. */
public class SearchFragment extends ListFragment {

    /** @brief	The reference from firebase (BDD). */
    private static final Firebase ref = new Firebase("https://simplycook.firebaseio.com");
    /** @brief	The list view control, to display a list. */
    private ListView mListView;
    /** @brief	The adapter. */
    private static ArrayAdapter<User> mAdapter;
    /** @brief	The users list. */
    private static List<User> users = new ArrayList<User>();
    /** @brief	The search button. */
    private ImageView searchButton;
    /** @brief	The search edit text. */	
    private EditText searchEditText;
	
	/**
     * @brief	Called to do initial creation of a fragment.
     *
     * @param	savedInstanceState	If the fragment is being re-created from a previous saved state,
     * 								this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.search_fragment, container, false);

        return root;
    }

	/**
     * @brief	Called when the fragment's activity has been created and this fragment's view hierarchy instantiated.
     *
     * @param	savedInstanceState	If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View root = getView();

        mAdapter = new FavorisArrayAdapter(getActivity(), R.layout.favoris_list_item, users);

        mListView = getListView();
        mListView.setAdapter(mAdapter);

		// Find the search button in the view
        searchButton = (ImageView) root.findViewById(R.id.searchButton);
		// Find the search edit text in the view
        searchEditText = (EditText) root.findViewById(R.id.searchText);

		// Set an action listener on it that update the search list
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                updateSearchList();
                return false;
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSearchList();
            }
        });
    }

    /** @brief	Updates the search list. */
    private void updateSearchList(){
		// Clear the previous user list
        users.clear();
        // Launch the search
        // Get the name to search
        final String searchName = searchEditText.getText().toString();
        if(searchName.length() != 0) {
			// Split name between first name and last name
            final String[] searchNameSplit;
            final String firstName;
            final String lastName;
            if(searchName.contains(" ")){
                searchNameSplit = searchName.split(" ");
                firstName = searchNameSplit[0];
                lastName = searchNameSplit[1];
            }else{
                firstName = searchName;
                lastName = "";
            }
			// Reinit the edit text view
            searchEditText.setText("");
			// Interrogate the firebase
            ref.child("/users").orderByChild("lastName").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        for (DataSnapshot user : dataSnapshot.getChildren()) {
                            String firstNameUser = user.child("firstName").getValue(String.class);
                            String lastNameUser = user.child("lastName").getValue(String.class);
                            if ((firstName.equalsIgnoreCase(firstNameUser) || lastName.equalsIgnoreCase(firstNameUser))) {
                                // User found
                                System.out.println("User found");
								// Create a new user
                                User newUser = new User();
								// Get the information from the user of firebase and fill the new user with.
                                newUser.firebaseId = user.getKey();
                                newUser.firstName = user.child("firstName").getValue(String.class);
                                newUser.lastName = user.child("lastName").getValue(String.class);
								// If user is a facebook's user.
                                if (user.child("id").getValue() != null) {
                                    newUser.connexionMode = "facebook";
									// Get its profil image
                                    getFbImageTaskAndNotify getTask = new getFbImageTaskAndNotify();
                                    HashMap<String, User> userImage = new HashMap<String, User>();
                                    userImage.put(user.child("id").getValue().toString(), newUser);
                                    getTask.execute(userImage);

                                } else {
                                    newUser.connexionMode = "password";
                                    newUser.imageRessource = R.drawable.default_profil;
                                    users.add(newUser);
                                    System.out.println(users);
                                }
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {}
            });
        }
    }

    /** @brief	Task that get the facebook image of an user and notify after. */
    private static class getFbImageTaskAndNotify extends AsyncTask<HashMap<String, User>, Void, Bitmap> {
        /** @brief	Identifier for the user. */
        public String userId;
        /** @brief	The new user. */
        public User newUser;

        /**
         * @brief	Runs on the UI thread after doInBackground(Params...). 
         *
         * @param	bitmap	The result of the operation computed by doInBackground(Params...).
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            // Set image to the ImageView
            super.onPostExecute(bitmap);

            mAdapter.notifyDataSetChanged();
        }

        /**
         * @brief	This method performs a computation on a background thread. The specified parameters
         * 			are the parameters passed to execute(Params...) by the caller of this task.
         * 			Try to load a profil image from the facebook api.
         *
         * @param	params	The parameters of the task.
         *
         * @return	A Bitmap.
         */
        @Override
        protected Bitmap doInBackground(HashMap<String, User>... params) {
			// Get informations
            userId = params[0].keySet().toArray()[0].toString();
            newUser = params[0].get(userId);
            try {
                // Load image from graph facebook api
                URL imgUrl = new URL("https://graph.facebook.com/"
                        + userId + "/picture?width=128&height=128");

                InputStream in = (InputStream) imgUrl.getContent();

                newUser.imageBitmap = BitmapFactory.decodeStream(in);
                users.add(newUser);

                return BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

