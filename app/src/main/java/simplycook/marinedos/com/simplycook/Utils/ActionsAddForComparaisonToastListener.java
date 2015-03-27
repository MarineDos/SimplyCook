package simplycook.marinedos.com.simplycook.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;

import simplycook.marinedos.com.simplycook.ComparaisonAddFromFavoris;
import simplycook.marinedos.com.simplycook.ComparaisonAddFromSearch;
import simplycook.marinedos.com.simplycook.ComparaisonChoiceActivity;
import simplycook.marinedos.com.simplycook.ComparaisonChoiceFragment;
import simplycook.marinedos.com.simplycook.R;

/** @brief	Class that manage addition for comparaison. */
public class ActionsAddForComparaisonToastListener implements View.OnClickListener {
    /** @brief	The index. */
    private int mIndex;
    /** @brief	The context of the view */
    private Context mContext;

    /**
    *@brief		Constructor.
    *
    *@param		index  	The mIndex.
    *@param		context	The mContext.
     */
    public ActionsAddForComparaisonToastListener(int index, Context context){
        mIndex = index;
        mContext = context;
    }

    /**
    *@brief		Executes the click action.
    *
    *@param		v	The View.
     */
    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.choose_action_comparation)
                .setItems(R.array.list_add_from_comparaison, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            // Add from favoris
                            Intent intent = new Intent(mContext, ComparaisonAddFromFavoris.class);
                            intent.putExtra("index", mIndex);
                            mContext.startActivity(intent);
                        }else{
                            if(which == 1)
                            {
                                // Add from search
                                Intent intent = new Intent(mContext, ComparaisonAddFromSearch.class);
                                intent.putExtra("index", mIndex);
                                mContext.startActivity(intent);
                            }
                            else
                            {
                                ComparatorManager.removeUser(mIndex);
                                ComparaisonChoiceActivity activity = (ComparaisonChoiceActivity)mContext;
                                ComparaisonChoiceFragment fragment = (ComparaisonChoiceFragment)(activity.getSupportFragmentManager().findFragmentById(R.id.container));
                                fragment.updateList();
                            }
                        }
                    }
                });

        // Create the AlertDialog object and show it
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
