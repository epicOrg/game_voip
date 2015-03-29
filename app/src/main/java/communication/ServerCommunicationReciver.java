package communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Luca on 28/03/2015.
 */
public class ServerCommunicationReciver extends Thread{

    private Socket socket;
    private BufferedReader reader;

    public ServerCommunicationReciver(Socket socket) throws IOException{
        this.socket = socket;
        //reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        super.run();

        //TODO
    }


}
