package it.tc.mobile;

import android.app.DialogFragment;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import it.tc.mobile.helpers.DatePickerFragment;

public class home extends AppCompatActivity {

    JSONArray disciplines;
    JSONArray classes;
    String choosenDate;

    protected String TAG = "LIFECYCLE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        //start init of global variables
        try {
            disciplines = new JSONArray("[]");
            classes = new JSONArray("[]"); } catch (JSONException ignored) {}


        getDisciplines();
        getClasses();
        initDatePicker();
        Log.d(TAG, "________home: >>");
    }

    protected void initDatePicker() {
        final TextView datePickerButton = (TextView) findViewById(R.id.datePickerButton);
        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        choosenDate = "" + month + "-" + day + "-" + year;
                        System.out.println(choosenDate);
                        moveToSharedPreferences("date", choosenDate);
                        datePickerButton.setText(choosenDate);
                    }
                };
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });

    }

    protected void initDisciplines() {
        //This code will run after disciplines was got.
        //init spinner, set listener
        Spinner spinner = (Spinner) findViewById(R.id.discpline);
        ArrayList<String> dists = new ArrayList<String>();
        for (int i = 0; i < disciplines.length(); i ++) {
            try {
                JSONObject oneDis = disciplines.getJSONObject(i);
                String disStr = oneDis.getString("name");
                dists.add(disStr);
            } catch (JSONException ignored) {}
        }
        ArrayAdapter<String> adapter =  new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dists);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(getLastDiscipline());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
                String lastSettings = sharedPreferences.getString("localSettings", "{}");
                Log.d("spinner", lastSettings);
                //Сохраняем выбранный класс в настройках
                try {
                    //вытаскиваем предыдущие сохраненные данные, поверх записываем новые
                    JSONObject selectedDiscipline = new JSONObject(lastSettings);
                    selectedDiscipline.put("lastDiscipline", position);
                    moveToSharedPreferences("localSettings", selectedDiscipline.toString());
                } catch (JSONException ignored) {
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (classes.length() > 0) {
            initGoButton();
        }
    }

    protected void initClasses() {
        //This code will run after disciplines was got.
        //init spinner, set listener
        Spinner spinner = (Spinner) findViewById(R.id.classes);
        ArrayList<String> classArray = new ArrayList<String>();
        for (int i = 0; i < classes.length(); i ++) {
            try {
                JSONObject oneClass = classes.getJSONObject(i);
                String classStr = oneClass.getString("name");
                classArray.add(classStr);
            } catch (JSONException ignored) {}
        }
        ArrayAdapter<String> adapter =  new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, classArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(getLastClass(), true);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
                String lastSettings = sharedPreferences.getString("localSettings", "{}");
                Log.d("spinner", lastSettings);
                //Сохраняем выбранный класс в настройках
                try {
                    //вытаскиваем предыдущие сохраненные данные, поверх записываем новые
                    JSONObject selectedClass = new JSONObject(lastSettings);
                    selectedClass.put("lastClass", position);
                    moveToSharedPreferences("localSettings", selectedClass.toString());
                } catch (JSONException ignored) {}
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        }) ;
        if (disciplines.length() > 0) {
            initGoButton();
        }
    }

    protected int getLastClass() {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        int lastClass = 0;
        try {
            JSONObject last = new JSONObject(sharedPreferences.getString("localSettings", "{}"));
            lastClass = last.getInt("lastClass");
            Log.d("last", "class: " + lastClass);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return lastClass;
    }

    protected int getLastDiscipline() {
        //Position in spinner
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        int lastDiscipline = 0;
        try {
            JSONObject last = new JSONObject(sharedPreferences.getString("localSettings", "{}"));
            lastDiscipline = last.getInt("lastDiscipline");
            Log.d("last", "discipline: " + lastDiscipline);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return lastDiscipline;
    }

    protected void initGoButton() {
        Button goListPage = (Button) findViewById(R.id.goListPage);
        goListPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                Intent intent = new Intent(context, mark_page.class);
                startActivityForResult(intent, 3);
            }
        });
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
        new AsyncHttpGetClasses().execute("GET", "/classes?id_staff=" + getStaffID() + "&token=" + getToken());
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
        editor.apply();
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
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "________home: <<");
        //          presentation
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 3: {
                String resultStr = data.getStringExtra("result");
                if (resultStr.equals("home")) {
                    Log.d(TAG, "Welcome from mark_page to Home!");
                }
                else if (resultStr.equals("goToStart"))
                    goToStartPage();
                break;
            }
            default: {
                break;
            }
        }
    }
}
