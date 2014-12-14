package simplycook.marinedos.com.simplycook.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import simplycook.marinedos.com.simplycook.R;

public class UsersManager {

    private static final Firebase ref = new Firebase("https://simplycook.firebaseio.com");
    private static List<User> users;
    private static ArrayAdapter<User> listAdapter;

    public static List<User> updateAllUsersList(final ArrayAdapter<User> adapter, final List<User> usersList){
        users = usersList;
        listAdapter = adapter;
        ref.child("/users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                        User newUser = new User();
                        newUser.firebaseId = user.getKey();
                        newUser.firstName = user.child("firstName").getValue(String.class);
                        newUser.lastName = user.child("lastName").getValue(String.class);
                        if(user.child("id").getValue() != null){
                            newUser.connexionMode = "facebook";
                            getFbImageTaskAndNotify getTask = new getFbImageTaskAndNotify();
                            HashMap<String, User> userImage = new HashMap<String, User>();
                            userImage.put(user.child("id").getValue().toString(), newUser);
                            getTask.execute(userImage);

                        }else{
                            newUser.connexionMode = "password";
                            newUser.imageRessource = R.drawable.default_profil;
                            usersList.add(newUser);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        return users;
    }

    static class getFbImageTaskAndNotify extends AsyncTask<HashMap<String, User>, Void, Bitmap> {
        public String userId;
        public User newUser;

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            // Set image to the ImageView
            super.onPostExecute(bitmap);

            listAdapter.notifyDataSetChanged();
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

    public static void updateProfil(String userFirebaseId, final TextView nameView, final ImageView imgView){
        ref.child("/users/" + userFirebaseId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    nameView.setText( dataSnapshot.child("firstName").getValue(String.class) + " " +  dataSnapshot.child("lastName").getValue(String.class));
                    if(dataSnapshot.child("id").getValue() != null){
                       getFbImageTask getTask = new getFbImageTask();
                       HashMap<String, ImageView> userImage = new HashMap<String, ImageView>();
                       userImage.put(dataSnapshot.child("id").getValue().toString(), imgView);
                       getTask.execute(userImage);
                    }else{
                        imgView.setImageResource(R.drawable.default_profil);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
    static class getFbImageTask extends AsyncTask<HashMap<String, ImageView>, Void, Bitmap> {
        public String userId;
        public ImageView imgView;

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            // Set image to the ImageView
            super.onPostExecute(bitmap);

            imgView.setImageBitmap(bitmap);
        }

        @Override
        protected Bitmap doInBackground(HashMap<String, ImageView>... params) {
            userId = params[0].keySet().toArray()[0].toString();
            imgView = params[0].get(userId);
            try {
                // Load image from graph facebook api
                URL imgUrl = new URL("https://graph.facebook.com/"
                        + userId + "/picture?width=128&height=128");

                InputStream in = (InputStream) imgUrl.getContent();

                return BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}