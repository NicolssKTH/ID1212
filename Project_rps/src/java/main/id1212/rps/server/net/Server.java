package java.main.id1212.rps.server.net;

import java.io.IOException;
import java.main.id1212.rps.server.controller.ServerController;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Server  {

    private static final int LINGER_TIME = 5000;
    private static final int TIMEOUT_HALF_HOUR = 1800000;
    private final ServerController controller = new ServerController();
    private final List<ClientHandler> clients = new ArrayList<>();
    private int portNo = 8080;


    public static void main(String[] args) {
        Server server = new Server();
        server.parseArguments(args);
        server.serve();
    }

    void updatepaleyerState(String msg){
        controller.appendEntry(msg);
        synchronized (clients){
            clients.forEach((client) -> client.sendMsg(msg));
        }
    }

    void removeHandler(ClientHandler handler){
        synchronized (clients){
            clients.remove(handler);
        }
    }

    private void serve(){
        try{
            ServerSocket listeningSocket = new ServerSocket(portNo);
            while (true){
                Socket clientSocket = listeningSocket.accept();
            }
        }catch (IOException e){
            System.err.println("server failure");
        }
    }

    private void startHandler(Socket clientSocket) throws SocketException{
        clientSocket.setSoLinger(true, LINGER_TIME);
        clientSocket.setSoTimeout(TIMEOUT_HALF_HOUR);
        ClientHandler handler = new ClientHandler(this, clientSocket, );
        synchronized (clients){
            clients.add(handler);
        }
    }

    private void parseArguments(String[] arguments){
        if (arguments.length > 0){
            try{
                portNo = Integer.parseInt(arguments[1]);
            }catch (NumberFormatException e){
                System.err.print("Invalid port number, default will be used.");
            }
        }
    }
}
