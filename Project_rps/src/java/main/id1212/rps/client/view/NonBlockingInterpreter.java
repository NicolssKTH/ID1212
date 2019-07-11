package java.main.id1212.rps.client.view;

import java.io.IOException;
import java.main.id1212.rps.client.controller.ClientController;;
import java.main.id1212.rps.client.net.OutputHandler;
import java.util.ArrayList;
import java.util.Scanner;


public class NonBlockingInterpreter implements Runnable{
    private static final String PROMT = "> ";
    private final Scanner scanner = new Scanner(System.in);
    private boolean receivingCmds = false;
    private ClientController controller;
    private final ThreadSafeStdOut outMgr = new ThreadSafeStdOut();
    private ArrayList<String> arr;

    public void start(){
        if (receivingCmds){
            return;
        }
        receivingCmds = true;
        controller = new ClientController();
        new Thread(this).start();
    }

    @Override
    public void run(){
        while (receivingCmds){
            try {
                String readLine = scanner.nextLine();
                parseline(readLine);
                Command cmd = getCommand(arr);
                switch (cmd){
                    case CONNECT:
                        controller.connect(arr.get(1), Integer.parseInt(arr.get(2)), new ConsoleOutput());
                        break;

                    case QUIT:
                        receivingCmds = false;
                        controller.disconnect();
                        break;

                    case CHOICE:
                        controller.sendChoice(arr.get(1));
                        break;

                    case HELP:
                        System.out.println("help message");
                        break;

                    case NOCOMMAND:
                        System.out.println("That is not a valid command");
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Command getCommand(ArrayList<String> arr) {
        if (arr.isEmpty()){
            return null;
        }
        switch (arr.get(0)){
            case "CONNECT":
                return Command.CONNECT;

            case "QUIT":
                return Command.QUIT;

            case "CHOICE":
                return Command.CHOICE;

            case "HELP":
                return Command.HELP;

                default:
                    return Command.NOCOMMAND;
        }
    }

    private class ConsoleOutput implements OutputHandler{
        @Override
        public void handleMsg(String msg){
            outMgr.print(msg);
        }
    }

    private void parseline(String input){

        for (String substring : input.trim().split(" ")){
            arr.add(substring);
        }
    }
}
