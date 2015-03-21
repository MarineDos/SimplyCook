package simplycook.marinedos.com.simplycook.Utils.tabsSwipeMyProfil;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import simplycook.marinedos.com.simplycook.R;
import simplycook.marinedos.com.simplycook.Utils.ConnexionManager;
import simplycook.marinedos.com.simplycook.Utils.DeviceInformation;
import simplycook.marinedos.com.simplycook.Utils.ExpandableListAdapter_Message;
import simplycook.marinedos.com.simplycook.Utils.TasteMessage;

public class messagesFragment extends Fragment {
    private ExpandableListAdapter_Message listAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, List<TasteMessage>> listDataChild;

    private static final Firebase ref = new Firebase("https://simplycook.firebaseio.com");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.profil_message_tab, container, false);

        // List
        expListView = (ExpandableListView) rootView.findViewById(R.id.expandableListView_message);
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<TasteMessage>>();
        listAdapter = new ExpandableListAdapter_Message(getActivity(), listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);

        prepareListData(DeviceInformation.isTablet(getActivity()));

        return rootView;
    }

    private void prepareListData(final boolean expandList){
        // Get all category of food
        ref.child("/category").orderByChild("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // For each category
                for (DataSnapshot category : dataSnapshot.getChildren()) {
                    // Get it's name
                    final String categoryName = category.child("name").getValue(String.class);

                    // Get all tastes of the current user for the category
                    ref.child("/users/" + ConnexionManager.user.firebaseId + "/messages")
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
                                        List<TasteMessage> listOfMessage = new ArrayList<TasteMessage>();
                                        // For each taste get it's name, like and comment and add it to the listOfTaste.
                                        for (DataSnapshot food : dataSnapshot.getChildren()) {
                                            String foodName = food.child("foodName").getValue(String.class);
                                            int foodLike = food.child("like").getValue(Integer.class);
                                            String foodComment = food.child("comment").getValue(String.class);
                                            String firstName = food.child("firstName").getValue(String.class);
                                            String lastName = food.child("lastName").getValue(String.class);
                                            listOfMessage.add(new TasteMessage(foodName, foodLike, foodComment, firstName, lastName));
                                        }
                                        listDataChild.put(categoryName, listOfMessage);

                                        if (expandList){
                                            for (int i = 0; i < listDataHeader.size(); ++i) {
                                                expListView.expandGroup(i);
                                            }
                                        }

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
