package call;

import android.net.rtp.AudioStream;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import call.audio.AudioCallManager;

/**
 * Created by Luca on 29/03/2015.
 */
public class CallManager{

    private static CallManager instance = new CallManager();
    private AudioCallManager audioCallManager = AudioCallManager.getInstance();
    public static final int PORT = 7007;
    public static final String SERVER_ADDRESS = "192.168.1.4";

    private CallManager(){
        
    }

    public static CallManager getInstance(){
        return  instance;
    }

    public void makeCall(String callee, String caller) throws SocketException, UnknownHostException {

        
        AudioStream stream = audioCallManager.newAudioStream();

        JSONObject jsonObject = new JSONObject();
        try {
            Socket socket = new Socket(InetAddress.getByName(SERVER_ADDRESS),PORT);
            
            jsonObject.put("service", "Call");
            jsonObject.put("callee", callee);
            jsonObject.put("caller",caller );
            jsonObject.put("ip", stream.getLocalAddress());
            jsonObject.put("port", stream.getLocalPort());
            Log.d("CallRequest", jsonObject.toString());

            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println(jsonObject.toString());
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            JSONObject jsonObject1 = new JSONObject(reader.readLine());
            Log.d("CallResponse", jsonObject1.toString());

            if(jsonObject1.getBoolean("value")){
                stream.associate(InetAddress.getByName(jsonObject1.getString("ip"))
                    ,jsonObject1.getInt("port"));

            }

            
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //TODO
    }
}
