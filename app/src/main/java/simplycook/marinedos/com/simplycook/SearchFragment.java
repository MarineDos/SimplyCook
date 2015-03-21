package simplycook.marinedos.com.simplycook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
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

    private ProgressBar loader;

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
        loader = (ProgressBar) root.findViewById(R.id.loader);

		// Find the search button in the view
        searchButton = (ImageView) root.findViewById(R.id.searchButton);
		// Find the search edit text in the view
        searchEditText = (EditText) root.findViewById(R.id.searchText);

		// Set an action listener on it that update the search list
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                loader.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.GONE);
                updateSearchList();
                return false;
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loader.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.GONE);
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
            // If there is a space, we search with first name and last name.
            if(searchName.contains(" ")){
                searchNameSplit = searchName.split(" ");
                firstName = searchNameSplit[0];
                // Need to test if we not just provide a space after the first name.
                if(searchNameSplit.length > 1)
                    lastName = searchNameSplit[1];
                else
                    lastName = "";
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
                                    UsersManager.getFbImageTaskAndNotify getTask = new UsersManager.getFbImageTaskAndNotify();
                                    HashMap<String, User> userImage = new HashMap<String, User>();
                                    userImage.put(user.child("id").getValue().toString(), newUser);
                                    getTask.setAdapter(mAdapter);
                                    getTask.setListUsers(users);
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
                    loader.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {}
            });
        }
    }

    public void onListItemClick(ListView l, View v, int pos, long id) {
        super.onListItemClick(l, v, pos, id);
        User user = mAdapter.getItem(pos);

        Intent intent = new Intent(getActivity(), ProfilActivity.class);
        intent.putExtra("firebaseId", user.firebaseId);
        getActivity().startActivity(intent);
    }

}

