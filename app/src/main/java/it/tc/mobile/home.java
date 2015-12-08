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

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.disciplines, android.R.layout.simple_spinner_item);
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
        String method = "/subjects/?";
        new AsyncHttpGetDiscipline().execute("GET", method + "id_staff=" + ID + "&token=" + token);
    }

    class AsyncHttpGetDiscipline extends AsyncHttp {
        @Override
        protected void onPostExecute(String result) {
            Log.d("TAG_HTTP", result);
            try {
                disciplines = new JSONArray(result);
                initViews();
            } catch (JSONException e) {
                //Couldn't parse JSON
            }
        }
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
