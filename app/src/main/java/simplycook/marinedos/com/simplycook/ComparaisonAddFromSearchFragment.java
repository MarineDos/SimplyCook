package simplycook.marinedos.com.simplycook;

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

import simplycook.marinedos.com.simplycook.Utils.ComparatorManager;
import simplycook.marinedos.com.simplycook.Utils.FavorisArrayAdapter;
import simplycook.marinedos.com.simplycook.Utils.User;
import simplycook.marinedos.com.simplycook.Utils.UsersManager;

/**
 * @brief	The fragment that manage the search of user by name for adding it in the comparaison
 * 			choice fragment.
 */
public class ComparaisonAddFromSearchFragment extends ListFragment {

    /** @brief	The list view. */
    private static ListView mListView;
    /** @brief	The adapter for the list view. */
    private static ArrayAdapter<User> mAdapter;
    /** @brief	The users list. */
    private static List<User> users;
    /** @brief	The search button. */
    private ImageView searchButton;
    /** @brief	The search edit text. */
    private EditText searchEditText;
    /** @brief	The reference of Firebase. */
    private static final Firebase ref = new Firebase("https://simplycook.firebaseio.com");
	
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.comparaison_add_from_search_fragment, container, false);
    }

	/**
     * @brief	Called when the fragment's activity has been created and this fragment's view hierarchy instantiated.
     *
     * @param	savedInstanceState	If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

		// Init the class members
        View root = getView();

        users = new ArrayList<User>();
        mAdapter = new FavorisArrayAdapter(getActivity(), R.layout.favoris_list_item, users);

        mListView = getListView();
        mListView.setAdapter(mAdapter);

        searchButton = (ImageView) root.findViewById(R.id.searchButton);
        searchEditText = (EditText) root.findViewById(R.id.searchText);

		// Add event on the search edit text
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                updateSearchList();
                return false;
            }
        });

		// Add event on the search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSearchList();
            }
        });
    }

	/**
     * @brief	Called when an item of the list is clicked.
     *
     * @param	l  	The list View.
     * @param	v  	The View to process.
     * @param	pos	The position in the list.
     * @param	id 	The identifier of the item.
     */
    public void onListItemClick(ListView l, View v, int pos, long id) {
        super.onListItemClick(l, v, pos, id);

		// Get the user selected 
        User user = mAdapter.getItem(pos);

		// Add this user to the comparator manager
        boolean succeed = ComparatorManager.addUser(user, ((ComparaisonAddFromSearch)getActivity()).index, getActivity());

        if(succeed){
            getActivity().finish();
        }
    }

    /** @brief	Updates the search list. */
    private void updateSearchList(){
        users.clear();
        // Launch search with the name passed in the search edit text
        final String searchName = searchEditText.getText().toString();
        if(searchName.length() != 0) {
			// Get first name and last name
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

			// Remove entry in search edit text
            searchEditText.setText("");

			// Get all users from the reference of Firebase
            ref.child("/users").orderByChild("lastName").addListenerForSingleValueEvent(new ValueEventListener() {

				/**
				 * @brief	Executes the data change action.
				 *
				 * @param	dataSnapshot	The data snapshot.
				 */
				@Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
						// For each user found
                        for (DataSnapshot user : dataSnapshot.getChildren()) {
							// Get first name and last name
                            String firstNameUser = user.child("firstName").getValue(String.class);
                            String lastNameUser = user.child("lastName").getValue(String.class);
							// Test if they are equal to the fist name and last name get previously
                            if ((firstName.equalsIgnoreCase(firstNameUser) || lastName.equalsIgnoreCase(firstNameUser))) {
                                // User found
                                System.out.println("User found");
								// Create User and update data from firebase
                                User newUser = new User();
                                newUser.firebaseId = user.getKey();
                                newUser.firstName = user.child("firstName").getValue(String.class);
                                newUser.lastName = user.child("lastName").getValue(String.class);

								// Get its user image following its connexion mode (fb or password)
                                if (user.child("id").getValue() != null) {
                                    newUser.connexionMode = "facebook";
									// Create a task to get fb image and launch it
                                    UsersManager.getFbImageTaskAndNotify getTask = new UsersManager.getFbImageTaskAndNotify();
                                    HashMap<String, User> userImage = new HashMap<String, User>();
                                    userImage.put(user.child("id").getValue().toString(), newUser);
                                    getTask.setAdapter(mAdapter);
                                    getTask.setListUsers(users);
                                    getTask.execute(userImage);
                                } else {
									// Simply get the default profil image
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
}
