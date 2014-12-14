package simplycook.marinedos.com.simplycook.Utils.tabsSwipeUserProfil;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import simplycook.marinedos.com.simplycook.ProfilActivity;
import simplycook.marinedos.com.simplycook.Utils.ExpandableListAdapter;
import simplycook.marinedos.com.simplycook.R;
import simplycook.marinedos.com.simplycook.Utils.Taste;
import simplycook.marinedos.com.simplycook.Utils.TasteManager;
import simplycook.marinedos.com.simplycook.Utils.UsersManager;

public class TasteFragment extends Fragment {
    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, List<Taste>> listDataChild;
    private String userFirebaseId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.profil_taste_tab, container, false);

        // Get user id
        userFirebaseId = ((ProfilActivity)getActivity()).userFirebaseId;
        // List
        expListView = (ExpandableListView) rootView.findViewById(R.id.expandableListView_food);
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Taste selectedTaste = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);

                if(!selectedTaste.getComment().equals("")){
                    Toast toast = Toast.makeText(
                            getActivity(),
                            selectedTaste.getName() + " : " + selectedTaste.getComment(),
                            Toast.LENGTH_LONG
                    );
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                }
                return false;
            }
        });

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<Taste>>();
        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);

        prepareListData();
        //prepareListData_debug();

        // Profil
        TextView nameView = (TextView) rootView.findViewById(R.id.profil_name);
        final ImageView imgView = (ImageView) rootView.findViewById(R.id.profil_img);
        UsersManager.updateProfil(userFirebaseId, nameView, imgView);

        // add to favorite
        final ImageView favorisImg = (ImageView) rootView.findViewById(R.id.favoris_img);
        UsersManager.updateIfFavoris(userFirebaseId, favorisImg);
        favorisImg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                UsersManager.addFavoris(userFirebaseId, favorisImg);
                return false;
            }
        });


        return rootView;
    }

    private void prepareListData()
    {
        TasteManager.updateFoodList(userFirebaseId,listDataHeader, listDataChild, listAdapter );
    }

    // Debug
    private void prepareListData_debug() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<Taste>>();

        // Adding child data
        listDataHeader.add("Viande");
        listDataHeader.add("Poisson");
        listDataHeader.add("Légume");

        // Adding child data
        List<Taste> viande = new ArrayList<Taste>();
        viande.add(new Taste("Boeuf", 1, "Surtout en sauce"));
        viande.add(new Taste("Agneau", -1, ""));
        viande.add(new Taste("Cheval", 0, ""));
        viande.add(new Taste("Poulet", 1, ""));

        List<Taste> poisson = new ArrayList<Taste>();
        poisson.add(new Taste("Cabillaud", 1, ""));
        poisson.add(new Taste("Saumon", 0, "Que le saumon fumé"));
        poisson.add(new Taste("Bar", 0, "Je me souviens plus..."));

        List<Taste> legume = new ArrayList<Taste>();
        legume.add(new Taste("Carotte", 1, "C'est pour ça que je suis aimable"));
        legume.add(new Taste("Courgette", 1, ""));

        listDataChild.put(listDataHeader.get(0), viande);
        listDataChild.put(listDataHeader.get(1), poisson);
        listDataChild.put(listDataHeader.get(2), legume);
    }
}

