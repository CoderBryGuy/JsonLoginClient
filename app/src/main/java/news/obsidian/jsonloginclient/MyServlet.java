package news.obsidian.jsonloginclient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Bryan on 6/14/2016.
 */
public class MyServlet extends javax.servlet.HttpServlet {

    private String requestString;
    private JSONObject jsonUsers;

    protected void doPost(javax.servlet.http.HttpServletRequest request,
                          javax.servlet.http.HttpServletResponse response ){
    int actuallyRead=0;
            InputStream inputStream = request.getInputStream();
        byte[] buffer = new byte[1024];
        try {
            actuallyRead = inputStream.read(buffer);
            if(actuallyRead == -1)
                return;
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        requestString = new String(buffer,0,actuallyRead);
        String result = processQueryString(requestString);

        OutputStream outputStream = response.getOutputStream();
        try {
            outputStream.write((result == null ? "error": result).getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String processQueryString(String stringJson) {
        String[] keyValuePars = stringJson.split("&");
        if(keyValuePars.length > 0)
        {
            String[] keyValue = keyValuePars[0].split("=");
            if(keyValue.length != 2)
                return null;
            if(keyValue[0] != "action")
                return null;
            String action = keyValue[1];
            switch (action) {
                case "send":
                    String[] keyJsonOb = keyValuePars[1].split("=");
                    if(keyJsonOb[0] != "json")
                        return null;
                    try {
                        jsonUsers = new JSONObject(keyJsonOb[1]);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return "result=ok";
                case "get":
                    return "json=" + jsonUsers.toString();
            }
        }
    }
}
