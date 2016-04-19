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
        Log.d(TAG, "presentation: >>");

        Button btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), start.class);
                startActivity(intent);
            }
        });

        if (isAuth())
            goHomePage();
        else
            goStartPage();
    }

    protected boolean isAuth() {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        int ID = sharedPreferences.getInt("ID", -1);
        Log.d(TAG, ID + " - ID of user from settings");
        return ID != -1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, requestCode + " - request Code, " + resultCode + " - resultCode, " + data.getStringExtra("result"));
        switch (requestCode) {
            case 1: {
                String resultStr = data.getStringExtra("result");
                if (resultStr.equals("exit")) {

                    finish();
                } else
                if (resultStr.equals("successLogin"))
                    goHomePage();
                break;
            }
            case 2: {
                String resultStr = data.getStringExtra("result");
                if (resultStr.equals("exit"))
                    finish();
                else if (resultStr.equals("goToStart"))
                    goStartPage();
                break;
            }
            default: {
                break;
            }
        }
    }

    protected void goStartPage() {
        Context context = getApplicationContext();
        Intent intent = new Intent(context, start.class);
        startActivityForResult(intent, 1);
    }

    protected void goHomePage() {
        Context context = getApplicationContext();
        Intent intent = new Intent(context, home.class);
        startActivityForResult(intent, 2);
    }

    protected String TAG = "LIFECYCLE";
}
