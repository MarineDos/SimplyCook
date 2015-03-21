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
import java.util.Map;

import simplycook.marinedos.com.simplycook.R;

public class UsersManager {
	
    /** @brief	The reference for firebase. */
    private static final Firebase ref = new Firebase("https://simplycook.firebaseio.com");
    /** @brief	The users list. */
    private static List<User> users;
    /** @brief	The list adapter. */
    private static ArrayAdapter<User> listAdapter;

    /**
     * @brief	Updates the ArrayAdapter and the List of User passed in params. Debug function. Never
     * 			call anyway.
     *
     * @param	adapter  	The adapter to update.
     * @param	usersList	List of users to update.
     *
     * @return	A List of all user;
     */
    public static List<User> updateAllUsersList(final ArrayAdapter<User> adapter, final List<User> usersList) {
        // Copy informations provide by param
		users = usersList;
        listAdapter = adapter;
		// Get all the users from Firebase
        ref.child("/users").orderByChild("lastName").addListenerForSingleValueEvent(new ValueEventListener() {

			/**
			 * @brief	Executes the data change action.
			 *
			 * @param	dataSnapshot	The data snapshot.
			 */
			@Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
					// For each user in the database
                    for (DataSnapshot user : dataSnapshot.getChildren()) {
						// Create a new user
                        User newUser = new User();
						// Fill information with the data get from the database
                        newUser.firebaseId = user.getKey();
                        newUser.firstName = user.child("firstName").getValue(String.class);
                        newUser.lastName = user.child("lastName").getValue(String.class);
						// If it is a FB user
                        if (user.child("id").getValue() != null) {
                            newUser.connexionMode = "facebook";
							// Create and launch a task to get the user's profil image
                            getFbImageTaskAndNotify getTask = new getFbImageTaskAndNotify();
                            HashMap<String, User> userImage = new HashMap<String, User>();
                            userImage.put(user.child("id").getValue().toString(), newUser);
                            getTask.execute(userImage);
						// Not a FB user, get default image
                        } else {
                            newUser.connexionMode = "password";
                            newUser.imageRessource = R.drawable.default_profil;
                            usersList.add(newUser);
                        }
                    }

					// Notify that data are chander
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}

        });

        return users;
    }

    /** @brief	Task that get the facebook image of an user and notify after. */
    public static class getFbImageTaskAndNotify extends AsyncTask<HashMap<String, User>, Void, Bitmap> {
        /** @brief	Identifier for the user. */       
	    public String userId;
        /** @brief	The new user. */
        public User newUser;

		/**
         * @brief	Runs on the UI thread after doInBackground(Params...). 
         *
         * @param	bitmap	The result of the operation computed by doInBackground(Params...).
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            // Set image to the ImageView
            super.onPostExecute(bitmap);

            listAdapter.notifyDataSetChanged();
        }
		
		/**
         * @brief	This method performs a computation on a background thread. The specified parameters
         * 			are the parameters passed to execute(Params...) by the caller of this task.
         * 			Try to load a profil image from the facebook api.
         *
         * @param	params	The parameters of the task.
         *
         * @return	A Bitmap.
         */
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

    /**
     * @brief	Updates the user's profil.
     *
     * @param	userFirebaseId	Identifier of the user from firebase.
     * @param	nameView	  	The name view.
     * @param	imgView		  	The image view.
     */
    public static void updateProfil(String userFirebaseId, final TextView nameView, final ImageView imgView) {
		// Get the user in Firebase
        ref.child("/users/" + userFirebaseId).addListenerForSingleValueEvent(new ValueEventListener() {

            /**
             * @brief	Executes the data change action.
             *
             * @param	dataSnapshot	The data snapshot.
             */
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
					// Set first name and last name in the nameView
                    nameView.setText(dataSnapshot.child("firstName").getValue(String.class) + " " + dataSnapshot.child("lastName").getValue(String.class));
                    // If it's a FB user
					if (dataSnapshot.child("id").getValue() != null) {
						// Create and launch a FB Image task
                        getFbImageTask getTask = new getFbImageTask();
                        HashMap<String, ImageView> userImage = new HashMap<String, ImageView>();
                        userImage.put(dataSnapshot.child("id").getValue().toString(), imgView);
                        getTask.execute(userImage);
					// Else get the default image
                    } else {
                        imgView.setImageResource(R.drawable.default_profil);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

	/** @brief	Task that get the facebook image of an user. */
    public static class getFbImageTask extends AsyncTask<HashMap<String, ImageView>, Void, Bitmap> {
        /** @brief	Identifier for the user. */
	    public String userId;
        /** @brief	The image view. */
        public ImageView imgView;

		/**
         * @brief	Runs on the UI thread after doInBackground(Params...). 
         *
         * @param	bitmap	The result of the operation computed by doInBackground(Params...).
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            // Set image to the ImageView
            super.onPostExecute(bitmap);
            imgView.setImageBitmap(bitmap);
        }

		 /**
         * @brief	This method performs a computation on a background thread. The specified parameters
         * 			are the parameters passed to execute(Params...) by the caller of this task.
         * 			Try to load a profil image from the facebook api.
         *
         * @param	params	The parameters of the task.
         *
         * @return	A Bitmap.
         */
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

    /**
     * @brief	Add the user define by the firebaseId to the Favorite of the current user.
     *
     * @param	firebaseId	Identifier of the user in the firebase.
     * @param	favorisImg	The favoris image view.
     */
    public static void addFavoris(final String firebaseId, final ImageView favorisImg) {
		// Get the user from Firebase by it firebaseId
        ref.child("/users/" + ConnexionManager.user.firebaseId + "/favorites")
                .startAt(firebaseId)
                .endAt(firebaseId)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    /**
                     * @brief	Executes the data change action.
                     *
                     * @param	dataSnapshot	The data snapshot.
                     */
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() == null) {
                            // Add to favorite
                            // Set the new image
                            favorisImg.setImageResource(R.drawable.star_yellow);

                            Map<String, String> newFavoris = new HashMap<String, String>();
                            newFavoris.put("firebaseId", firebaseId);

							// Put the user in Firebase
                            Firebase newRef = ref.child("/users/" + ConnexionManager.user.firebaseId + "/favorites").push();
                            newRef.setValue(newFavoris, firebaseId);
                        } else {
                            // Delete from favorites
                            // Set the new image
                            favorisImg.setImageResource(R.drawable.star);
							// For each favorite, find the one to delete and delete it
                            for (DataSnapshot favorite : dataSnapshot.getChildren()) {
                                String key = favorite.getKey();
                                Firebase refFavorite = ref.child("/users/" + ConnexionManager.user.firebaseId + "/favorites/" + key);
                                refFavorite.removeValue();
                            }

                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {}
                });
    }

    /**
     * @brief	Updates the favoris image view if the user given by its id is in the current user favoris.
     *
     * @param	firebaseId	The user identifier from the firebase.
     * @param	favorisImg	The favoris image.
     */
    public static void updateIfFavoris(final String firebaseId, final ImageView favorisImg) {
		// Get the user from Firebase by it firebaseId
        ref.child("/users/" + ConnexionManager.user.firebaseId + "/favorites")
                .startAt(firebaseId)
                .endAt(firebaseId)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    /**
                     * @brief	Executes the data change action.
                     *
                     * @param	dataSnapshot	The data snapshot.
                     */
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            // Set the yellow star as image view because user is in favorite
                            favorisImg.setImageResource(R.drawable.star_yellow);
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {}
                });
    }

    public static List<User> updateAFavorisUsersList(final ArrayAdapter<User> adapter, final List<User> usersList) {
        users = usersList;
        listAdapter = adapter;
        ref.child("/users/" + ConnexionManager.user.firebaseId + "/favorites/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    // Get firebase id of favorites
                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                        final User newUser = new User();
                        newUser.firebaseId = user.child("firebaseId").getValue(String.class);
                        ref.child("/users/" + newUser.firebaseId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    newUser.firstName = dataSnapshot.child("firstName").getValue(String.class);
                                    newUser.lastName = dataSnapshot.child("lastName").getValue(String.class);
                                    if (dataSnapshot.child("id").getValue() != null) {
                                        newUser.connexionMode = "facebook";
                                        getFbImageTaskAndNotify getTask = new getFbImageTaskAndNotify();
                                        HashMap<String, User> userImage = new HashMap<String, User>();
                                        userImage.put(dataSnapshot.child("id").getValue().toString(), newUser);
                                        getTask.execute(userImage);

                                    } else {
                                        newUser.connexionMode = "password";
                                        newUser.imageRessource = R.drawable.default_profil;
                                        usersList.add(newUser);
                                    }

                                    adapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        return users;
    }

    public static void addMessageToUser(Taste taste, String category, String firebaseId){
        Map<String, String> newMessage = new HashMap<String, String>();
        newMessage.put("firstName", ConnexionManager.user.firstName);
        newMessage.put("lastName", ConnexionManager.user.lastName);
        newMessage.put("foodName", taste.getName());
        newMessage.put("category", category);
        newMessage.put("like", Integer.toString(taste.getLike()));
        newMessage.put("comment", taste.getComment());

        Firebase newRef = ref.child("/users/" + firebaseId + "/messages").push();

        newRef.setValue(newMessage, category);
    }
}