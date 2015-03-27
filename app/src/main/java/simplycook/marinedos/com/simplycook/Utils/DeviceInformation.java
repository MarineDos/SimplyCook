package simplycook.marinedos.com.simplycook.Utils;


import android.content.Context;
import android.content.res.Configuration;

/** @brief	Class that manage the information about the screen device.  */
public class DeviceInformation {

    /**
    *@brief		Query if the device is tablet.
    *
    *@param		context	The context of the view.
    *
    *@return	true if tablet, false if not.
     */
    static public boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }
}
