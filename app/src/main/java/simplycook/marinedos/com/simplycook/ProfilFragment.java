package simplycook.marinedos.com.simplycook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ProfilFragment extends Fragment {
    private final Firebase ref = new Firebase("https://simplycook.firebaseio.com");
    private TextView profilName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.profil_fragment, container, false);

        profilName = (TextView) view.findViewById(R.id.profil_name);

        AuthData authData = ref.getAuth();
        if (authData != null) {

            // If user if connected with Facebook, get his profil image
            if(authData.getProvider().equals("facebook")){
                // Get profil image
                String userId = authData.getProviderData().get("id").toString();
                getFbImageTask getTask = new getFbImageTask();
                getTask.execute(userId);

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

        return view;
    }

    class getFbImageTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            // Set image to the ImageView
            View rootView = getView();
            ImageView image = (ImageView)rootView.findViewById(R.id.profil_img);
            image.setImageBitmap(bitmap);
            super.onPostExecute(bitmap);
        }

        @Override
        protected Bitmap doInBackground(String... userIds) {
            String userId = userIds[0];
            try {
                // Load image from graph facebook api
                URL imgUrl = new URL("https://graph.facebook.com/"
                        + userId + "/picture?width=500&height=500");

                InputStream in = (InputStream) imgUrl.getContent();

                return BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
