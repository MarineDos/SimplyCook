package simplycook.marinedos.com.simplycook.Utils;

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

import simplycook.marinedos.com.simplycook.R;


public class ExpandableListAdapter_Comparaison extends BaseExpandableListAdapter {
    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String,List<TasteMultiLike>> _listDataChild;

    public ExpandableListAdapter_Comparaison(Context context, List<String> listDataHeader,
                                             HashMap<String, List<TasteMultiLike>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final TasteMultiLike childHashTable = (TasteMultiLike) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.comparaison_list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);

        txtListChild.setText(childHashTable.getName());

        ImageView iconListChild;
        int[] likes = childHashTable.getLikes();
        for(int i = 0; i < 4; ++i){
            switch (i){
                case 0 : iconListChild = (ImageView)convertView.findViewById(R.id.iconListImage1); break;
                case 1 : iconListChild = (ImageView)convertView.findViewById(R.id.iconListImage2); break;
                case 2 : iconListChild = (ImageView)convertView.findViewById(R.id.iconListImage3); break;
                case 3 : iconListChild = (ImageView)convertView.findViewById(R.id.iconListImage4); break;
                default: iconListChild = (ImageView)convertView.findViewById(R.id.iconListImage1); break;
            }
            switch(likes[i]){
                case -1:
                    iconListChild.setImageResource(R.drawable.unlike);
                    break;
                case 0:
                    iconListChild.setImageResource(R.drawable.bof);
                    break;
                case 1:
                    iconListChild.setImageResource(R.drawable.like);
                    break;
                default:
                    iconListChild.setVisibility(View.INVISIBLE);
            }
        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.comparaison_list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
