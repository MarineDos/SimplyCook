package simplycook.marinedos.com.simplycook.Utils.tabsswipe;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import simplycook.marinedos.com.simplycook.R;

public class myFavoriteFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.profil_favorite_tab, container, false);

        return rootView;
    }
}
