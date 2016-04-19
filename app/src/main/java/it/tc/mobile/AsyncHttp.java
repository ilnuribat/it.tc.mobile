package it.tc.mobile;

import android.os.AsyncTask;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by itibatullin on 20.11.2015
 */
class AsyncHttp extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {

        // params comes from the execute() call: params[0] is the url.

        String method = urls[0];
        String url = urls[1];
        String API_KEY = "&apikey=6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b";
        String IP = "http://52.34.168.159:8080";
        if (method.equals("GET"))
            return downloadUrlGET(IP + url + API_KEY);
        //POST METHOD
        else {
            String data = urls[2];
            return downloadUrlPOST(IP + url, data + API_KEY);
        }
    }

    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    private String downloadUrlPOST(String myurl, String data) {
        InputStream is = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(myurl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");

            byte[] postData       = data.getBytes("UTF-8");
            int    postDataLength = postData.length;

            conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
            conn.getOutputStream().write(postData);
            // Starts the query
            conn.connect();
            is = conn.getInputStream();

            // Convert the InputStream into a string
            return IOUtils.toString(is, "UTF-8");

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        }
        catch (IOException e) {
            assert conn != null;
            is = conn.getErrorStream();
            try {
                return IOUtils.toString(is, "UTF-8");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            assert conn != null;
            conn.disconnect();
        }
        return "";
    }

    // Here must be POST request.
    //
    //
    private String downloadUrlGET(String myurl){
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        HttpURLConnection conn = null;
        try {
            URL url = new URL(myurl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            //conn.setDoInput(true);
            // Starts the query
            conn.connect();
            is = conn.getInputStream();

            // Convert the InputStream into a string
            return IOUtils.toString(is, "UTF-8");

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } catch (IOException e) {
            assert conn != null;
            is = conn.getErrorStream();
            try {
                return IOUtils.toString(is, "UTF-8");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            assert conn != null;
            conn.disconnect();
        }
        return "";
    }

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
    }

}
