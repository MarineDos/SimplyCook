package simplycook.marinedos.com.simplycook.Utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ComparatorManager {
    private static User[] usersToCompare = new User[4];
    private static int usersToCompareNumber = 0;

    static public boolean addUser(User newUser, int index, Context context){
        boolean alreadyExist = false;
        for(int i = 0; i < 4; ++i){
            User user = usersToCompare[i];
            if(user != null){
                if(user.firstName.equals(newUser.firstName) && user.lastName.equals(newUser.lastName)){
                    alreadyExist = true;
                }
            }
        }

        if(!alreadyExist){
            usersToCompare[index] = newUser;
            ++usersToCompareNumber;
        }else{
            Toast toast = Toast.makeText(
                    context,
                    "Déjà dans le comparateur",
                    Toast.LENGTH_LONG
            );
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        return !alreadyExist;
    }

    static public int getUsersNumber(){
        return usersToCompareNumber;
    }

    static public User[] getUsers(){
        return usersToCompare;
    }
}
