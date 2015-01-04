package simplycook.marinedos.com.simplycook.Utils.tabsSwipeMyProfil;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }
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
    public int getCount() {
        // get item count - equal to number of tabs
        return 4;
    }
}