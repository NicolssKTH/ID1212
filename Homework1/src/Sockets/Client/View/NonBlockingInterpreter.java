package Sockets.Client.View;

import Sockets.Client.Net.OutputHandler;
import Sockets.Client.Controller.Controller;
import java.util.Scanner;

/**
 * class for handling all user communication while playing the game
 */
public class NonBlockingInterpreter implements Runnable{

    private static final String PROMT = "> ";
    private final MyPrinter myPrinter = new MyPrinter();
    private final Scanner console = new Scanner(System.in);
    private final int IP_INDEX = 1;
    private final int PORT_INDEX = 2;
    private String ip = "127.0.0.1";
    private int port = 8080;
    private boolean running = false;
    private boolean connected;
    private Controller contr;


    public void start(){
        if (running){
            return;
        }
        running = true;
        contr = new Controller();
        new Thread(this).start();
    }
    private void disconnect(){
        connected = false;
        myPrinter.println("Disconnecting");
        contr.disconnect();
    }
    private void quit(){
        running = false;
        if (connected){
            contr.disconnect();
        }
        myPrinter.println("Quitting");
    }

    private int max(int a, int b){
        if (a < b)
            return b;
        return a;
    }

    /**
     * connect to a IP and a port, if non is given use default
     * @param IPport IP and port
     */
    private void connect(String IPport){
        String[] s = IPport.split(" ");
        if (s.length <= max(PORT_INDEX, IP_INDEX)){
            myPrinter.println("Incorrect input of  IP and port");
        }else{
            try {
                port = Integer.parseInt(s[PORT_INDEX]);
                ip = s[IP_INDEX];
                myPrinter.println("Using IP: " + ip + " Port: " + port);
            }catch (NumberFormatException e){
                myPrinter.println("error when parsing portnumber, using default values");
            }
        }
        myPrinter.println("Connection...");
        contr.connect(ip, port, new ConsoleOutput());
        connected = true;
    }
    private void notConnected(){
        myPrinter.println("Not Connected");
    }

    private void startGame(){
        contr.newGame();
    }
    private void sendGuess(String guess){
        contr.sendGuess(guess);
    }
    /**
     * Main method for reading user input. Each row is saved as a new 'CommandLine'
     * object, which splits the input into a command and a body.
     */
    @Override
    public void run() {
        myPrinter.println(usageMessage("Welcome"));
        while (running){
            try {
                CmdLine cmdLine = new CmdLine(readNextLine());
                switch (cmdLine.getCmd()){
                    case DISCONNECT:
                        if (connected)
                            disconnect();
                        else
                            myPrinter.println("not yet connected");
                        break;
                    case QUIT:
                        quit();
                        break;
                    case CONNECT:
                        if (!connected)
                        connect(cmdLine.getMessage());
                        else
                            myPrinter.println("already connected");
                        break;
                    case GUESS:
                        if (connected){
                            sendGuess(cmdLine.getBody());
                        }else {
                            notConnected();
                        }
                        break;
                    case NEWWORD:
                        if (connected){
                            startGame();
                        }else {
                            notConnected();
                        }
                        break;
                    case HELP:
                        myPrinter.println(usageMessage(""));
                        break;
                    default:
                        myPrinter.println("Incorrect input, type HELP for commands");
                }
            }catch (Exception e){
                myPrinter.println("Operation failed");
            }
        }
    }

    /**
     * Returns the default usage message for an user. This includes all
     * commands and their descriptors
     * @param current   If an initial message is wanted, append the commands to this message
     * @return          a constructed usage message
     */
    private String usageMessage(String current){
        StringBuilder sb = new StringBuilder(current);
        sb.append("\nCommands:\n");
        for (Command c : Command.values()){
            sb.append(c + " - " + c.getDescriptor() + "\n");
        }
        sb.append("\nNOTE: Seperate commands from their respective arguments using spaces");
        return sb.toString();
    }
    private String readNextLine(){
        myPrinter.print(PROMT);
        return console.nextLine();
    }
    private class ConsoleOutput implements OutputHandler{
        @Override
        public void handleMsg(String msg) {
            myPrinter.println(msg);
            myPrinter.print(PROMT);
        }
    }
}
