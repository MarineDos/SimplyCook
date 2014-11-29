package simplycook.marinedos.com.simplycook.Utils.tabsswipe;

import simplycook.marinedos.com.simplycook.ExpandableListAdapter;
import simplycook.marinedos.com.simplycook.R;
import simplycook.marinedos.com.simplycook.Utils.Taste;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class myTasteFragment extends Fragment {
    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, List<Taste>> listDataChild;
    private final Firebase ref = new Firebase("https://simplycook.firebaseio.com");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.profil_taste_tab, container, false);


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

        prepareListData();
        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);

        return rootView;
    }
    private void prepareListData() {
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

