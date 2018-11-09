package Sockets.Client.Startup;

import Sockets.Client.View.NonBlockingInterpreter;

/**
 * class for starting the client side of the game
 */
public class Main {
    public static void main(String[] args) {
        new NonBlockingInterpreter().start();
    }
}
