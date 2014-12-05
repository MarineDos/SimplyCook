package simplycook.marinedos.com.simplycook.Utils.tabsswipe;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import simplycook.marinedos.com.simplycook.R;
import simplycook.marinedos.com.simplycook.Utils.ConnexionManager;
import simplycook.marinedos.com.simplycook.Utils.Taste;

public class manageTasteFragment extends Fragment {

    private final Firebase ref = new Firebase("https://simplycook.firebaseio.com");
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.profil_managetaste_tab, container, false);

        final Spinner spinnerFood= (Spinner) rootView.findViewById(R.id.food_spinner);

        // Populate catégorie
        final Spinner spinnerCategory= (Spinner) rootView.findViewById(R.id.category_spinner);
        // Get All categories
        ref.child("/category").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> tab = new ArrayList();

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
                System.out.println("COuouc : " + selectedCategory);
                ref.child("/food/" + selectedCategory).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<String> tab = new ArrayList();
                        System.out.println(dataSnapshot.getValue());

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

        final Button addButton = (Button) rootView.findViewById(R.id.button_add_taste);
        final RadioGroup radioGroup = (RadioGroup) rootView.findViewById(R.id.radio_group_like);
        final EditText commentArea = (android.widget.EditText) rootView.findViewById(R.id.comment_area);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = spinnerCategory.getSelectedItem().toString();
                String food = spinnerFood.getSelectedItem().toString();
                String comment = commentArea.getText().toString();
                System.out.println("Add food : " + food);

                if(!food.equals("") && !category.equals("")){
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    int taste = 0;
                    switch (selectedId){
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

                    System.out.println("Ready to add");

                    Taste newTaste = new Taste(food, taste, comment);
                    ConnexionManager.addTaste(newTaste, category);
                }
            }
        });


        return rootView;
    }
}
