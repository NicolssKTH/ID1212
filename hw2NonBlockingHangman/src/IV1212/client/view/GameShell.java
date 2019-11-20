package IV1212.client.view;

import IV1212.client.controller.Controller;
import IV1212.client.net.CommunicationListener;

import java.io.IOException;
import java.util.Scanner;

public class GameShell implements Runnable {

    private static final String PROMT = ">> ";
    private final ThreadSafeStdOut outMgr = new ThreadSafeStdOut();
    private final Scanner console = new Scanner(System.in);
    private ConsoleOutput consoleOutput = new ConsoleOutput();
    private boolean running = false;
    private Controller controller;

    public void start(){
        if (running) return;
        running = true;

        controller = new Controller();
        controller.setViewObserver(consoleOutput);

        new Thread(this).start();
    }



    @Override
    public void run() {
        outMgr.println("Welcome to Hangman");
        outMgr.println("Connect to a server with 'connect <ip> <port>'\nQuit the game with 'quit'");
        outMgr.print(PROMT);

        while (running) {
            try{
                CommandParser parsedCommand = new CommandParser(console.nextLine());
                switch (parsedCommand.getCommand()){
                    case CONNECT:
                        controller.connect(
                                parsedCommand.getArgument(0),
                                Integer.parseInt(parsedCommand.getArgument(1)));
                        break;

                    case QUIT:
                        controller.disconnect();
                        running = false;
                        break;

                    case START:
                        controller.startNewRound();
                        break;

                    case GUESS:
                        controller.submitGuess(parsedCommand.getArgument(0));

                    case NO_COMMAND:
                        outMgr.print(PROMT);

                }
            } catch (IOException e) {
                e.printStackTrace();
                outMgr.print(PROMT);
            }
        }
    }

    private class ConsoleOutput implements CommunicationListener{
        @Override
        public void print(String msg) {
            outMgr.println(msg);
            outMgr.print(PROMT);
        }
    }
}
