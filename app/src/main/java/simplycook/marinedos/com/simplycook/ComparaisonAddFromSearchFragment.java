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

public class ComparaisonAddFromSearchFragment extends ListFragment {

    private static ListView mListView;
    private static ArrayAdapter<User> mAdapter;
    private static List<User> users;
    private ImageView searchButton;
    private EditText searchEditText;
    private static final Firebase ref = new Firebase("https://simplycook.firebaseio.com");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.comparaison_add_from_search_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        View root = getView();

        users = new ArrayList<User>();
        mAdapter = new FavorisArrayAdapter(getActivity(), R.layout.favoris_list_item, users);

        mListView = getListView();
        mListView.setAdapter(mAdapter);

        searchButton = (ImageView) root.findViewById(R.id.searchButton);
        searchEditText = (EditText) root.findViewById(R.id.searchText);

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

    public void onListItemClick(ListView l, View v, int pos, long id) {
        super.onListItemClick(l, v, pos, id);
        User user = mAdapter.getItem(pos);

        boolean succeed = ComparatorManager.addUser(user, ((ComparaisonAddFromSearch)getActivity()).index, getActivity());

        if(succeed){
            getActivity().finish();
        }
    }
    private void updateSearchList(){
        users.clear();
        // Launch search
        final String searchName = searchEditText.getText().toString();
        if(searchName.length() != 0) {
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
            searchEditText.setText("");
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
                                User newUser = new User();
                                newUser.firebaseId = user.getKey();
                                newUser.firstName = user.child("firstName").getValue(String.class);
                                newUser.lastName = user.child("lastName").getValue(String.class);
                                if (user.child("id").getValue() != null) {
                                    newUser.connexionMode = "facebook";
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
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
    }


    private static class getFbImageTaskAndNotify extends AsyncTask<HashMap<String, User>, Void, Bitmap> {
        public String userId;
        public User newUser;

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            // Set image to the ImageView
            super.onPostExecute(bitmap);

            mAdapter.notifyDataSetChanged();
        }

        @Override
        protected Bitmap doInBackground(HashMap<String, User>... params) {
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
