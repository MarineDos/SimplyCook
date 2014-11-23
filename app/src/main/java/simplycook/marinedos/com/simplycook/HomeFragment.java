package simplycook.marinedos.com.simplycook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeFragment extends Fragment {

    private View profil_btn;
    private View favoris_btn;
    private View comparaison_btn;
    private View search_btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView =  inflater.inflate(R.layout.home_fragment, container, false);

        profil_btn = rootView.findViewById(R.id.profil_btn);
        profil_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Go to profil Activity
                Intent intent = new Intent(getActivity(), ProfilActivity.class);
                startActivity(intent);
            }
        });

        favoris_btn = rootView.findViewById(R.id.favoris_btn);
        favoris_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Go to profil Activity
                Intent intent = new Intent(getActivity(), FavorisActivity.class);
                startActivity(intent);
            }
        });

        comparaison_btn = rootView.findViewById(R.id.comparaison_btn);
        comparaison_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Go to profil Activity
                Intent intent = new Intent(getActivity(), ComparaisonActivity.class);
                startActivity(intent);
            }
        });

        search_btn = rootView.findViewById(R.id.search_btn);
        search_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Go to profil Activity
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

}
