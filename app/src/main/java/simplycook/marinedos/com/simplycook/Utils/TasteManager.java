package simplycook.marinedos.com.simplycook.Utils;

import android.content.Context;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** @brief	Class that manage the tastes. */
public class TasteManager {
	/** @brief	The reference for firebase. */
    private static final Firebase ref = new Firebase("https://simplycook.firebaseio.com");

    /**
    *@brief		Adds a taste to the category in firebase.
    *
    *@param		taste   	The taste.
    *@param		category	The category.
     */
    public static void addTaste(final Taste taste, final String category){
        AuthData authData = ref.getAuth();
        if (authData != null) {

            String id = ConnexionManager.user.firebaseId;

            Map<String, String> newTaste = new HashMap<String, String>();
            newTaste.put("like", Integer.toString(taste.getLike()));
            newTaste.put("comment", taste.getComment());

            ref.child("/users/" + id + "/tastes/" + taste.getName()).setValue(newTaste, category);

        }
    }

    /**
    *@brief		Updates the food list.
    *
    *@param		context		  	The context of the view.
    *@param		userId		  	Identifier for the user.
    *@param		listDataHeader	The list data header.
    *@param		listDataChild 	The list data child.
    *@param		listAdapter   	The list adapter.
    *@param		expListView   	The exponent list view.
    *@param		loader		  	The loader.
    *@param		expandList	  	List of expands.
     */
    public static void updateFoodList(final Context context, final String userId, final List<String> listDataHeader, final HashMap<String, List<Taste>> listDataChild, final ExpandableListAdapter listAdapter, final ExpandableListView expListView, final ProgressBar loader, final boolean expandList) {
        // Get all category of food
        ref.child("/category").orderByChild("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // For each category
                for (DataSnapshot category : dataSnapshot.getChildren()) {
                    // Get it's name
                    final String categoryName = category.child("name").getValue(String.class);
                    System.out.println("Category : " + categoryName);

                    // Get all tastes of the current user for the category
                    ref.child("/users/" + userId + "/tastes")
                            .startAt(categoryName)
                            .endAt(categoryName)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // If there is value
                                    if (dataSnapshot.getValue() != null) {
                                        if (listDataHeader.size() == 0) {
                                            listDataHeader.add(categoryName);
                                        } else {
                                            for (int i = 0; i < listDataHeader.size(); ++i) {
                                                if (listDataHeader.get(i).equals(categoryName)) {
                                                    // Category name already existe so delete the older
                                                    listDataHeader.remove(i);
                                                    break;
                                                }
                                            }
                                            listDataHeader.add(categoryName);
                                        }
                                        List<Taste> listOfTaste = new ArrayList<Taste>();
                                        // For each taste get it's name, like and comment and add it to the listOfTaste.
                                        for (DataSnapshot food : dataSnapshot.getChildren()) {
                                            String foodName = food.getKey();
                                            int foodLike = food.child("like").getValue(Integer.class);
                                            String foodComment = food.child("comment").getValue(String.class);
                                            listOfTaste.add(new Taste(foodName, foodLike, foodComment));
                                        }
                                        listDataChild.put(categoryName, listOfTaste);

                                        if (expandList){
                                            for (int i = 0; i < listDataHeader.size(); ++i) {
                                                expListView.expandGroup(i);
                                            }
                                        }

                                        listAdapter.notifyDataSetChanged();
                                    }
                                    Anim.hide(context, loader);
                                    Anim.show(context, expListView);
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
