package simplycook.marinedos.com.simplycook.Utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * @brief	A class that manage an array of user for comparaison. Maximum 4 users can be add to
 * 			this manager for comparaison. Comparaison is made in fragment / activity.
 */
public class ComparatorManager {
	
    /** @brief	The users to compare. Maximum 4 users */
    private static User[] usersToCompare = new User[4];
    /** @brief	The number of user actually in the array of users. */
    private static int usersToCompareNumber = 0;

    /**
     * @brief	Add a user in the array of users for comparaison.
     *
     * @param	newUser	The user.
     * @param	index  	The indice of the array where you want to add the player.
     * @param	context	The context.
     *
     * @return	true if it succeeds, false if it fails.
     */
    static public boolean addUser(User newUser, int index, Context context){
        boolean alreadyExist = false;

		// Test if user is already in the array. Avoid double user in array.
        for(int i = 0; i < 4; ++i){
            User user = usersToCompare[i];
            if(user != null){
                if(user.firstName.equals(newUser.firstName) && user.lastName.equals(newUser.lastName)){
                    alreadyExist = true;
                }
            }
        }

		// If user don't exit, add it in the array of user
        if(!alreadyExist){
            usersToCompare[index] = newUser;
			// Don't forget to increment the number of user to compare
            ++usersToCompareNumber;
        }else{
		// Else make a toast to advertise and show it
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

    /**
     * @brief	Gets the number of users in the array.
     *
     * @return	The number of users in the array.
     */
    static public int getUsersNumber(){
        return usersToCompareNumber;
    }

    /**
     * @brief	Gets the array of users.
     *
     * @return	An array of user.
     */
    static public User[] getUsers(){
        return usersToCompare;
    }
}
