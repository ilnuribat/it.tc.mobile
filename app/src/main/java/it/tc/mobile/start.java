package it.tc.mobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class start extends AppCompatActivity {

    String TAG_HTTP = "HTTP";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Button btn = (Button) findViewById(R.id.auth);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView login = (TextView) findViewById(R.id.loginText);
                TextView password = (TextView) findViewById(R.id.passwordText);
                String loginTxt = login.getText().toString();
                String md5Password = md5(password.getText().toString());

                new AsyncHttpP().execute("POST", "http://52.27.138.37:8080/auth",
                        "apikey=6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b" +
                                "&login=" + loginTxt +
                                "&password=" + md5Password +
                                "&method=login");
            }
        });


    }

    public static String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    class AsyncHttpP extends AsyncHttp {
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject json = new JSONObject(result);
                String res = json.getString("result");
                if(res.equals("success")) {
                    successLogin(json);
                }
                else {
                    errorLogin(json.getString("error"));
                }
            } catch (JSONException e) {
                //Couldn't parse JSON
            }
        }
    }
    protected void successLogin(JSONObject json) {
        String firstMiddleName = null;
        String position = null;
        String token = null;
        int ID = 0;

        try {
            firstMiddleName = json.getString("firstname") + json.getString("middlename");
            position = json.getString("position");
            token = json.getString("token");
            ID = json.getInt("id");
        } catch (JSONException e) {
            Log.d("jsonParseError", "couldn't parse JSON after auth");
            e.printStackTrace();
        }



        SharedPreferences sharedPref = getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("firstMiddleName", firstMiddleName);
        editor.putString("position", position);
        editor.putString("token", token);
        editor.putInt("ID", ID);
        editor.commit();

        Context context = getApplicationContext();
        Intent intent = new Intent(context, home.class);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data.getStringExtra("result").equals("exit")) {
            Intent intent = new Intent();
            intent.putExtra("result", "exit");
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    protected void errorLogin(String errorDescription) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, errorDescription, duration);
        toast.show();
        EditText loginText = (EditText) findViewById(R.id.loginText);
        EditText passwordText = (EditText) findViewById(R.id.passwordText);
        loginText.setText("");
        passwordText.setText("");
    }

    @Override
    public void onBackPressed() {
        Log.d("LOG", "back Button clicked");
        Intent intent = new Intent();
        intent.putExtra("result", "exit");
        setResult(RESULT_OK, intent);
        finish();
    }
}
