package simplycook.marinedos.com.simplycook.Utils.tabsSwipeMyProfil;

import android.support.v4.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import simplycook.marinedos.com.simplycook.ProfilActivity;
import simplycook.marinedos.com.simplycook.R;
import simplycook.marinedos.com.simplycook.Utils.FavorisArrayAdapter;
import simplycook.marinedos.com.simplycook.Utils.User;
import simplycook.marinedos.com.simplycook.Utils.UsersManager;

/** @brief	Class that manage the favorite fragment. */
public class myFavoriteFragment extends ListFragment {

    /** @brief	The list view control. */
    private ListView mListView;
    /** @brief	The adapter. */
    private ArrayAdapter<User> mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.favoris_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        ProgressBar loader = (ProgressBar) getView().findViewById(R.id.loader);

        List<User> users = new ArrayList<User>();

        mListView = getListView();
        mAdapter = new FavorisArrayAdapter(getActivity(), R.layout.favoris_list_item, users);
        UsersManager.updateAFavorisUsersList(getActivity(), mAdapter, users, mListView, loader);

        mListView.setAdapter(mAdapter);
    }

    public void onListItemClick(ListView l, View v, int pos, long id) {
        super.onListItemClick(l, v, pos, id);
        User user = mAdapter.getItem(pos);

        Intent intent = new Intent(getActivity(), ProfilActivity.class);
        intent.putExtra("firebaseId", user.firebaseId);
        getActivity().startActivity(intent);


    }
}
