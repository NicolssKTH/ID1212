package Sockets.Client.Net;

/**
 * Interface for the listener which is handling callbacks from the server
 */
public interface OutputHandler {
    void handleMsg(String msg);
}
