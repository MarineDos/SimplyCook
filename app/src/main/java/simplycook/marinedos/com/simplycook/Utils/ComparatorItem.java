package simplycook.marinedos.com.simplycook.Utils;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Marine on 03/01/2015.
 */
public class ComparatorItem {
    private User user;
    private ArrayList<Taste>[] comparatorList;

    public ComparatorItem(User newUser){
        user = newUser;
        comparatorList = new ArrayList[12];
    }

    public ArrayList<Taste>[] getComparatorList(){
        return comparatorList;
    }

    public User getUser(){
        return user;
    }
}