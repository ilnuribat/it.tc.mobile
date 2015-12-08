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

        getDisciplines();
        Log.d(TAG, "________home: >>");
    }

    protected void initViews() {
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

    protected void getDisciplines() {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        int ID = sharedPreferences.getInt("ID", -1);

        if (token.equals("") || ID == -1) {
            //no token, no ID -> go to start page
            goToStartPage();
        }
        String method = "/subjects?";
        new AsyncHttpGetDiscipline().execute("GET", method + "id_staff=" + ID + "&token=" + token);
    }

    class AsyncHttpGetDiscipline extends AsyncHttp {
        @Override
        protected void onPostExecute(String result) {
            Log.d("TAG_HTTP", result);
            try {
                disciplines = new JSONArray(result);
                moveToSharedPreferences(result);
                initViews();
            } catch (JSONException e) {
                e.getStackTrace();
                //Couldn't parse JSON
            }
        }
    }

    protected void moveToSharedPreferences(String str) {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("disciplines", str);
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
