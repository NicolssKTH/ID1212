package java.main.id1212.rps.client.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ServerConnection {
    private static final int TIMEOUT_HALF_HOUR = 1800000;
    private static final int TIMEOUT_HALF_MINUTE = 30000;
    private Socket socket;
    private PrintWriter toServer;
    private BufferedReader fromServer;
    private volatile boolean connected;

    public void connect(String host, int port, OutputHandler broadcasstHandler)throws IOException{
        socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), TIMEOUT_HALF_MINUTE);
        socket.setSoTimeout(TIMEOUT_HALF_HOUR);
        connected = true;
        boolean autoFlush = true;
        toServer = new PrintWriter(socket.getOutputStream(), autoFlush);
        fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        new Thread(new Listener(broadcasstHandler)).start();
    }

    public void disconnect() throws IOException{
        socket.close();
        socket = null;
        connected = false;
    }

    public void sendUsername(String username){
        sendMsg("USER " + username);
    }

    public void playerChoice(String msg){
        sendMsg("PLAY " + msg);
    }

    private void sendMsg(String msg){
        toServer.println(msg);
    }

    private class Listener implements Runnable{
        private final OutputHandler outputHandler;

        private Listener(OutputHandler outputHandler){
            this.outputHandler = outputHandler;
        }

        @Override
        public void run() {
            try{
                for (;;){
                    outputHandler.handleMsg(fromServer.readLine());
                }
            }catch (Throwable connectionFailure){
                if (connected){
                    outputHandler.handleMsg("Lost connection");
                }
            }
        }
    }
}
