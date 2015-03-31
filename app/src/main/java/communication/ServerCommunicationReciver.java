package communication;

import android.app.Activity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

import services.Service;

/**
 * Created by Luca on 28/03/2015.
 */
public class ServerCommunicationReciver extends Thread{

    private BufferedReader reader;
    private Activity context;
    private RequestElaborator requestElaborator = new RequestElaborator();

    public ServerCommunicationReciver(BufferedReader reader) throws IOException{
        this.reader = reader;
    }

    @Override
    public void run() {
        String line;
        JSONObject received;
        while(true){
            try {
                line = reader.readLine();
                if(line != null){
                    received = new JSONObject(line);
                    Service service = requestElaborator.setService(received);
                }
            }catch (JSONException e) {
                    e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //TODO
    }

    public void setContext(Activity context) {
        Log.d("SETCONTEXT", "contex_setted");
        this.context = context;
    }
}
