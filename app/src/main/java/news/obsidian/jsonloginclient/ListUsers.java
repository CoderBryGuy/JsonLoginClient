package news.obsidian.jsonloginclient;

import android.app.Activity;
import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ListUsers extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);



        MyAsync myAsync = new MyAsync();
        myAsync.execute();
    }

   class MyAsync extends AsyncTask<Void, Void, String>{

       @Override
       protected String doInBackground(Void... params) {
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
               urlConnection.setRequestProperty("Content-Type", "text/plain");
               urlConnection.setRequestMethod("POST");
               urlConnection.connect();

               outputStream = urlConnection.getOutputStream();
               outputStream.write(("action=get").getBytes());
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
           try {
               JSONObject jsonUserList = new JSONObject(s);
//do something here
           } catch (JSONException e) {
               e.printStackTrace();
           }
       }
   }
}
