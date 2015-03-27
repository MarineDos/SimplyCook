package simplycook.marinedos.com.simplycook.Utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import simplycook.marinedos.com.simplycook.R;

/** @brief	Class that manage the favoris. */
public class FavorisArrayAdapter extends ArrayAdapter<User>{
    /** @brief	The context. */
    Context context;
    /** @brief	Identifier for the layout resource. */
    int layoutResourceId;
    /** @brief	The list of user. */
    List<User> data = null;

    /**
    *@brief		Constructor.
    *
    *@param		context				The context.
    *@param		layoutResourceId	Identifier for the layout resource.
    *@param		data				The data.
     */
    public FavorisArrayAdapter(Context context, int layoutResourceId, List<User> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    /**
    *@brief		Gets the view.
    *
    *@param		position   	The position.
    *@param		convertView	The convert view.
    *@param		parent	   	The parent view.
    *
    *@return	The view.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        UserHolder holder;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new UserHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.favoris_profil_img);
            holder.txtTitle = (TextView)row.findViewById(R.id.favoris_profil_name);

            row.setTag(holder);
        }
        else
        {
            holder = (UserHolder)row.getTag();
        }

        User user = data.get(position);
        holder.txtTitle.setText(user.firstName + " " + user.lastName);
        if(user.connexionMode.equals("facebook")){
            holder.imgIcon.setImageBitmap(user.imageBitmap);
        }else{
            holder.imgIcon.setImageResource(user.imageRessource);
        }

        return row;
    }

    static class UserHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
    }
}
