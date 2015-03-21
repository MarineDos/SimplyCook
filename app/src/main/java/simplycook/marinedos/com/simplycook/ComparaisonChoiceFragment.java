package simplycook.marinedos.com.simplycook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import simplycook.marinedos.com.simplycook.Utils.ComparatorManager;
import simplycook.marinedos.com.simplycook.Utils.ActionsAddForComparaisonToastListener;
import simplycook.marinedos.com.simplycook.Utils.User;

/** @brief	The comparaison choice fragment. Allow to choose user for taste comparaison */
public class ComparaisonChoiceFragment extends Fragment {

    /** @brief	Array of button to add user for comparaison. */
    private LinearLayout userAdd_bt[] = new LinearLayout[4];
    /** @brief	The comparaison submit button. */
    private Button comparaison_btn;
	
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
     * @brief Called when the fragment is visible to the user and actively running.
     */
    @Override
    public void onResume() {
        super.onResume();
        updateList();
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
        View root = inflater.inflate(R.layout.comparaison_choice_fragment, container, false);

        // Get all buttons for adding user to compare
        userAdd_bt[0] = (LinearLayout) root.findViewById(R.id.user1);
        userAdd_bt[1] = (LinearLayout) root.findViewById(R.id.user2);
        userAdd_bt[2] = (LinearLayout) root.findViewById(R.id.user3);
        userAdd_bt[3] = (LinearLayout) root.findViewById(R.id.user4);

        // Add listener on buttons
        for(int i = 0; i < 4; ++i){
            userAdd_bt[i].setOnClickListener(new ActionsAddForComparaisonToastListener(i, getActivity()));
        }

		// Get the comparaison submit button
        comparaison_btn = (Button) root.findViewById(R.id.compare_btn);
		// Add listener on it
        comparaison_btn.setOnClickListener(new View.OnClickListener() {

		    /**
		     * @brief	Executes the click action.
		     *
		     * @param	v	The View to process.
		     */
		    @Override
            public void onClick(View v) {
				// Create and start the ComparaisonActivity
                Intent intent = new Intent(getActivity(), ComparaisonActivity.class);
                getActivity().startActivity(intent);
            }
        });

        // Populate the list with user already selected
        updateList();

        return root;
    }

    /** @brief	Updates the list. */
    public void updateList(){
        // Populate the list with user already selected
        View root = getView();

		// Get users already selected for comparaison
        User[] usersToCompare = ComparatorManager.getUsers();
		// For each user
        for(int i = 0; i < usersToCompare.length; ++i){
            User user = usersToCompare[i];

            //Get the user add button
            LinearLayout userAdd = userAdd_bt[i];
            ImageView userAdd_img = (ImageView)userAdd.getChildAt(0);
            if(user != null){
				// Remplace the user button image by the user profil's image

                if(user.connexionMode.equals("facebook")){
                    userAdd_img.setImageBitmap(user.imageBitmap);
                }else{
                    userAdd_img.setImageResource(user.imageRessource);
                }

				// Set the name of the button with the user's name
                String name = user.firstName + " " + user.lastName;
                TextView nameView = (TextView)userAdd.getChildAt(1);
                nameView.setText(name);
                nameView.setVisibility(View.VISIBLE);
            }
            else
            {
                userAdd_img.setImageResource(R.drawable.add);
            }
        }

		// If more than one user is selected set the visibility of the submit comparaison visible, else not
        if(ComparatorManager.getUsersNumber()<2){
            comparaison_btn.setVisibility(View.GONE);
        }else{
            comparaison_btn.setVisibility(View.VISIBLE);
        }
    }
}
