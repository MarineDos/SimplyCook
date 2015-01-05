package simplycook.marinedos.com.simplycook.Utils.Service;


import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import simplycook.marinedos.com.simplycook.ConnectActivity;
import simplycook.marinedos.com.simplycook.R;

public class SuggestNotificationService extends IntentService{

    public SuggestNotificationService() {
        super("SuggestNotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // Programm next call to ths service
        Intent serviceIntent = new Intent(this, SuggestNotificationService.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 60 * 1000 * 100 * 2, PendingIntent.getService(this, 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        System.out.println("Refresh notification");

        Firebase.setAndroidContext(this);
        final Firebase ref = new Firebase("https://simplycook.firebaseio.com");

        final AuthData authData = ref.getAuth();

        if (authData != null) {

            System.out.println("Serach with searched id");
            String search;
            // If user if connected with Facebook
            if (authData.getProvider().equals("facebook")) {
                search = "id";
            } else {
                search = "email";
            }

            String userId = authData.getProviderData().get(search).toString();
            // Search in firebase data to get firebaseId of the user
            ref.child("/users/")
                    .startAt(userId)
                    .endAt(userId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            boolean exists = (snapshot.getValue() != null);
                            if (exists) {
                                for (DataSnapshot keys : snapshot.getChildren()) {
                                    FirebaseIdSaver.firebaseId = keys.getKey();
                                }

                                searchForEntries();
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });

        } else if(FirebaseIdSaver.firebaseId != null) {
            System.out.println("Serach with saved id");
            searchForEntries();
        }

    }

    private void searchForEntries(){
        final Context context = this;
        final Firebase ref = new Firebase("https://simplycook.firebaseio.com");
        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Search if there are entry in messages
        ref.child("users/" + FirebaseIdSaver.firebaseId + "/messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            long nbMessage = dataSnapshot.getChildrenCount();

                            if (nbMessage > 0) {
                                // There is unread messages
                                String title;
                                String message;
                                if (nbMessage == 1) {
                                    title = "Vous avez un nouveau message";
                                    message = nbMessage + " suggestion non traitée";
                                } else {
                                    title = "Vous avez des nouveaux messages";
                                    message = nbMessage + " suggestions non traitées";
                                }

                                // Construct notification
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                                        .setSmallIcon(R.drawable.ic_launcher)
                                        .setContentTitle(title)
                                        .setContentText(message)
                                        .setAutoCancel(true);

                                Intent resultIntent = new Intent(context, ConnectActivity.class);
                                // Ensures that navigating backward from the Activity leads out of your application to the Home screen.
                                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                                // Adds the back stack for the Intent (but not the Intent itself)
                                stackBuilder.addParentStack(ConnectActivity.class);
                                // Adds the Intent that starts the Activity to the top of the stack
                                stackBuilder.addNextIntent(resultIntent);
                                PendingIntent resultPendingIntent =
                                        stackBuilder.getPendingIntent(
                                                0,
                                                PendingIntent.FLAG_UPDATE_CURRENT
                                        );

                                builder.setContentIntent(resultPendingIntent);
                                notificationManager.notify(12, builder.build());

                            }
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });
    }
}
