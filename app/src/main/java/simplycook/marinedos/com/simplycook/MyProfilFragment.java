package simplycook.marinedos.com.simplycook;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import simplycook.marinedos.com.simplycook.Utils.ConnexionManager;

public class MyProfilFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.profil_fragment, container, false);

        // Profil
        TextView name = (TextView) view.findViewById(R.id.profil_name);
        name.setText(ConnexionManager.user.firstName + " " + ConnexionManager.user.lastName);
        ImageView img = (ImageView) view.findViewById(R.id.profil_img);
        if(ConnexionManager.user.connexionMode.equals("facebook")){
            img.setImageBitmap(ConnexionManager.user.imageBitmap);
        }else{
            img.setImageResource(ConnexionManager.user.imageRessource);
        }

        return view;
    }
}
