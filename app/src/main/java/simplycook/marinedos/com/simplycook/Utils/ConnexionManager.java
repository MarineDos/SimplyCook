package simplycook.marinedos.com.simplycook.Utils;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import simplycook.marinedos.com.simplycook.ConnectActivity;
import simplycook.marinedos.com.simplycook.HomeActivity;
import simplycook.marinedos.com.simplycook.R;

/** @brief	Class that manage the connexions. */
public class ConnexionManager {
	
	/** @brief	The reference for firebase. */
    private static final Firebase ref = new Firebase("https://simplycook.firebaseio.com");
    /** @brief	The user. */
    public static User user = new User();

    /**
    *@brief	Stores a new user.
    *
    *@param	newUser	The new user.
     */
    public static void storeUser(Map<String, String> newUser){

        AuthData authData = ref.getAuth();

        user.connexionMode = newUser.get("connexionMode");
        if(user.connexionMode.equals("facebook")){
            String userId = authData.getProviderData().get("id").toString();
            getFbImageTask getTask = new getFbImageTask();
            getTask.execute(userId);
        }else{
            user.imageRessource = R.drawable.default_profil;
        }

        user.firstName = newUser.get("firstName");
        user.lastName = newUser.get("lastName");

        System.out.println("Stored id : " + newUser.get("firebaseId"));
        user.firebaseId = newUser.get("firebaseId");

    }

    /** @brief	Searches for the first and store user. */
    public static void searchAndStoreUser(){
        String search;
        AuthData authData = ref.getAuth();
        if (authData != null) {
            // If user if connected with Facebook, get his profil image
            if(authData.getProvider().equals("facebook")){
                user.connexionMode = "facebook";
                search = "id";
                // Get profil image
                String userId = authData.getProviderData().get("id").toString();
                getFbImageTask getTask = new getFbImageTask();
                getTask.execute(userId);
            }else{
                user.connexionMode = authData.getProvider();
                search = "email";
                // Put default image
                user.imageRessource = R.drawable.default_profil;

            }

            String userId = authData.getProviderData().get(search).toString();
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
                                Map<String, Object> userInfo = (Map<String, Object>)snapshot.getValue();
                                Set set = userInfo.keySet();
                                Iterator iter = set.iterator();
                                Map<String, Object> value = (Map<String, Object>)userInfo.get(iter.next());

                                for(DataSnapshot keys : snapshot.getChildren()){
                                    user.firebaseId = keys.getKey();
                                }

                                user.firstName =  value.get("firstName").toString();
                                user.lastName = value.get("lastName").toString();
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });

        }
    }

    /** @brief	Class that get FaceBook image task. */
    static class getFbImageTask extends AsyncTask<String, Void, Bitmap> {

        /**
        *@brief		Override onPostExecute and give the bitmap to the user.
        *
        *@param		bitmap	The FaceBook image in bitmap.
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            // Set image to the ImageView
            super.onPostExecute(bitmap);
            user.imageBitmap = bitmap;
        }

        /**
        *@brief		Executes the operation in background.
        *
        *@param		userIds	Variable arguments providing list of identifiers for the users.
        *
        *@return	The FaceBook image in bitmap.
         */
        @Override
        protected Bitmap doInBackground(String... userIds) {
            String userId = userIds[0];
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

    /**
    *@brief		Disconnects the user.
    *
    *@param		context	The context of the view.
     */
    public static void disconnect(Context context){
        AuthData authData = ref.getAuth();

        if (authData != null) {
            String message = "Disconnect from ";
            if (authData.getProvider().equals("facebook")) {
                Session session = Session.getActiveSession();
                if (session != null) {
                    if (!session.isClosed()) {
                        message += "Facebook & ";
                        session.closeAndClearTokenInformation();
                    }
                }
            }
            message += "Firebase";
            System.out.println(message);
            ref.unauth();

            // Redirect to login page
            Intent intent = new Intent(context, ConnectActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
    }

    /**
    *@brief		Connects the firebase with facebook.
    *
    *@param		session	The session.
    *@param		context	The context of the view.
     */
    public static void connectFirebaseWithFacebook(final Session session, final Context context){

        System.out.println("Logging in with Facebook");
        // Request user data
        Request.newMeRequest(session, new Request.GraphUserCallback() {

            @Override
            public void onCompleted(final GraphUser user, final Response response) {
                if (user != null) {

                    // Connect with Firebase
                    ref.authWithOAuthToken("facebook", session.getAccessToken(), new Firebase.AuthResultHandler() {
                        @Override
                        public void onAuthenticated(AuthData authData) {
                            // The Facebook user is now authenticated with Firebase
                            System.out.println("Logged with Firebase");

                            // Check if user is in database
                            final String id = (String) (authData.getProviderData().get("id"));
                            ref.child("/users/")
                                    .startAt(id)
                                    .endAt(id)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {

                                            boolean exists = (snapshot.getValue() != null);
                                            if (!exists) {
                                                // Create user
                                                Map<String, String> newUser = new HashMap<String, String>();
                                                newUser.put("id", user.getId());
                                                newUser.put("firstName", user.getFirstName());
                                                newUser.put("lastName", user.getLastName());
                                                newUser.put("email", user.getProperty("email").toString());

                                                Firebase newRef = ref.child("/users/").push();
                                                newRef.setValue(newUser, id);

                                                newUser.put("connexionMode", "facebook");
                                                newUser.put("firebaseId", newRef.getKey());

                                                storeUser(newUser);
                                            } else {
                                                System.out.println("User already exists : " + id);
                                                searchAndStoreUser();
                                            }

                                            // Change activity to home page
                                            Intent intent = new Intent(context, HomeActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            context.startActivity(intent);
                                        }

                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {
                                        }
                                    });
                        }

                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {
                            // There was an error
                            System.out.println("Logging with Firebase make an error");
                            System.out.println(firebaseError.getCode());
                            System.out.println(firebaseError.getMessage());
                        }
                    });
                }
            }
        }).executeAsync();
    }
}
