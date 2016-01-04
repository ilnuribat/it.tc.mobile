package it.tc.mobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.json.JSONArray;

import java.util.List;

/**
 * Created by itibatullin on 30.12.2015.
 */
public class markListAdapter extends ArrayAdapter<ResItemMark> {
    private List<ResItemMark> listOfRes;
    private Context context;

    public markListAdapter(Context context, int resource, List<ResItemMark> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return convertView;
    }

}
