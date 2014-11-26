package simplycook.marinedos.com.simplycook.Utils;

import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;


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
