package simplycook.marinedos.com.simplycook.Utils;

import java.util.ArrayList;
import java.util.List;

public class ComparatorManager {
    private static User[] usersToCompare = new User[4];

    static public void addUser(User newUser, int index){
        usersToCompare[index] = newUser;
    }

    static public User[] getUsers(){
        return usersToCompare;
    }
}
