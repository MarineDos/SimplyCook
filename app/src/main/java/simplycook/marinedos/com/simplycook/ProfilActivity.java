package simplycook.marinedos.com.simplycook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ProfilActivity extends ActionBarActivity {
    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private final Firebase ref = new Firebase("https://simplycook.firebaseio.com");
    private TextView profilName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil_activity);
        if (savedInstanceState == null) {
            Fragment fragment = new ProfilFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }

        // List
        expListView = (ExpandableListView) findViewById(R.id.expandableListView_food);
        prepareListData();
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);

        // Header
        profilName = (TextView)findViewById(R.id.profil_name);
        String search;

        AuthData authData = ref.getAuth();
        if (authData != null) {

            // If user if connected with Facebook, get his profil image
            if(authData.getProvider().equals("facebook")){
                search = "id";
                // Get profil image
                String userId = authData.getProviderData().get("id").toString();
                getFbImageTask getTask = new getFbImageTask();
                getTask.execute(userId);
            }else{
                // Put default image
                search = "email";
                ImageView image = (ImageView)findViewById(R.id.profil_img);
                image.setImageResource(R.drawable.default_profil);

            }


            String userId = authData.getProviderData().get(search).toString();
            System.out.println(userId);
            // Search in firebase data to get name of the user
            ref.child("/users/")
                    .startAt(userId)
                    .endAt(userId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            boolean exists = (snapshot.getValue() != null);
                            if(!exists) {
                                System.out.println("User doesn't exist");
                            } else {
                                System.out.println("User exist");
                                Map<String, Object> user = (Map<String, Object>)snapshot.getValue();
                                Set set = user.keySet();
                                Iterator iter = set.iterator();
                                Map<String, Object> value = (Map<String, Object>)user.get(iter.next());

                                String name = value.get("firstName").toString() + " " + value.get("lastName").toString();
                                profilName.setText(name);
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });

        }
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Viande");
        listDataHeader.add("Poisson");
        listDataHeader.add("Légume");

        // Adding child data
        List<String> viande = new ArrayList<String>();
        viande.add("Boeuf");
        viande.add("Agneau");
        viande.add("Cheval");
        viande.add("Poulet");

        List<String> poisson = new ArrayList<String>();
        poisson.add("Cabillaud");
        poisson.add("Saumon");
        poisson.add("Bar");

        List<String> legume = new ArrayList<String>();
        legume.add("Carotte");
        legume.add("Courgette");

        listDataChild.put(listDataHeader.get(0), viande);
        listDataChild.put(listDataHeader.get(1), poisson);
        listDataChild.put(listDataHeader.get(2), legume);
    }

    class getFbImageTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            // Set image to the ImageView
            super.onPostExecute(bitmap);
            ImageView image = (ImageView)findViewById(R.id.profil_img);
            image.setImageBitmap(bitmap);
        }

        @Override
        protected Bitmap doInBackground(String... userIds) {
            String userId = userIds[0];
            try {
                // Load image from graph facebook api
                URL imgUrl = new URL("https://graph.facebook.com/"
                        + userId + "/picture?width=128&height=128");

                System.out.println(imgUrl);

                InputStream in = (InputStream) imgUrl.getContent();

                return BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
