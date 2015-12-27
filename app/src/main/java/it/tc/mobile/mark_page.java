package it.tc.mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class mark_page extends AppCompatActivity {
    JSONObject listRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_page);
        try {
            listRes = new JSONObject("{}"); } catch (JSONException ignored) {}

        getListOfRes();
    }

    protected void getListOfRes() {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        int id_staff = sharedPreferences.getInt("ID", -1);
        if (token == "" || id_staff == -1)
            goToStartPage();


        int disPos = 0, classPos = 0;
        try {
            JSONObject localSettings = new JSONObject(sharedPreferences.getString("localSettings", "{}"));
            disPos = localSettings.getInt("lastDiscipline");
            classPos = localSettings.getInt("lastClass");
        } catch (JSONException ignored) {}

        int disID = getResId(disPos, "disciplines");
        int classID = getResId(classPos, "classes");

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date today = Calendar.getInstance().getTime();
        String dateStr = formatter.format(today);

        String query = "/marks?id_staff=" + id_staff + "&token=" + token + "&class=" + classID + "&id_discipline=" + disID + "&date=" + dateStr;
        new AsyncGetListOfRes().execute("GET", query);
    }

    protected int getResId(int position, String resource) {
        //There are two json arrays: list of classes and list of disciplines
        //params 'resource' is name of list: "class" or "discipline"
        JSONArray allRes;
        JSONObject oneRes;
        int resID = 0;
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        String fromShared = sharedPreferences.getString(resource, "[]");
        try {
            allRes = new JSONArray(fromShared);
            oneRes = allRes.getJSONObject(position);
            resID = oneRes.getInt("id");
        } catch (JSONException ignored) {
            ignored.getStackTrace();
        }
        return resID;
    }

    protected class AsyncGetListOfRes extends AsyncHttp {
        @Override
        protected void onPostExecute(String result) {
            Log.d("TAG_HTTP", result);
            try {
                listRes = new JSONObject(result);
                initListView();
            } catch (JSONException e) {
                e.getStackTrace();
                //Couldn't parse JSON
            }
        }
    }

    protected void initListView() {
        Log.d("listResult", listRes.toString());
    }

    protected void goToStartPage() {
        Intent intent = new Intent();
        intent.putExtra("result", "goToStart");
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("result", "home");
        setResult(RESULT_OK, intent);
        finish();
    }
}
