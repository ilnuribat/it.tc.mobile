package it.tc.mobile;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by itibatullin on 29.12.2015.
 */
public class ResItemMark {
    private String nameLastname;
    private int ID;
    private int markID;
    private int intentMark;

    ResItemMark (JSONObject jsonObject) {
        try {
            nameLastname = jsonObject.getString("nameLastname");
            ID = jsonObject.getInt("id");
            markID = jsonObject.getInt("mark");
        } catch (JSONException ignored) {}
    }
    public String getName() {
        return nameLastname;
    }

    public int getID() {
        return ID;
    }

    public int getMarkID() {
        return markID;
    }

    public void setMarkID(int newMark) {
        //sent mark to server, handling response.
        markID = newMark;
    }
}
