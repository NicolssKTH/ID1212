package Sockets.Server.Controller;
import Sockets.Server.Model.WordHandler;

/**
 * ServerController used by classes in the net layer to communicate with the server model
 */
public class ServerController {
    private final WordHandler model;

    public ServerController(){
        this.model = new WordHandler();
    }

    public String getWord(){
        return model.getWord();
    }

    public String processGuess(String guess, String word, String hidden){
        return model.processGuess(guess, word, hidden);
    }
}
