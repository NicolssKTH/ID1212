package java.main.id1212.rps.common;

import java.io.Serializable;
import java.main.id1212.rps.server.model.Player;

public class MessageWrapper implements Serializable {
    private Message message;
    private Player senderPlayer;
    private String move;

    public MessageWrapper(Message message, Player senderPlayer){
        this.message = message;
        this.senderPlayer = senderPlayer;
        this.move = null;
    }

    public MessageWrapper(Message message, Player senderPlayer, String move){
        this.message = message;
        this.senderPlayer = senderPlayer;
        this.move = move;
    }

    public Message getMessage() {
        return message;
    }

    public Player getSenderPlayer() {
        return senderPlayer;
    }

    public String getMove() {
        return move;
    }

    @Override
    public String toString(){
        return "MessageWrapper{" +
                "message=" + message +
                ", senderPeerInfo=" + senderPlayer +
                ", move='" + move + '\'' +
                '}';
    }
}
