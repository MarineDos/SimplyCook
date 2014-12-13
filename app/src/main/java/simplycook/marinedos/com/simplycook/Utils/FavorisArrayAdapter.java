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

public class FavorisArrayAdapter extends ArrayAdapter<User>{
    Context context;
    int layoutResourceId;
    List<User> data = null;

    public FavorisArrayAdapter(Context context, int layoutResourceId, List<User> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

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
