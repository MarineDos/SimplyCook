package simplycook.marinedos.com.simplycook;

import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import simplycook.marinedos.com.simplycook.Utils.FavorisArrayAdapter;
import simplycook.marinedos.com.simplycook.Utils.User;
import simplycook.marinedos.com.simplycook.Utils.UsersManager;

public class FavorisFragment extends ListFragment {

    private ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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

        List<User> users = new ArrayList<User>();
        ArrayAdapter<User> adapter = new FavorisArrayAdapter(getActivity(), R.layout.favoris_list_item, users);
        UsersManager.updateAllUsersList(adapter, users);

        mListView = getListView();
        mListView.setAdapter(adapter);
    }
}
