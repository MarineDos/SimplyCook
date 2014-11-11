package simplycook.marinedos.com.simplycook;

import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;

/**
 * Created by Marine on 11/11/2014.
 */
public class Anim {

    public static void hide(Context context, final View view) {

        if (view.getVisibility() != View.GONE){
            view.setAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out));
            view.setVisibility(View.GONE);
        }
    }

    public static void show(Context context, final View view) {

        if (view.getVisibility() != View.VISIBLE){
            view.setAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));
            view.setVisibility(View.VISIBLE);
        }
    }
}
