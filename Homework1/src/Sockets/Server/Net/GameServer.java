package Sockets.Server.Net;
import Sockets.Server.Controller.ServerController;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * server for handling new connections for new players
 */
public class GameServer {
    private static final int LINGER_TIME = 30000;
    private static final int TIMEOUT_HALF_HOUR = 1800000;
    private final ServerController contr = new ServerController();
    private int portNo = 8080;

    public static void main(String[] args) {
        GameServer server = new GameServer();
        server.parseArguments(args);
        server.serve();
    }

    /**
     * creating a new game for new players
     */
    private void serve(){
        try{
            ServerSocket listeningSocket = new ServerSocket(portNo);
            while (true){
                Socket clientSocket = listeningSocket.accept();
                startGame(clientSocket);
            }
        }catch (IOException e){
            System.out.println("Error when creating server socket with port " + portNo);
        }
    }

    /**
     * Create a new thread for a user
     * @param clientSocket the client socket that the thread communicate with
     * @throws SocketException
     */
    private void startGame(Socket clientSocket) throws SocketException{
        clientSocket.setSoLinger(true, LINGER_TIME);
        clientSocket.setSoTimeout(TIMEOUT_HALF_HOUR);
        ClientHandler handler = new ClientHandler(contr, clientSocket);
        Thread handlerThread = new Thread(handler);
        handlerThread.setPriority(Thread.MAX_PRIORITY);
        handlerThread.run();
    }

    /**
     * parsing arguments when connection to server, if no porn nr is entered use default
     * @param arguments arguments when connecting
     */
    private void parseArguments(String[] arguments){
        if (arguments.length > 0){
            try{
                portNo = Integer.parseInt(arguments[0]);
            }catch (NumberFormatException e){
               System.out.println("Invalid port number, using default " + portNo);
            }
        }
    }
}