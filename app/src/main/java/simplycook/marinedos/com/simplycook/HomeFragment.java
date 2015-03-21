package simplycook.marinedos.com.simplycook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import simplycook.marinedos.com.simplycook.Utils.ConnexionManager;


/** @brief	The home fragment. Define the home page of application */
public class HomeFragment extends Fragment {
	
    /** @brief	The profil button. */
    private View profil_btn;
    /** @brief	The favoris button. */
    private View favoris_btn;
    /** @brief	The comparaison button. */
    private View comparaison_btn;
    /** @brief	The search button. */
    private View search_btn;

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
		// Create the root view
        View rootView =  inflater.inflate(R.layout.home_fragment, container, false);

		// Find the profil button in the view
        profil_btn = rootView.findViewById(R.id.profil_btn);
		// Add a function on click on it
        profil_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Go to profil Activity
                Intent intent = new Intent(getActivity(), MyProfilActivity.class);
                startActivity(intent);
            }
        });

		// Find the favoris button in the view
        favoris_btn = rootView.findViewById(R.id.favoris_btn);
		// Add a function on click on it
        favoris_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Go to profil Activity
                Intent intent = new Intent(getActivity(), FavorisActivity.class);
                startActivity(intent);
            }
        });
		
		// Find the comparaison button in the view
        comparaison_btn = rootView.findViewById(R.id.comparaison_btn);
		// Add a function on click on it
        comparaison_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Go to profil Activity
                Intent intent = new Intent(getActivity(), ComparaisonChoiceActivity.class);
                startActivity(intent);
            }
        });

		// Find the search button in the view
        search_btn = rootView.findViewById(R.id.search_btn);
		// Add a function on click on it
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

    /**
     * @brief	Initialize the contents of the Activity's standard options menu. 
     * 			You should place your menu items in to menu. 
     * 			For this method to be called, you must have first called setHasOptionsMenu(boolean) in the activity.
     *
     * @param	menu		The options menu in which you place your items.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * @brief	This hook is called whenever an item in your options menu is selected.
     *
     * @param	item	The menu item that was selected.
     *
     * @return	false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		// Get the id of item
        int id = item.getItemId();
        if (id == R.id.action_disconnect) {
            ConnexionManager.disconnect(getActivity());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
