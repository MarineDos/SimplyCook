package simplycook.marinedos.com.simplycook.Utils.tabsSwipeUserProfil;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import simplycook.marinedos.com.simplycook.ProfilActivity;
import simplycook.marinedos.com.simplycook.R;
import simplycook.marinedos.com.simplycook.Utils.Taste;
import simplycook.marinedos.com.simplycook.Utils.UsersManager;

/** @brief	Class tgat manage the suggestion of taste fragment. */
public class SuggestTasteFragment extends Fragment {

	/** @brief	The reference for firebase. */
    private final Firebase ref = new Firebase("https://simplycook.firebaseio.com");

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

        final View rootView = inflater.inflate(R.layout.profil_suggest_tab, container, false);

        final Spinner spinnerFood= (Spinner) rootView.findViewById(R.id.food_spinner);

        // Populate catégorie
        final Spinner spinnerCategory= (Spinner) rootView.findViewById(R.id.category_spinner);
        // Get All categories
        ref.child("/category").orderByChild("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> tab = new ArrayList<String>();

                for(DataSnapshot category : dataSnapshot.getChildren()){
                    tab.add(category.child("name").getValue(String.class));
                }
                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, tab);
                // Apply the adapter to the spinner
                spinnerCategory.setAdapter(adapter);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                String selectedCategory = spinnerCategory.getSelectedItem().toString();
                ref.child("/food/" + selectedCategory).orderByChild("name").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<String> tab = new ArrayList<String>();

                        for(DataSnapshot food : dataSnapshot.getChildren()){
                            tab.add(food.child("name").getValue(String.class));
                        }
                        // Create an ArrayAdapter using the string array and a default spinner layout
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, tab);
                        // Apply the adapter to the spinner
                        spinnerFood.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        final Button suggestButton = (Button) rootView.findViewById(R.id.button_suggest_taste);
        final RadioGroup radioGroup = (RadioGroup) rootView.findViewById(R.id.radio_group_like);
        final EditText commentArea = (android.widget.EditText) rootView.findViewById(R.id.comment_area);
        suggestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = spinnerCategory.getSelectedItem().toString();
                String food = spinnerFood.getSelectedItem().toString();
                String comment = commentArea.getText().toString();
                int selectedId = radioGroup.getCheckedRadioButtonId();

                if (!food.equals("") && !category.equals("") && selectedId != -1) {
                    int taste = 0;
                    switch (selectedId) {
                        case R.id.radioButton_like:
                            taste = 1;
                            break;
                        case R.id.radioButton_bof:
                            taste = 0;
                            break;
                        case R.id.radioButton_unlike:
                            taste = -1;
                            break;
                    }

                    Taste newTaste = new Taste(food, taste, comment);
                    String firebaseId = ((ProfilActivity)getActivity()).userFirebaseId;
                    UsersManager.addMessageToUser(newTaste, category, firebaseId);
                    //TasteManager.addTaste(newTaste, category);

                    radioGroup.clearCheck();
                    commentArea.setText("");

                    String message = food + " a été suggéré.";
                    Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                }
            }
        });

        return rootView;
    }
}
