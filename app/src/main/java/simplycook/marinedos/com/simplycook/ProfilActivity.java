package simplycook.marinedos.com.simplycook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import simplycook.marinedos.com.simplycook.Utils.ConnexionManager;


public class ProfilActivity extends ActionBarActivity {
    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, List<Taste>> listDataChild;
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
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Taste selectedTaste = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);

                if(!selectedTaste.getComment().equals("")){
                    Toast toast = Toast.makeText(
                            getApplicationContext(),
                            selectedTaste.getName() + " : " + selectedTaste.getComment(),
                            Toast.LENGTH_LONG
                    );
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                }
                return false;
            }
        });

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
        listDataChild = new HashMap<String, List<Taste>>();

        // Adding child data
        listDataHeader.add("Viande");
        listDataHeader.add("Poisson");
        listDataHeader.add("Légume");

        // Adding child data
        List<Taste> viande = new ArrayList<Taste>();
        viande.add(new Taste("Boeuf", 1, "Surtout en sauce"));
        viande.add(new Taste("Agneau", 1, ""));
        viande.add(new Taste("Cheval", 1, ""));
        viande.add(new Taste("Poulet", 1, ""));

        List<Taste> poisson = new ArrayList<Taste>();
        poisson.add(new Taste("Cabillaud", 1, ""));
        poisson.add(new Taste("Saumon", 1, "Que le saumon fumé"));
        poisson.add(new Taste("Bar", 1, "Je me souviens plus..."));

        List<Taste> legume = new ArrayList<Taste>();
        legume.add(new Taste("Carotte", 1, "C'est pour ça que je suis aimable"));
        legume.add(new Taste("Courgette", 1, ""));

        listDataChild.put(listDataHeader.get(0), viande);
        listDataChild.put(listDataHeader.get(1), poisson);
        listDataChild.put(listDataHeader.get(2), legume);
    }

    class Taste{
        private String m_name;
        private String m_comment;
        private int m_like;

        public Taste(String name, int like, String comment){
            m_name = name;
            m_comment = comment;
            m_like = like;
        }
        public String getName(){
            return m_name;
        }
        public String getComment(){
            return m_comment;
        }
        public int getLike(){
            return m_like;
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_disconnect) {
            ConnexionManager.disconnect(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
