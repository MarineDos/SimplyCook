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

public class ComparaisonAddFromFavorisFragment extends ListFragment {

    private ListView mListView;
    ArrayAdapter<User> mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.comparaison_add_from_favoris_fragment, container, false);
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

        boolean succeed = ComparatorManager.addUser(user, ((ComparaisonAddFromFavoris)getActivity()).index, getActivity());

        if(succeed){
            getActivity().finish();
        }
    }
}
