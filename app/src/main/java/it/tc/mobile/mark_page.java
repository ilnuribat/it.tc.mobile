package it.tc.mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class mark_page extends AppCompatActivity {
    JSONArray listOfRes;
    ArrayList<String> list;
    int currentMarkType;
    int disciplineID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_page);
        try {
            listOfRes = new JSONArray("[]");
        } catch (JSONException ignored) {}

        currentMarkType = -1;
        list = null;
        getListOfRes();
        initRadioBtns();
    }

    protected void initRadioBtns() {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        JSONArray types = null;
        try {
            JSONObject discJSON = new JSONObject(sharedPreferences.getString("localSettings", "{}"));
            //Тут хранится последний выбранный предмет
            int discsPos = discJSON.getInt("lastDiscipline");
            JSONArray discs = new JSONArray(sharedPreferences.getString("disciplines", "[]"));
            JSONObject lastDisc = discs.getJSONObject(discsPos);
            types = lastDisc.getJSONArray("types");
        } catch (JSONException ignored) {}

        RadioGroup typesBtns = (RadioGroup) findViewById(R.id.radioBtns);
        typesBtns.setWeightSum(types.length());

        for (int i = 0; i < types.length(); i ++) {
            String currentTitle = "error";
            int currentID = -1;
            try {
                JSONObject oneType = types.getJSONObject(i);
                currentTitle = oneType.getString("shortName");
                currentID = oneType.getInt("id");
            } catch (JSONException ignored) {}
            RadioButton currentBtn = new RadioButton(this);
            currentBtn.setText(currentTitle);
            currentBtn.setId(currentID);
            currentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentMarkType = v.getId();
                }
            });
            typesBtns.addView(currentBtn);
        }
    }

    protected void getListOfRes() {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        int id_staff = sharedPreferences.getInt("ID", -1);
        if (token.equals("") || id_staff == -1)
            goToStartPage();


        int disPos = 0, classPos = 0;
        try {
            JSONObject localSettings = new JSONObject(sharedPreferences.getString("localSettings", "{}"));
            disPos = localSettings.getInt("lastDiscipline");
            classPos = localSettings.getInt("lastClass");
        } catch (JSONException ignored) {}

        disciplineID = getResId(disPos, "disciplines");
        int classID = getResId(classPos, "classes");

        String dateStr = getDate();

        String query = "/marks?id_staff=" + id_staff + "&token=" + token + "&class=" + classID + "&id_discipline=" + disciplineID + "&date=" + dateStr;

        new AsyncGetListOfRes().execute("GET", query);
    }

    protected String getDate() {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date today = Calendar.getInstance().getTime();
        String dateStr = formatter.format(today);
        return dateStr;
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
            try {
                JSONObject listRes = new JSONObject(result);
                listOfRes = listRes.getJSONArray("data");
                initListView();
            } catch (JSONException e) {
                e.getStackTrace();
                //Couldn't parse JSON
            }
        }
    }

    protected void initListView() {
        final ListView listView = (ListView) findViewById(R.id.listView);
        try {
            list = new ArrayList<String>(listOfRes.length());
            for (int i = 0; i < listOfRes.length(); i++) {
                String nameLastname = listOfRes.getJSONObject(i).getString("nameLastname");
                list.add(nameLastname);
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.raw_layout, R.id.nameLastname, list);
            listView.setAdapter(arrayAdapter);
        } catch (JSONException ignored) {}

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Вытаскиваем ID объекта и передаем в функцию
                int resID = -1;
                try {
                    resID = listOfRes.getJSONObject(position).getInt("id");
                } catch (JSONException ignored) {}
                sendMark(resID);
            }
        });
    }

    protected void sendMark(int resID) {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        int id_staff = sharedPreferences.getInt("ID", -1);
        String date = getDate();
        //disciplineID
        //resID
        if(currentMarkType == -1)
            //Если не выбрал оценку, то не отправлять оценку
            return;

        String query = "&token=" + token + "&id_staff=" + id_staff + "&discipline=" + disciplineID + "&id_object=" + resID + "&mark=" + currentMarkType + "&date=" + date;
        new AsyncSendMark().execute("POST", "/mark", query);
    }

    protected class AsyncSendMark extends AsyncHttp {
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject report = new JSONObject(result);
                Log.d("marking", result);
            } catch (JSONException e) {
                e.getStackTrace();
            }
        }
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
