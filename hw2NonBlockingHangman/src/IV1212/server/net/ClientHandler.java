package IV1212.server.net;

import IV1212.common.Message;
import IV1212.common.MessageType;
import IV1212.server.model.Game;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;

import static IV1212.common.Message.deserialize;
import  static IV1212.common.Message.serialize;

public class ClientHandler implements  Runnable {
    private final GameServer server;
    private final SocketChannel clientChannel;
    private final ByteBuffer clientMessage = ByteBuffer.allocateDirect(8192);
    private final LinkedBlockingQueue<Message> sendingQueue = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<Message> readingQueue = new LinkedBlockingQueue<>();

    private SelectionKey selectionKey;
    private Game hangmanGame = new Game();

    public ClientHandler(GameServer server, SocketChannel clientChannel) {
        this.server = server;
        this.clientChannel = clientChannel;
    }

    @Override
    public void run() {
        Iterator<Message> iterator = readingQueue.iterator();
        while (iterator.hasNext()) {
            Message message = iterator.next();
            switch (message.getMessageType()) {
                case START:
                    String currentState = this.hangmanGame.startRound();
                    sendResponseToClient(MessageType.START_RESPONSE, currentState);
                    break;
                case GUESS:
                    String currentState1 = this.hangmanGame.validateGuess(message.getBody());
                    if (this.hangmanGame.getChosenWord() == null) {
                        sendResponseToClient(MessageType.END_RESPONSE, currentState1);
                    } else {
                        sendResponseToClient(MessageType.GUESS_RESPONSE, currentState1);
                    }
                    break;
                case QUIT:
                    disconnectClient();
                    break;
                default:
                    System.out.println("Invalid message type");
            }
            iterator.remove();
        }

    }

    private void sendResponseToClient(MessageType messageType, String body) {
        Message message = new Message(messageType, body);

        synchronized (sendingQueue) {
            sendingQueue.add(message);
        }

        server.addMessageToWritingQueue(this.selectionKey);
        server.wakeupSelector();
    }

    public void setSelectionKey(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }

    public void readMessage() throws IOException {
        clientMessage.clear();
        int numOfReadBytes = clientChannel.read(clientMessage);
        if (numOfReadBytes == -1) throw new IOException("Client has closed connection");

        readingQueue.add(deserialize(extractMessageFromBuffer()));
        ForkJoinPool.commonPool().execute(this);
    }

    private String extractMessageFromBuffer() {
        clientMessage.flip();
        byte[] bytes = new byte[clientMessage.remaining()];
        clientMessage.get(bytes);
        return new String(bytes);
    }

    public void writeMessage() throws IOException {
        synchronized (sendingQueue) {
            while (sendingQueue.size() > 0) {
                ByteBuffer message = ByteBuffer.wrap(serialize(sendingQueue.poll()).getBytes());
                clientChannel.write(message);
            }
        }
    }

    public void disconnectClient() {
        try {
            clientChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
