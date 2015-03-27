package simplycook.marinedos.com.simplycook.Utils;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import simplycook.marinedos.com.simplycook.R;


/** @brief	Class that manage an expandable list of messages. */
public class ExpandableListAdapter_Message extends BaseExpandableListAdapter{
    /** @brief	The context of the view. */
    private Context _context;
    /** @brief	Header titles. */
    private List<String> _listDataHeader;
    /** @brief	Child data in format of header title, child title. */
    private HashMap<String, List<TasteMessage>> _listDataChild;

	*/** @brief	The reference for firebase. */
    private final Firebase ref = new Firebase("https://simplycook.firebaseio.com");

    /**
    *@brief		Constructor.
    *
    *@param		context		  	The context of the view.
    *@param		listDataHeader	The list data header.
    *@param		listChildData 	The list data child.
     */
    public ExpandableListAdapter_Message(Context context, List<String> listDataHeader,
                                 HashMap<String, List<TasteMessage>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }
    
    /**
    *@brief		Gets a child data in HashMap _listDataChild.
    *
    *@param		groupPosition 	The group position.
    *@param		childPosititon	The child data posititon.
    *
    *@return	The child data.
     */
	 @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    /**
    *@brief		Gets a child data identifier.
    *
    *@param		groupPosition	The group position.
    *@param		childPosition	The child data position.
    *
    *@return	The child data identifier.
     */
	 @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
    *@brief		Gets a child data view.
    *
    *@param		groupPosition	The group position.
    *@param		childPosition	The child data position.
    *@param		isLastChild  	true if this object is the last child data.
    *@param		convertView  	The convert view.
    *@param		parent		 	The parent view.
    *
    *@return	The child data view.
     */
    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final TasteMessage childTaste = (TasteMessage) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.profil_message_list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);

        txtListChild.setText(childTaste.foodName);

        TextView txtListChildHeader = (TextView) convertView.findViewById(R.id.lblListItemName);
        txtListChildHeader.setText(childTaste.firstName + " " + childTaste.lastName + " a suggéré");

        ImageView iconListChild = (ImageView) convertView.findViewById(R.id.iconListImage);
        switch(childTaste.like){
            case -1:
                iconListChild.setImageResource(R.drawable.unlike);
                break;
            case 0:
                iconListChild.setImageResource(R.drawable.bof);
                break;
            case 1:
                iconListChild.setImageResource(R.drawable.like);
                break;
        }

        /*if(childTaste.getComment().equals("")){
            ImageView info = (ImageView) convertView.findViewById(R.id.iconInfoImage);
            info.setVisibility(View.INVISIBLE);
        }*/

        // Accept Button
        ImageView accept_btn = (ImageView) convertView.findViewById(R.id.btn_accept);
        accept_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //System.out.println("Clic on button accept : " + childTaste.foodName + " in " + getGroup(groupPosition).toString());
                // Create taste and add to my taste
                final String categoryName = getGroup(groupPosition).toString();
                Taste newTaste = new Taste(childTaste.foodName, childTaste.like, childTaste.comment);
                TasteManager.addTaste(newTaste, categoryName);

                // Delete message
                ref.child("users/" + ConnexionManager.user.firebaseId + "/messages")
                        .startAt(categoryName)
                        .endAt(categoryName)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot != null){
                                    for(DataSnapshot message : dataSnapshot.getChildren()){
                                        if((message.child("foodName").getValue(String.class)).equals(childTaste.foodName)){
                                            //System.out.println("I want to delete : " + message.child("foodName").getValue(String.class) + " = " + childTaste.foodName + " which key is " + message.getKey());

                                            // Remove element from list
                                            Iterator it = _listDataChild.get(categoryName).iterator();
                                            while(it.hasNext()){
                                                TasteMessage mess = (TasteMessage)it.next();
                                                if(mess.foodName.equals(childTaste.foodName)){
                                                    //System.out.println("Remove child from listDataChild : " + mess.foodName);
                                                    it.remove();
                                                }
                                            }

                                            // Remove element from header if needed
                                            if(_listDataChild.get(categoryName).size() < 1){
                                                //System.out.println("Remove header from listDataHeader : " + categoryName);
                                                _listDataHeader.remove(categoryName);
                                            }

                                            notifyDataSetChanged();

                                            // Remove element from firebase
                                            Firebase refToDelete = ref.child("users/" + ConnexionManager.user.firebaseId + "/messages/" + message.getKey());
                                            refToDelete.removeValue();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
            }
        });

        // Refuse Button
        ImageView refuse_btn = (ImageView) convertView.findViewById(R.id.btn_refuse);
        refuse_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //System.out.println("Clic on button refuse : " + childTaste.foodName + " in " + getGroup(groupPosition).toString());
                final String categoryName = getGroup(groupPosition).toString();

                // Delete message
                ref.child("users/" + ConnexionManager.user.firebaseId + "/messages")
                        .startAt(categoryName)
                        .endAt(categoryName)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot != null){
                                    for(DataSnapshot message : dataSnapshot.getChildren()){
                                        if((message.child("foodName").getValue(String.class)).equals(childTaste.foodName)){
                                            //System.out.println("I want to delete : " + message.child("foodName").getValue(String.class) + " = " + childTaste.foodName + " which key is " + message.getKey());

                                            // Remove element from list
                                            Iterator it = _listDataChild.get(categoryName).iterator();
                                            while(it.hasNext()){
                                                TasteMessage mess = (TasteMessage)it.next();
                                                if(mess.foodName.equals(childTaste.foodName)){
                                                    //System.out.println("Remove child from listDataChild : " + mess.foodName);
                                                    it.remove();
                                                }
                                            }

                                            // Remove element from header if needed
                                            if(_listDataChild.get(categoryName).size() < 1){
                                                //System.out.println("Remove header from listDataHeader : " + categoryName);
                                                _listDataHeader.remove(categoryName);
                                            }

                                            notifyDataSetChanged();

                                            // Remove element from firebase
                                            Firebase refToDelete = ref.child("users/" + ConnexionManager.user.firebaseId + "/messages/" + message.getKey());
                                            refToDelete.removeValue();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
            }
        });

        return convertView;
    }

    /**
    *@brief		Gets the number of children in the group.
    *
    *@param		groupPosition	The group position.
    *
    *@return	The number of children.
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    /**
    *@brief		Gets a group.
    *
    *@param		groupPosition	The group position.
    *
    *@return	The group.
     */
    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    /**
    *@brief		Gets the number of group.
    *
    *@return	The number of group.
     */
    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    /**
    *@brief		Gets group identifier.
    *
    *@param		groupPosition	The group position.
    *
    *@return	The group identifier.
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
    *@brief		Gets group view.
    *
    *@param		groupPosition	The group position.
    *@param		isExpanded   	true if the group is expanded.
    *@param		convertView  	The convert view.
    *@param		parent		 	The parent view.
    *
    *@return	The group view.
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.profil_message_list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    /**
    *@brief		Query if this object has stable identifiers.
    *
    *@return	true if stable identifiers, false if not.
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
    *@brief		Query if the child is selectable.
    *
    *@param		groupPosition	The group position.
    *@param		childPosition	The child position.
    *
    *@return	true if child selectable, false if not.
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
