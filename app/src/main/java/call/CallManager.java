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
import communication.CommunicationManager;

/**
 * Created by Luca on 29/03/2015.
 */
public class CallManager{

    public static final String SERVER_ADDRESS = "192.168.1.4";
    public static final int PORT = 7007;

    private static CallManager instance = new CallManager();
    private AudioCallManager audioCallManager = AudioCallManager.getInstance();
    private CommunicationManager communicationManager;

    private CallManager(){
        communicationManager = CommunicationManager.getInstance();
    }

    public static CallManager getInstance(){
        return  instance;
    }

    public void makeCall(String callee, String caller) throws SocketException, UnknownHostException {

        AudioStream stream = audioCallManager.newAudioStream();

        JSONObject callRequest = new JSONObject();
        try {
            callRequest.put("service", "Call");
            callRequest.put("callee", callee);
            callRequest.put("caller", caller);
            callRequest.put("port", stream.getLocalPort());
            Log.d("CallRequest", callRequest.toString());

            communicationManager.send(callRequest);
            //TODO
            /**if(jsonObject1.getBoolean("value")){
                stream.associate(InetAddress.getByName(jsonObject1.getString("ip"))
                    ,jsonObject1.getInt("port"));

            }*/

            
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
