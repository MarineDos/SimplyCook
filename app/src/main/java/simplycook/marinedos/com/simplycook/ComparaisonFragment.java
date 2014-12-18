package simplycook.marinedos.com.simplycook;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class ComparaisonFragment  extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.comparaison_fragment, container, false);

        // on clic on a add button, display choice (from favoris or from research)
        LinearLayout userAdb_btn = (LinearLayout) root.findViewById(R.id.user1);
        userAdb_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.choose_adding_comparation)
                        .setItems(R.array.list_add_from_comparaison, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 0){
                                    // Add from favoris
                                    Intent intent = new Intent(getActivity(), ComparaisonAddFromFavoris.class);
                                    startActivity(intent);
                                }else{
                                    // Add from search
                                }
                            }
                        });

                // Create the AlertDialog object and show it
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return root;
    }
}
