package simplycook.marinedos.com.simplycook;

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

    private static final Firebase ref = new Firebase("https://simplycook.firebaseio.com");

    private final  ArrayList<ComparatorItem> listComparatorItem  = new ArrayList<ComparatorItem>();
    private int cptUpdate = 0;
    private int cptMaxUpdate;
    private final int[] categoryIndex = new int[1];
    private final int[] numCategory = new int[1];

    final Hashtable<Integer, String> mapCategory = new Hashtable<Integer, String>();
    final Hashtable<String, Integer> mapIndexCategory = new Hashtable<String, Integer>();

    ExpandableListAdapter_Comparaison listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<TasteMultiLike>> listDataChild;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.comparaison_fragment, container, false);

        // Get the listview
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<TasteMultiLike>>();
        expListView = (ExpandableListView) root.findViewById(R.id.expandableListView_compare);
        listAdapter = new ExpandableListAdapter_Comparaison(getActivity(), listDataHeader, listDataChild);

        // Setting list adapter
        expListView.setAdapter(listAdapter);

        final User[] usersToCompare = ComparatorManager.getUsers();

        categoryIndex[0] = -1;
        numCategory[0] = 0;

        ref.child("/category").orderByChild("name").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // For each catgory, save it and increase category number
                for (DataSnapshot category : dataSnapshot.getChildren()) {
                    // Get its name
                    final String categoryName = category.child("name").getValue(String.class);
                    ++categoryIndex[0];
                    mapCategory.put(categoryIndex[0], categoryName);
                    mapIndexCategory.put(categoryName, categoryIndex[0]);
                }

                // For every user (max 4) to compare
                for(int i = 0; i < 4; ++i){
                    User user = usersToCompare[i];
                    if( user != null) {
                        final ComparatorItem comparatorItem = new ComparatorItem(user);
                        listComparatorItem.add(comparatorItem);

                        // Scan taste of the current user according to one category
                        for (int j = 0; j < categoryIndex[0]+1; ++j){
                            final String categoryName = mapCategory.get(j);
                            ref.child("/users/" + user.firebaseId + "/tastes")
                                    .startAt(categoryName)
                                    .endAt(categoryName)
                                    .addValueEventListener(new ValueEventListener() {

                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getValue() != null) {
                                                ArrayList<Taste> listOfTaste = new ArrayList<Taste>();
                                                // For each food of the list
                                                for (DataSnapshot food : dataSnapshot.getChildren()) {
                                                    String foodName = food.getKey();
                                                    int foodLike = food.child("like").getValue(Integer.class);
                                                    String foodComment = food.child("comment").getValue(String.class);
                                                    listOfTaste.add(new Taste(foodName, foodLike, foodComment));
                                                }
                                                // Populate comparatorItem for this user
                                                comparatorItem.getComparatorList()[mapIndexCategory.get(categoryName)] = listOfTaste;
                                            } else {
                                                comparatorItem.getComparatorList()[mapIndexCategory.get(categoryName)] = null;
                                            }
                                            eventListUpdated();
                                        }

                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {

                                        }
                                    });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        return root;
    }

    public void eventListUpdated(){
        ++cptUpdate;
        cptMaxUpdate = (categoryIndex[0] + 1) * ComparatorManager.getUsersNumber();

        //System.out.println("Update : " + cptUpdate + "/" + cptMaxUpdate);
        if(cptUpdate >= cptMaxUpdate){

            for(int userIndex = 0; userIndex < listComparatorItem.size(); ++userIndex){
                // Set name of users to compare in the header
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

            for(int localCategoryIndex = 0; localCategoryIndex <= categoryIndex[0]; ++localCategoryIndex) {
                // Fill header for expendable list
                listDataHeader.add(mapCategory.get(localCategoryIndex));

                // Lists of food for a category
                // Easy searchable list
                Hashtable<String, int[]> listTasteOfACategory = new Hashtable<String, int[]>();
                // Real list
                List<TasteMultiLike> listTasteMultiLike = new ArrayList<TasteMultiLike>();

                for(int userIndex = 0; userIndex < listComparatorItem.size(); ++userIndex) {
                    // Array with all taste of one user according one category
                    ArrayList<Taste> listTaste = listComparatorItem.get(userIndex).getComparatorList()[localCategoryIndex];
                    if (listTaste != null) {
                        for (Taste taste : listTaste) {
                            if (listTasteOfACategory.containsKey(taste.getName())) {
                                // Entry already exist, add only the like
                                listTasteOfACategory.get(taste.getName())[userIndex] = taste.getLike();
                                Iterator<TasteMultiLike> it = listTasteMultiLike.iterator();
                                while(it.hasNext()){
                                    TasteMultiLike existingTaste = it.next();
                                    if(existingTaste.getName().equals(taste.getName())){
                                        // It is the same taste, add only the like
                                        existingTaste.getLikes()[userIndex] = taste.getLike();
                                        break;
                                    }
                                }
                            } else {
                                // Create entry and add the like
                                int[] tab = new int[4];
                                tab[0] = tab[1] = tab[2] = tab[3] = -2;
                                TasteMultiLike newTaste = new TasteMultiLike(taste.getName());
                                newTaste.getLikes()[userIndex] = taste.getLike();
                                listTasteMultiLike.add(newTaste);

                                // For easy search
                                listTasteOfACategory.put(taste.getName(), tab);
                                listTasteOfACategory.get(taste.getName())[userIndex] = taste.getLike();
                            }
                        }
                    }
                }
                listDataChild.put(listDataHeader.get(localCategoryIndex), listTasteMultiLike);
            }
            if (DeviceInformation.isTablet(getActivity())){
                for (int i = 0; i < listDataHeader.size(); ++i) {
                    expListView.expandGroup(i);
                }
            }
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
