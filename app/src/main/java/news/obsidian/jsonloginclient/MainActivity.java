package news.obsidian.jsonloginclient;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    EditText userName, pwd;
    String stringUserName, stringPwd;
    JSONObject jsonUserDetails;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userName = (EditText)findViewById(R.id.userName);
        pwd = (EditText)findViewById(R.id.pwd);
        btn = (Button)findViewById(R.id.btn);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        stringUserName = userName.getText().toString();
        stringPwd = pwd.getText().toString();
        jsonUserDetails = new JSONObject();
        String messageToSend = null;
        try {
            jsonUserDetails.put("userName", stringUserName);
            jsonUserDetails.put("pwd", stringPwd);
            messageToSend = jsonUserDetails.toString();
        } catch (JSONException e) {
            e.printStackTrace(); }

        MyAsync myAsync = new MyAsync();
        myAsync.execute(messageToSend);
    }

    class MyAsync extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            String responseFromServer = null;
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            OutputStream outputStream = null;
            boolean inClosed = false, outClosed = false;
            try{
                URL url = new URL("http://10.0.2.2:8080/MainServlet");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.setRequestMethod("POST");
                urlConnection.connect();

                outputStream = urlConnection.getOutputStream();
                outputStream.write(("action=send&json=" + params[0]).getBytes());
                outputStream.close();
                outClosed = true;

                inputStream = urlConnection.getInputStream();
                byte[] buffer = new byte[1024];
                int actuallyRead = inputStream.read(buffer);
                inputStream.close();
                inClosed = true;
                if(actuallyRead != -1){
                    responseFromServer = new String(buffer,0,actuallyRead);
                }

            }catch (MalformedURLException e){e.printStackTrace();
            }catch (IOException e){e.printStackTrace();
            }finally {
                if(!inClosed && inputStream != null)
                    try{inputStream.close();} catch (IOException e) {
                        e.printStackTrace();
                    }
                if(!outClosed && outputStream !=null)
                    try{outputStream.close();} catch (IOException e) {
                        e.printStackTrace();
                    }
                if(urlConnection != null){urlConnection.disconnect();}
            }
            return responseFromServer;
        }

        @Override
        protected void onPostExecute(String s) {
            String[]keyValue = s.split("=");
            if(keyValue[0] == "result" && keyValue[1] =="OK") {
                Intent intent = new Intent(MainActivity.this, ListUsers.class);
                startActivity(intent);
            }
            else return;
        }
    }
}
