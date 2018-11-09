package Sockets.Client.Controller;

import Sockets.Client.Net.OutputHandler;
import Sockets.Client.Net.ServerConnection;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;

/**
 * Client controller used by all users for communicate with server.
 * the communication is done by seperate threads through the use of CompletableFuture class.
 */
public class Controller {
    private final ServerConnection serverConnection = new ServerConnection();

    /**
     * connect to a server with a IP and a port.
     * @param host the IP
     * @param port the port
     * @param outputHandler the outputHandler
     */
    public void connect(String host, int port, OutputHandler outputHandler){
        CompletableFuture.runAsync(() -> {
            try{
                serverConnection.connect(host, port, outputHandler);
            }catch (IOException ioe){
                throw new UncheckedIOException(ioe);
            }
        }).thenRun(() -> outputHandler.handleMsg("Connected to " + host + ":" + port));
    }

    /**
     * disconnect from server
     */
    public void disconnect(){
        CompletableFuture.runAsync(() -> {
            try {
                serverConnection.disconnect();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }
    public void newGame(){
        CompletableFuture.runAsync(() -> serverConnection.newGame());
    }

    public void sendGuess(String command){
        CompletableFuture.runAsync(() -> serverConnection.sendGuess(command));
    }
}
