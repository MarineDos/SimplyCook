package simplycook.marinedos.com.simplycook.Utils.tabsSwipeUserProfil;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/** @brief	Class that manage the tabs pager adapter. */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    /**
    *@brief		Constructor.
    *
    *@param		fm	The fragment manager.
     */
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    /**
    *@brief		Gets an item.
    *
    *@param		index	The index of the item.
    *
    *@return	The item.
     */
    @Override
    public Fragment getItem(int index) {
        switch (index) {
            case 0:
                return new TasteFragment();
            case 1:
                return new SuggestTasteFragment();
        }
        return null;
    }

    /**
    *@brief		Gets the item count - equal to number of tabs.
    *
    *@return	The item count.
     */
    public int getCount() {
        return 2;
    }
}