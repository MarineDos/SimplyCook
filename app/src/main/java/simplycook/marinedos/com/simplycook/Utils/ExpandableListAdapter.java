package simplycook.marinedos.com.simplycook.Utils;

import simplycook.marinedos.com.simplycook.R;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/** @brief	Class that manage an expandable list. */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    /** @brief	The context of the view. */
    private Context _context;
    /** @brief	Header titles. */
    private List<String> _listDataHeader;
    /** @brief	Child data in format of header title, child title. */
    private HashMap<String, List<TasteMessage>> _listDataChild;

	/**
    *@brief		Constructor.
    *
    *@param		context		  	The context.
    *@param		listDataHeader	The list data header.
    *@param		listChildData 	The list data child.
     */
    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<Taste>> listChildData) {
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
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final Taste childTaste = (Taste) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.profil_list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);

        txtListChild.setText(childTaste.getName());

        ImageView iconListChild = (ImageView) convertView.findViewById(R.id.iconListImage);
        switch(childTaste.getLike()){
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

        TextView infoText = (TextView) convertView.findViewById(R.id.textInfo);
        ImageView info = (ImageView) convertView.findViewById(R.id.iconInfoImage);
        if(!childTaste.getComment().equals("")){
            if(DeviceInformation.isTablet(_context)){
                infoText.setText(childTaste.getComment());
                infoText.setVisibility(View.VISIBLE);
                info.setVisibility(View.INVISIBLE);
            }else {
                infoText.setVisibility(View.INVISIBLE);
                info.setVisibility(View.VISIBLE);
            }
        }else{
            info.setVisibility(View.INVISIBLE);
            infoText.setVisibility(View.INVISIBLE);
        }

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
            convertView = infalInflater.inflate(R.layout.profil_list_group, null);
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