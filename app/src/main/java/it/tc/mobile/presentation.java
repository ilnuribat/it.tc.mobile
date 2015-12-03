package it.tc.mobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by itibatullin on 24.11.2015.
 */
public class presentation extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presentation);
        Log.d(TAG, "presentation onCreate()");
        Button btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), start.class);
                startActivity(intent);
            }
        });
        Context context = getApplicationContext();
        Intent intent = null;
        if(isAuth() == true)
            intent = new Intent(context, home.class);
        else
            intent = new Intent(context, start.class);
        startActivityForResult(intent, 1);
    }

    protected boolean isAuth()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        int ID = sharedPreferences.getInt("ID", -1);
        if(ID == -1)
            return false;
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        
    }

    protected String TAG = "LIFECYCLE";

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "presentaion: onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "presentaion: onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "presentaion: onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "presentaion: onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "presentaion: onDestroy()");
    }

}
