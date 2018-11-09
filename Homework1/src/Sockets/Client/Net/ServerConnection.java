package Sockets.Client.Net;

import Sockets.Common.Constants;
import Sockets.Common.MsgType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Class who is responsible for handling serverconnection for a client
 */
public class ServerConnection {
    private static final int TIMEOUT_HALF_HOUR = 1800000;
    private static final int TIMEOUT_HALF_MINUTE = 30000;
    private Socket socket;
    private PrintWriter toServer;
    private BufferedReader fromServer;
    private volatile boolean connected;

    /**
     * method for connection to a server
     * @param host IP adress
     * @param port the portnumber
     * @param outputHandler outputhandler which is passed to a listener
     * @throws IOException
     */
    public void connect(String host, int port, OutputHandler outputHandler) throws IOException{
        socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), TIMEOUT_HALF_MINUTE);
        socket.setSoTimeout(TIMEOUT_HALF_HOUR);
        connected = true;
        boolean autoFlush = true;
        toServer = new PrintWriter(socket.getOutputStream(), autoFlush);
        fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        new Thread(new Listener(outputHandler)).start();
    }

    /**
     * disconnect from the server, called by the user
     * @throws IOException
     */
    public void disconnect() throws IOException{
        sendCommand(MsgType.DISCONNECT.toString());
        socket.close();
        socket = null;
        connected = false;
    }

    public void sendGuess(String guess){
        sendCommand(MsgType.GUESS + Constants.DELIMETER + guess);
    }

    public void newGame(){
        sendCommand(MsgType.NEWWORD.toString());
    }

    private void sendCommand(String command){
        if (connected){
            toServer.println(command);
        }
    }

    private class Listener implements Runnable{
        private final OutputHandler outputHandler;

        private Listener(OutputHandler outputHandler){
            this.outputHandler = outputHandler;
        }

        /**
         * listening for message
         */
        @Override
        public void run() {
            try{
                for (;;){
                    outputHandler.handleMsg(formatMsg(fromServer.readLine()));
                }
            }catch (Throwable connectionFailure){
                if (connected){
                    outputHandler.handleMsg("Lost connection");
                }
            }
        }

        /**
         * Extract the message received
         * @param entireMsg the message received
         * @return the message without the type
         */
        private String formatMsg(String entireMsg){
            String[] message = entireMsg.split(Constants.DELIMETER);
            if (MsgType.valueOf(message[Constants.TYPE_INDEX]) == MsgType.RESPONSE){
                return message[Constants.MESSAGE_INDEX];
            }else if (MsgType.valueOf(message[Constants.TYPE_INDEX]) == MsgType.DISCONNECT){
                return "DISCONNECTED FROM SERVER";
            }else {
                return message[Constants.MESSAGE_INDEX];
            }
        }
    }
}
