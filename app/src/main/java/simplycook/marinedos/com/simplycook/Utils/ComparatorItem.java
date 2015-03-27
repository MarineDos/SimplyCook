package simplycook.marinedos.com.simplycook.Utils;

import java.util.ArrayList;

/** @brief	Class that manage the comparison of the tastes. */
public class ComparatorItem {
	
    /** @brief	The user. */
    private User user;
    /** @brief	List of tastes. */
    private ArrayList<Taste>[] comparatorList = new ArrayList[12];

    /**
    *@brief		Constructor.
    *
    *@param		newUser	The user.
     */
    public ComparatorItem(User newUser){
        user = newUser;
    }

    /**
    *@brief		Gets comparator list.
    *
    *@return	An array list of tastes.
     */
    public ArrayList<Taste>[] getComparatorList(){
        return comparatorList;
    }

    /**
    *@brief		Gets the user.
    *
    *@return	The user.
     */
    public User getUser(){
        return user;
    }
}
