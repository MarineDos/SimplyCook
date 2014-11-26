package simplycook.marinedos.com.simplycook.Utils;


import android.content.Context;
import android.content.Intent;

import com.facebook.Session;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import simplycook.marinedos.com.simplycook.ConnectActivity;

public class ConnexionManager {

    private static final Firebase ref = new Firebase("https://simplycook.firebaseio.com");

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
            context.startActivity(intent);
        }
    }
}
