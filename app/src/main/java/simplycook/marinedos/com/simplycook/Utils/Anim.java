package simplycook.marinedos.com.simplycook.Utils;

import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;


/** @brief	Class that manage a simple animation to display / hide content. */
public class Anim {

    /**
     * @brief	Hide the given View.
     *
     * @param	context	The context of the View.
     * @param	view   	The view to hide.
     */
    public static void hide(Context context, final View view) {

        if (view.getVisibility() != View.GONE){
            view.setAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out));
            view.setVisibility(View.GONE);
        }
    }

    /**
     * @brief	Show the given View.
     *
     * @param	context	The context of the given View.
     * @param	view   	The view to show.
     */
    public static void show(Context context, final View view) {

        if (view.getVisibility() != View.VISIBLE){
            view.setAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));
            view.setVisibility(View.VISIBLE);
        }
    }
}
