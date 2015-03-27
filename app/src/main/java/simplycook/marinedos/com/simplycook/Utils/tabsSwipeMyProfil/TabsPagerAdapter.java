package simplycook.marinedos.com.simplycook.Utils.tabsSwipeMyProfil;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/** @brief	Class that manage the tabs pager. */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    /**
    *@brief	Constructor.
    *
    *@param	fm	The fragment manager.
     */
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    /**
    *@brief		Gets an item.
    *
    *@param		index	the index of the item.
    *
    *@return	The item.
     */
    @Override
    public Fragment getItem(int index) {
        switch (index) {
            case 0:
                return new myTasteFragment();
            case 1:
                return new manageTasteFragment();
            case 2:
                return new myFavoriteFragment();
            case 3:
                return new messagesFragment();
        }
        return null;
    }

    /**
    *@brief		Gets the items count  - equal to number of tabs.
    *
    *@return	The items count.
     */
    public int getCount() {
        return 4;
    }
}