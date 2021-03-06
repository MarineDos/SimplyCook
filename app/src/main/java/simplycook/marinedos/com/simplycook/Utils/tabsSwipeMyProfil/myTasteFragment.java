package simplycook.marinedos.com.simplycook.Utils.tabsSwipeMyProfil;

import simplycook.marinedos.com.simplycook.Utils.DeviceInformation;
import simplycook.marinedos.com.simplycook.Utils.ExpandableListAdapter;
import simplycook.marinedos.com.simplycook.R;
import simplycook.marinedos.com.simplycook.Utils.ConnexionManager;
import simplycook.marinedos.com.simplycook.Utils.Taste;
import simplycook.marinedos.com.simplycook.Utils.TasteManager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** @brief	Class that manage the taste fragment. */
public class myTasteFragment extends Fragment {
    /** @brief	The list adapter. */
    private ExpandableListAdapter listAdapter;
    /** @brief	The exponent list view. */
    private ExpandableListView expListView;
    /** @brief	The list data header. */
    private List<String> listDataHeader;
    /** @brief	The list data child. */
    private HashMap<String, List<Taste>> listDataChild;
    /** @brief	The loader control. */
    private ProgressBar loader;

	/**
    *@brief		Executes when create a view.
    *
    *@param		inflater		  	The inflater.
    *@param		container		  	The container.
    *@param		savedInstanceState	State of the saved instance.
    *
    *@return	The View.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.profil_mytaste_tab, container, false);

        // Profil
        TextView name = (TextView) rootView.findViewById(R.id.profil_name);
        name.setText(ConnexionManager.user.firstName + " " + ConnexionManager.user.lastName);
        ImageView img = (ImageView) rootView.findViewById(R.id.profil_img);
        if (ConnexionManager.user.connexionMode.equals("facebook")) {
            img.setImageBitmap(ConnexionManager.user.imageBitmap);
        } else {
            img.setImageResource(ConnexionManager.user.imageRessource);
        }

        // List
        loader = (ProgressBar) rootView.findViewById(R.id.loader);
        expListView = (ExpandableListView) rootView.findViewById(R.id.expandableListView_food);
        if (!DeviceInformation.isTablet(getActivity())){
            expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    Taste selectedTaste = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);

                    if (!selectedTaste.getComment().equals("")) {
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
        }

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<Taste>>();
        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);

        prepareListData();
        //prepareListData_debug();


        return rootView;
    }

    /** @brief	Prepare list data. */
    private void prepareListData()
    {
        TasteManager.updateFoodList(getActivity(), ConnexionManager.user.firebaseId,listDataHeader, listDataChild, listAdapter, expListView, loader, DeviceInformation.isTablet(getActivity()));
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

