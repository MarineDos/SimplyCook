package simplycook.marinedos.com.simplycook.Utils;

import java.util.ArrayList;
import java.util.List;

public class ComparatorManager {
    private static List<User> usersToCompare = new ArrayList<User>();

    static public void addUser(User newUser){
        usersToCompare.add(newUser);
    }
}
