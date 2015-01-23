package simplycook.marinedos.com.simplycook.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;

import simplycook.marinedos.com.simplycook.ComparaisonAddFromFavoris;
import simplycook.marinedos.com.simplycook.ComparaisonAddFromSearch;
import simplycook.marinedos.com.simplycook.R;

public class FavorisListener implements View.OnClickListener {
    private int mIndex;
    private Context mContext;

    public FavorisListener(int index, Context context){
        mIndex = index;
        mContext = context;
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.choose_adding_comparation)
                .setItems(R.array.list_add_from_comparaison, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            // Add from favoris
                            Intent intent = new Intent(mContext, ComparaisonAddFromFavoris.class);
                            intent.putExtra("index", mIndex);
                            mContext.startActivity(intent);
                        }else{
                            // Add from search
                            Intent intent = new Intent(mContext, ComparaisonAddFromSearch.class);
                            intent.putExtra("index", mIndex);
                            mContext.startActivity(intent);
                        }
                    }
                });

        // Create the AlertDialog object and show it
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
