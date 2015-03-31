package communication;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.gamevoip.epicorg.gamevoip.R;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import android.os.Process;

import interaction.CustomAlertDialog;

/**
 * Classe di gestione scambio dati con il server
 *
 * Metododo di utilizzo:
 * la prima volta che si deve usare va chiamato il metodo init!!!
 * e ogni volta che cambio activity devo settare il context!!!
 *
 * Created by Luca on 31/03/2015.
 */
public class CommunicationManager {

    public static final String SERVER_ADDRESS = "192.168.1.4";
    public static final int SERVER_PORT = 7007;

    private Context context;

    private static CommunicationManager instance = new CommunicationManager();;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    private CommunicationManager(){}

    public void init() {

        try {
            socket = new Socket(InetAddress.getByName(SERVER_ADDRESS), SERVER_PORT);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            new CustomAlertDialog(context.getString(R.string.dialog_error),
                    context.getString(R.string.dialog_net_error), context.getString(R.string.dialog_try_again), context);
            Process.killProcess(Process.myPid());
        }
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("EXCEPTION", ex.toString());
        }
        return null;
    }

    public static CommunicationManager getInstance() {
        return instance;
    }

    public void send(JSONObject object){
        writer.println(object.toString());
    }
}
