package IV1212.client.net;

import IV1212.common.Message;
import IV1212.common.MessageType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.LinkedBlockingQueue;

import static IV1212.common.Message.deserialize;
import static IV1212.common.Message.serialize;

public class ServerConnection implements Runnable {

    private final ByteBuffer msgFromServer = ByteBuffer.allocateDirect(8192);
    private final LinkedBlockingQueue<Message> sendingQ = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<Message> readingQ = new LinkedBlockingQueue<>();
    private CommunicationListener viewObserver;
    private volatile boolean timeToSend = false;
    private InetSocketAddress serverAddress;
    private SocketChannel socketChannel;
    private boolean connected = false;
    private Selector selector;


    public void connect(String host, int port){
        this.serverAddress = new InetSocketAddress(host, port);
        new Thread(this).start();
    }

    public void startNewRound() {
        enqueueAndSendMessage(MessageType.START, "");
    }

    public void submitGuess(String guess){
        enqueueAndSendMessage(MessageType.GUESS, guess);
    }

    public void disconnect() throws IOException {
        this.connected = false;
        enqueueAndSendMessage(MessageType.QUIT, "");
        this.socketChannel.close();
        this.socketChannel.keyFor(selector).cancel();
    }

    public void setViewObserver(CommunicationListener observer) {
        this.viewObserver = observer;
    }

    private void enqueueAndSendMessage(MessageType messageType, String body) {
        Message message = new Message(messageType, body);
        synchronized (sendingQ) {
            sendingQ.add(message);
        }
        this.timeToSend = true;
        selector.wakeup();
    }


    @Override
    public void run(){
        try {
            initSelector();
            while (connected) {
                if (timeToSend) {
                    socketChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
                    timeToSend = false;
                }

                this.selector.select();

                for (SelectionKey key : this.selector.selectedKeys()) {

                    if (!key.isValid()) continue;
                    if (key.isConnectable()) completeConnection(key);
                    else if (key.isReadable()) readFromServer();
                    else if (key.isWritable()) writeToServer(key);

                    selector.selectedKeys().remove(key);
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void writeToServer(SelectionKey key) throws IOException {
        synchronized (sendingQ) {
            while (sendingQ.size() > 0) {
                ByteBuffer message = ByteBuffer.wrap(serialize(sendingQ.poll()).getBytes());
                socketChannel.write(message);
                if (message.hasRemaining()) return;
            }
        }
        key.interestOps(SelectionKey.OP_READ);
    }

    private void readFromServer() throws IOException {
        msgFromServer.clear();
        int numOfReadBytes = socketChannel.read(msgFromServer);
        if (numOfReadBytes == -1) throw new IOException("Client closed connection");

        readingQ.add(deserialize(extractMessageFromBuffer()));

        while (readingQ.size() > 0) {
            Message message = readingQ.poll();
            

            switch (message.getMessageType()){
                case START_RESPONSE:
                    viewObserver.print("You started a new game!\nYou can guess a letter or the word\nYou have to guess:"+ message.getBody());
                    break;
                case GUESS_RESPONSE:
                    viewObserver.print(message.getBody());
                    break;
                case END_RESPONSE:
                    viewObserver.print("\" The game is ended! Here is the result:" + message.getBody() + "\nStart a new game with 'start'");
                    break;

                default:
                    viewObserver.print(message.getBody());

            }
        }
    }

    private String extractMessageFromBuffer() {
        msgFromServer.flip();
        byte[] bytes = new byte[msgFromServer.remaining()];
        msgFromServer.get(bytes);
        return new String(bytes);
    }

    private void completeConnection(SelectionKey key) throws IOException {
        this.socketChannel.finishConnect();
        viewObserver.print("Connection successful!\n" + "You can start a new game with 'start'");
        key.interestOps(SelectionKey.OP_WRITE);
    }

    private void initSelector() throws IOException {
        this.selector = SelectorProvider.provider().openSelector();

        this.socketChannel = SocketChannel.open();
        this.socketChannel.configureBlocking(false);
        this.socketChannel.connect(serverAddress);
        this.socketChannel.register(selector, SelectionKey.OP_CONNECT);
        this.connected = true;
    }

}
