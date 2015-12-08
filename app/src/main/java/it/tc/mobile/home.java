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
        initViews();
        Log.d(TAG, "________home: >>");
    }

    protected void initViews() {
        Spinner spinner = (Spinner) findViewById(R.id.discpline);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.disciplines, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
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
                Intent intent = new Intent();
                intent.putExtra("result", "goToStart");
                setResult(RESULT_OK, intent);
                finish();
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
