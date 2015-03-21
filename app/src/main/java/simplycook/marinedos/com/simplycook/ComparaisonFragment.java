package simplycook.marinedos.com.simplycook;

import android.content.pm.ActivityInfo;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import simplycook.marinedos.com.simplycook.Utils.ComparatorItem;
import simplycook.marinedos.com.simplycook.Utils.ComparatorManager;
import simplycook.marinedos.com.simplycook.Utils.DeviceInformation;
import simplycook.marinedos.com.simplycook.Utils.ExpandableListAdapter_Comparaison;
import simplycook.marinedos.com.simplycook.Utils.Taste;
import simplycook.marinedos.com.simplycook.Utils.TasteMultiLike;
import simplycook.marinedos.com.simplycook.Utils.User;

public class ComparaisonFragment extends Fragment {
	
	/** @brief	The reference for firebase. */
    private static final Firebase ref = new Firebase("https://simplycook.firebaseio.com");
    /** @brief	The list of comparator item. */
    private final  ArrayList<ComparatorItem> listComparatorItem  = new ArrayList<ComparatorItem>();
    /** @brief	The counter to avoid multi update because of async call. */
    private int cptUpdate = 0;
    /** @brief	The maximum number of update allowed because of async call. */
    private int cptMaxUpdate;

    /** @brief	Variable must be final for firebase that why we use an array */
    private final int[] categoryIndex = new int[1];
    /** @brief	Variable must be final for firebase that why we use an array */
    private final int[] numCategory = new int[1];

    /** @brief	Map to stock category following an index */
    final Hashtable<Integer, String> mapCategory = new Hashtable<Integer, String>();
    /** @brief	Map to stock index following a category */
	final Hashtable<String, Integer> mapIndexCategory = new Hashtable<String, Integer>();

    ExpandableListAdapter_Comparaison listAdapter;
    ExpandableListView expListView;

    /** @brief	The header list of data (Name of category). */
    List<String> listDataHeader;
    /** @brief	The child list of data for each header (Taste name and the like of it for each user) . */
    HashMap<String, List<TasteMultiLike>> listDataChild;
	
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.comparaison_fragment, container, false);

		// Init the data header (the taste category)
        listDataHeader = new ArrayList<String>();
		// Init the data child (the taste name and the like of it for each user)
        listDataChild = new HashMap<String, List<TasteMultiLike>>();        
		// Get the expandable listview
        expListView = (ExpandableListView) root.findViewById(R.id.expandableListView_compare);
        listAdapter = new ExpandableListAdapter_Comparaison(getActivity(), listDataHeader, listDataChild);

        // Setting list adapter
        expListView.setAdapter(listAdapter);

		// Get the array of user for comparaison
        final User[] usersToCompare = ComparatorManager.getUsers();

        categoryIndex[0] = -1;
        numCategory[0] = 0;

		// Add an event listener for each taste category on firebase reference
        ref.child("/category").orderByChild("name").addValueEventListener(new ValueEventListener() {

            /**
             * @brief	Executes the data change action.
             *
             * @param	dataSnapshot	The data snapshot.
             */
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // For each catgory, save it and increase category number
                for (DataSnapshot category : dataSnapshot.getChildren()) {
                    // Gets category name
                    final String categoryName = category.child("name").getValue(String.class);
                    ++categoryIndex[0];
                    mapCategory.put(categoryIndex[0], categoryName);
                    mapIndexCategory.put(categoryName, categoryIndex[0]);
                }

                // For every user (max 4) to compare
                for(int i = 0; i < 4; ++i){
                    User user = usersToCompare[i];
					// User can be stock in any index so test if user is not null
                    if( user != null) {
						// Create a comparator item for this user
                        final ComparatorItem comparatorItem = new ComparatorItem(user);
						// Add it to the list of comparator
                        listComparatorItem.add(comparatorItem);

                        // Scan taste of the current user according the number of category taken
                        for (int j = 0; j < categoryIndex[0]+1; ++j){
							// Get the name of the category
                            final String categoryName = mapCategory.get(j);
							// Get user taste for this category in firebase
                            ref.child("/users/" + user.firebaseId + "/tastes")
                                    .startAt(categoryName)
                                    .endAt(categoryName)
                                    .addValueEventListener(new ValueEventListener() {

                                        /**
                                         * @brief	Executes the data change action.
                                         *
                                         * @param	dataSnapshot	The data snapshot.
                                         */
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getValue() != null) {
                                                ArrayList<Taste> listOfTaste = new ArrayList<Taste>();
                                                // For each food of the list
                                                for (DataSnapshot food : dataSnapshot.getChildren()) {
													// Get its name
                                                    String foodName = food.getKey();
                                                    // Get the like
													int foodLike = food.child("like").getValue(Integer.class);
													// Get the commentary 
                                                    String foodComment = food.child("comment").getValue(String.class);
													// Add informations to the list of taste
                                                    listOfTaste.add(new Taste(foodName, foodLike, foodComment));
                                                }
                                                // Populate comparatorItem for this user
                                                comparatorItem.getComparatorList()[mapIndexCategory.get(categoryName)] = listOfTaste;
                                            } else {
                                                comparatorItem.getComparatorList()[mapIndexCategory.get(categoryName)] = null;
                                            }

											// Launch an event to update the list
                                            eventListUpdated();
                                        }

                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {}

                                    });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
        return root;
    }


    /** @brief	Event list updated. */
    public void eventListUpdated(){
        ++cptUpdate;
        cptMaxUpdate = (categoryIndex[0] + 1) * ComparatorManager.getUsersNumber();

        //System.out.println("Update : " + cptUpdate + "/" + cptMaxUpdate);
        if(cptUpdate >= cptMaxUpdate){

            for(int userIndex = 0; userIndex < listComparatorItem.size(); ++userIndex){
                // Set the user's name we compared in the header of the view
                TextView name;
                switch (userIndex){
                    case 0 : name = (TextView)getView().findViewById(R.id.profil_name1); break;
                    case 1 : name = (TextView)getView().findViewById(R.id.profil_name2); break;
                    case 2 : name = (TextView)getView().findViewById(R.id.profil_name3); break;
                    case 3 : name = (TextView)getView().findViewById(R.id.profil_name4); break;
                    default: name = (TextView)getView().findViewById(R.id.profil_name1); break;
                }
                name.setText(listComparatorItem.get(userIndex).getUser().firstName);
            }

			// For all the category of food
            for(int localCategoryIndex = 0; localCategoryIndex <= categoryIndex[0]; ++localCategoryIndex) {
                // Fill header for the expendable list with the category
                listDataHeader.add(mapCategory.get(localCategoryIndex));

                // Lists of food for a category
                // Easy searchable list
                Hashtable<String, int[]> listTasteOfACategory = new Hashtable<String, int[]>();
                // Real list
                List<TasteMultiLike> listTasteMultiLike = new ArrayList<TasteMultiLike>();

				// For all user to compare
                for(int userIndex = 0; userIndex < listComparatorItem.size(); ++userIndex) {
                    // Array with all taste of one user according one category
                    ArrayList<Taste> listTaste = listComparatorItem.get(userIndex).getComparatorList()[localCategoryIndex];
                    if (listTaste != null) {
						// For each taste in the list of taste
                        for (Taste taste : listTaste) {
                            if (listTasteOfACategory.containsKey(taste.getName())) {
                                // Entry already exist, add only the like of this user
                                listTasteOfACategory.get(taste.getName())[userIndex] = taste.getLike();
                                Iterator<TasteMultiLike> it = listTasteMultiLike.iterator();
								// Search for the good taste
                                while(it.hasNext()){
                                    TasteMultiLike existingTaste = it.next();
                                    if(existingTaste.getName().equals(taste.getName())){
                                        // It is the same taste, add only the like
                                        existingTaste.getLikes()[userIndex] = taste.getLike();
                                        break;
                                    }
                                }
                            } else {
                                // Create an array with 4 entry (because we only compare 4 users)
                                int[] tab = new int[4];
                                tab[0] = tab[1] = tab[2] = tab[3] = -2;
                                // Create entry and add the like
                                TasteMultiLike newTaste = new TasteMultiLike(taste.getName());
                                newTaste.getLikes()[userIndex] = taste.getLike();
                                listTasteMultiLike.add(newTaste);

                                // For easy search
                                listTasteOfACategory.put(taste.getName(), tab);
                                listTasteOfACategory.get(taste.getName())[userIndex] = taste.getLike();
                            }
                        }
                    }

                } // end for all user

				// Once done, add the data
                listDataChild.put(listDataHeader.get(localCategoryIndex), listTasteMultiLike);
            }

            if (DeviceInformation.isTablet(getActivity())){
                for (int i = 0; i < listDataHeader.size(); ++i) {
                    expListView.expandGroup(i);
                }
            }

			// Notify that data changed
            listAdapter.notifyDataSetChanged();

            // Print for test
            /*String str;
            Set<String> set = listTasteOfACategory.keySet();
            Iterator<String> itr = set.iterator();
            while(itr.hasNext()){
                str = itr.next();
                int[] tab = listTasteOfACategory.get(str);
                System.out.println(str + ": [" + tab[0] + "," + tab[1] + "," + tab[2] + "," + tab[2] + "]");
            }*/
        }

    }
}
