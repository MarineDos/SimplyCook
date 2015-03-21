package simplycook.marinedos.com.simplycook;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import simplycook.marinedos.com.simplycook.Utils.ComparatorManager;
import simplycook.marinedos.com.simplycook.Utils.FavorisArrayAdapter;
import simplycook.marinedos.com.simplycook.Utils.User;
import simplycook.marinedos.com.simplycook.Utils.UsersManager;

/**
 * @brief	The fragment that manage the search of user that are in the favoris of the current
 * 			user for adding it in the comparaison choice fragment.
 */
public class ComparaisonAddFromFavorisFragment extends ListFragment {

    /** @brief	The list view. */
    private ListView mListView;
    /** @brief	The adapter for the list view. */
    ArrayAdapter<User> mAdapter;
	
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
        return inflater.inflate(R.layout.comparaison_add_from_favoris_fragment, container, false);
    }

	/**
     * @brief	Called when the fragment's activity has been created and this fragment's view hierarchy instantiated.
     *
     * @param	savedInstanceState	If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        ProgressBar loader = (ProgressBar) getView().findViewById(R.id.loader);
		// Create a list with all users that are in the favoris of the current user

        List<User> users = new ArrayList<User>();
        mListView = getListView();
        mAdapter = new FavorisArrayAdapter(getActivity(), R.layout.favoris_list_item, users);

        UsersManager.updateAFavorisUsersList(getActivity(), mAdapter, users, mListView, loader);
		
		// Populate the list view with these datas
        mListView.setAdapter(mAdapter);
    }

	/**
     * @brief	Called when an item of the list is clicked.
     *
     * @param	l  	The list View.
     * @param	v  	The View to process.
     * @param	pos	The position in the list.
     * @param	id 	The identifier of the item.
     */
    public void onListItemClick(ListView l, View v, int pos, long id) {
        super.onListItemClick(l, v, pos, id);

		// Get the user clicked
        User user = mAdapter.getItem(pos);
	
		// Add this user to the comparator manager
        boolean succeed = ComparatorManager.addUser(user, ((ComparaisonAddFromFavoris)getActivity()).index, getActivity());

        if(succeed){
            getActivity().finish();
        }
    }
}
