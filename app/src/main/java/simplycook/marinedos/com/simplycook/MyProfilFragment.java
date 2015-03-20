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

    /**
     * @brief	Called to do initial creation of a fragment.
     *
     * @param	savedInstanceState	If the fragment is being re-created from a previous saved state,
     * 								this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * @brief	Function executed when the view is created. Called to have the fragment instantiate
     * 			its user interface view.
     *
     * @param	inflater		  	The LayoutInflater object that can be used to inflate any views
     * 								in the fragment.
     * @param	container		  	If non-null, this is the parent view that the fragment's UI
     * 								should be attached to. The fragment should not add the view
     * 								itself, but this can be used to generate the LayoutParams of the
     * 								view.
     * @param	savedInstanceState	If non-null, this fragment is being re-constructed from a
     * 								previous saved state as given here.
     *
     * @return	The View for the fragment's UI, or null.
     */
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
