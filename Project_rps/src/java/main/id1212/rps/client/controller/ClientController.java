package java.main.id1212.rps.client.controller;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.main.id1212.rps.client.net.OutputHandler;
import java.main.id1212.rps.client.net.ServerConnection;
import java.util.concurrent.CompletableFuture;

public class ClientController {
    private final ServerConnection serverConnection = new ServerConnection();

    public void connect(String host, int port, OutputHandler outputHandler){
        CompletableFuture.runAsync(() -> {
            try {
                serverConnection.connect(host,port, outputHandler);
            }catch (IOException ioe){
                throw new UncheckedIOException(ioe);
            }
        }).thenRun(() -> outputHandler.handleMsg("Connected to " + host + ":" + port));
    }
    public void disconnect() throws IOException{
        serverConnection.disconnect();
    }

    public void sendUsername(String username){
        CompletableFuture.runAsync(()-> serverConnection.sendUsername(username));
    }

    public void sendChoice(String choice){
        CompletableFuture.runAsync(() -> serverConnection.playerChoice(choice));
    }
}
