package it.tc.mobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by itibatullin on 23.11.2015.
 */
public class home extends AppCompatActivity {

    JSONArray disciplines;
    JSONArray classes;
    protected String TAG = "LIFECYCLE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        disciplines = null;
        classes = null;

        getDisciplines();
        getClasses();
        Log.d(TAG, "________home: >>");
    }

    protected void initDisciplines() {
        Spinner spinner = (Spinner) findViewById(R.id.discpline);
        ArrayList<String> dists = new ArrayList<String>();
        for(int i = 0; i < disciplines.length(); i ++) {
            try {
                JSONObject oneDis = disciplines.getJSONObject(i);
                String disStr = oneDis.getString("name");
                dists.add(disStr);
            } catch (JSONException ignored) {}
        }
        ArrayAdapter<String> adapter =  new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dists);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    protected void initClasses() {
        Spinner spinner = (Spinner) findViewById(R.id.classes);
        ArrayList<String> classArray = new ArrayList<String>();
        for(int i = 0; i < classes.length(); i ++) {
            try {
                JSONObject oneClass = classes.getJSONObject(i);
                String classStr = oneClass.getString("name");
                classArray.add(classStr);
            } catch (JSONException ignored) {}
        }
        ArrayAdapter<String> adapter =  new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, classArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    protected int getStaffID() {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        int ID = sharedPreferences.getInt("ID", -1);

        if (ID == -1) {
            //no ID -> go to start page
            goToStartPage();
        }

        return ID;
    }

    protected String getToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        if (token.equals("")) {
            //no token -> go to Start page
            goToStartPage();
        }
        return token;
    }

    protected void getClasses() {
        String method = "/classes?";
        new AsyncHttpGetClasses().execute("GET", method + "id_staff=" + getStaffID() + "&token=" + getToken());
    }

    class AsyncHttpGetClasses extends AsyncHttp {
        @Override
        protected void onPostExecute(String result) {
            Log.d("TAG_HTTP", result);
            try {
                classes = new JSONArray(result);
                moveToSharedPreferences("classes", result);
                initClasses();
            } catch (JSONException e) {
                e.getStackTrace();
                //Couldn't parse JSON
            }
        }
    }

    protected void getDisciplines() {
        String method = "/subjects?";
        new AsyncHttpGetDiscipline().execute("GET", method + "id_staff=" + getStaffID() + "&token=" + getToken());
    }

    class AsyncHttpGetDiscipline extends AsyncHttp {
        @Override
        protected void onPostExecute(String result) {
            try {
                disciplines = new JSONArray(result);
                moveToSharedPreferences("disciplines", result);
                initDisciplines();
            } catch (JSONException e) {
                e.getStackTrace();
                //Couldn't parse JSON
            }
        }
    }

    protected void moveToSharedPreferences(String key, String value) {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("result", "exit");
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        Log.d("HOME_PAGE", "Menu item is selected");
        switch (item.getItemId()) {
            case R.id.logOut: {
                clearSettings();
                goToStartPage();
            }
            case R.id.firstMidlename: {
                break;
            }
            default: {
                break;
            }
        }
        return true;
    }

    protected void goToStartPage() {
        Intent intent = new Intent();
        intent.putExtra("result", "goToStart");
        setResult(RESULT_OK, intent);
        finish();
    }

    protected void clearSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "________home: <<");
        //          presentation
    }
}
