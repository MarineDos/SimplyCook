package simplycook.marinedos.com.simplycook.Utils;

import java.util.ArrayList;

public class ComparatorItem {
	
    private User user;
    private ArrayList<Taste>[] comparatorList = new ArrayList[12];

    public ComparatorItem(User newUser){
        user = newUser;
    }

    public ArrayList<Taste>[] getComparatorList(){
        return comparatorList;
    }

    public User getUser(){
        return user;
    }
}
