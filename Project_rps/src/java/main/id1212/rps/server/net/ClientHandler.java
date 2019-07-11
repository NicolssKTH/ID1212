package java.main.id1212.rps.server.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.main.id1212.rps.common.Message;
import java.main.id1212.rps.common.MessageWrapper;
import java.main.id1212.rps.server.controller.ControllerObserver;
import java.net.Socket;

class ClientHandler{
    private final ControllerObserver controllerObserver;
    private Socket clientSocket;
    private ObjectOutputStream toClient;
    private ObjectInputStream fromClient;

    ClientHandler(Socket socket, ControllerObserver controllerObserver){
        this.clientSocket = socket;
        this.controllerObserver = controllerObserver;
    }

    void run(){
        try{
            toClient = new ObjectOutputStream(clientSocket.getOutputStream());
            fromClient = new ObjectInputStream(clientSocket.getInputStream());

            MessageWrapper message = (MessageWrapper) fromClient.readObject();

            switch (message.getMessage()){
                case JOIN:
                    controllerObserver.addPlayer(message.getSenderPlayer());
                    toClient.writeObject(new MessageWrapper(Message.SYNC, controllerObserver.getPlayer()));
                    break;
                case QUIT:
                    controllerObserver.removePlayer(message.getSenderPlayer());
                    break;
                case MOVE:
                    controllerObserver.setPlayerMove(message.getMove(), message.getSenderPlayer());
                    break;

                    default:
                        System.out.println("This should not happen");
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}