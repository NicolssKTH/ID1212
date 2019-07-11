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

public class ServerConnection implements Runnable {

    private final ByteBuffer serverMessage = ByteBuffer.allocateDirect(8192);
    private final LinkedBlockingQueue<Message> sendingQ = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<Message> readingQ = new LinkedBlockingQueue<>();
    private CommunicationListerner viewObserver;
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

    public void setViewObserver(CommunicationListerner observer) {
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
                    if (key.isConnectable()) establishConnection(key);
                    else if (key.isReadable()) readFromServer(key);
                    else if (key.isWritable()) writeToServer(key);

                    selector.selectedKeys().remove(key);
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void writeToServer(SelectionKey key) {
    }

    private void readFromServer(SelectionKey key) throws IOException {
        serverMessage.clear();
        int numOfReadBytes = socketChannel.read(serverMessage);
        if (numOfReadBytes == -1) throw new IOException("CLient closed connection");

        readingQ.add(Message.deserialize(extractMessageFromBuffer()));

        while (readingQ.size() > 0) {
            Message message = readingQ.poll();

            switch (message.getMessageType()){
                case START_RESPONSE:
                    viewObserver.print("some message");
                    break;
                case GUESS_RESPONSE:
                    viewObserver.print("message");
                    break;
                case END_RESPONSE:
                    viewObserver.print("mesasge");
                    break;

                default:
                    viewObserver.print(message.getBody());

            }
        }
    }

    private String extractMessageFromBuffer() {

    }

    private void establishConnection(SelectionKey key) throws IOException {
        this.socketChannel.finishConnect();
        viewObserver.print("message");
        key.interestOps(SelectionKey.OP_WRITE);
    }

    private void initSelector() throws IOException {
        this.selector = SelectorProvider.provider().openSelector();

        this.socketChannel = SocketChannel.open();
        this.socketChannel = configureBlocking(false);
        this.socketChannel = connect(serverAddress);
        this.socketChannel.register(selector, SelectionKey.OP_CONNECT);
        this.connected = true;
    }

}
