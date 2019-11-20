package IV1212.client.controller;

import IV1212.client.net.CommunicationListener;
import IV1212.client.net.ServerConnection;

import java.io.IOException;

public class Controller {
    ServerConnection serverConnection = new ServerConnection();

    public void connect(String host, int port){
        serverConnection.connect(host,port);
    }

    public void startNewRound(){
        serverConnection.startNewRound();
    }

    public void submitGuess(String guess){
        serverConnection.submitGuess(guess);
    }

    public void disconnect() throws IOException {
        serverConnection.disconnect();
    }

    public void setViewObserver(CommunicationListener observer){
        serverConnection.setViewObserver(observer);
    }
}
