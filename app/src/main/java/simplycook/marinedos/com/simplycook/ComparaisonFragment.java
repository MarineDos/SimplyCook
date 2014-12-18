package simplycook.marinedos.com.simplycook;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import simplycook.marinedos.com.simplycook.Utils.ComparatorManager;
import simplycook.marinedos.com.simplycook.Utils.FavorisListener;
import simplycook.marinedos.com.simplycook.Utils.User;

public class ComparaisonFragment  extends Fragment {

    private LinearLayout userAdd_bt[] = new LinearLayout[4];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.comparaison_fragment, container, false);

        // get all buttons
        userAdd_bt[0] = (LinearLayout) root.findViewById(R.id.user1);
        userAdd_bt[1] = (LinearLayout) root.findViewById(R.id.user2);
        userAdd_bt[2] = (LinearLayout) root.findViewById(R.id.user3);
        userAdd_bt[3] = (LinearLayout) root.findViewById(R.id.user4);

        // Add listener on button
        for(int i = 0; i < 4; ++i){
            userAdd_bt[i].setOnClickListener(new FavorisListener(i, getActivity()));
        }

        // Populate favoris
        updateList();

        return root;
    }

    public void updateList(){
        // Populate favoris
        View root = getView();
        User[] usersToCompare = ComparatorManager.getUsers();
        for(int i = 0; i < usersToCompare.length; ++i){
            if(usersToCompare[i] != null){
                User user = usersToCompare[i];
                LinearLayout userAdd = userAdd_bt[i];
                ImageView userAdd_img = (ImageView)userAdd.getChildAt(0);

                if(user.connexionMode.equals("facebook")){
                    userAdd_img.setImageBitmap(user.imageBitmap);
                }else{
                    userAdd_img.setImageResource(user.imageRessource);
                }

                String name = user.firstName + " " + user.lastName;
                TextView nameView = (TextView)userAdd.getChildAt(1);
                nameView.setText(name);
                nameView.setVisibility(View.VISIBLE);
            }
        }
    }
}
