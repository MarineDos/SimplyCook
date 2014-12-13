package simplycook.marinedos.com.simplycook.Utils;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TasteManager {
    private static final Firebase ref = new Firebase("https://simplycook.firebaseio.com");

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

    public static void updateFoodList(final String userId, final List<String> listDataHeader, final HashMap<String, List<Taste>> listDataChild, final ExpandableListAdapter listAdapter) {
        // Get all category of food
        ref.child("/category").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // For each category
                for (DataSnapshot category : dataSnapshot.getChildren()) {
                    // Get it's name
                    final String categoryName = category.child("name").getValue(String.class);

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

                                        listAdapter.notifyDataSetChanged();
                                    }
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
