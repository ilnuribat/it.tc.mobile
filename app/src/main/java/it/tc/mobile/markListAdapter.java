package it.tc.mobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

/**
 * Created by itibatullin on 30.12.2015
 */
public class MarkListAdapter extends ArrayAdapter<ResItemMark> {
    private List<ResItemMark> listOfRes;
    private Context context;
    private JSONArray typeOfMarks;

    public MarkListAdapter(Context context, int resource, List<ResItemMark> objects, JSONArray typeOfMarks) {
        super(context, resource, objects);
        this.typeOfMarks = typeOfMarks;
        this.context = context;
        listOfRes = objects;
    }

    private String getShortName(int markID) {
        for (int i = 0; i < typeOfMarks.length(); i ++) {
            try {
                if (typeOfMarks.getJSONObject(i).getInt("id") == markID)
                    return typeOfMarks.getJSONObject(i).getString("shortName");
            } catch (JSONException ignored) {}
        }
        return "";
    }

    public int getCount() {
        return listOfRes.size();
    }


    public long getItemId(int position) {
        return listOfRes.get(position).getID();
    }

    public ResItemMark getItem(int position) {
        return listOfRes.get(position);
    }

    public int findPositionByID(int ID) {
        //If no such ID, returns -1
        for (int i = 0; i < listOfRes.size(); i ++) {
            if (listOfRes.get(i).getID() == ID)
                return i;
        }
        return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ResItemMark entry = listOfRes.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.raw_layout, null);
        }
        TextView nameSurname = (TextView) convertView.findViewById(R.id.nameLastname);
        nameSurname.setText(entry.getName());

        TextView markID = (TextView) convertView.findViewById(R.id.markID);
        markID.setText(getShortName(entry.getMarkID()));

        return convertView;
    }

}
