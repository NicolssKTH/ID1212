package IV1212.client.view;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class CommandParser {
    private ArrayList<String> arguments = new ArrayList<>();
    private Command command;

    public CommandParser(String input){
        determineCommand(input);
    }

    private void determineCommand(String input) {
        StringTokenizer stringTokenizer = new StringTokenizer(input);

        if (stringTokenizer.countTokens() == 0){
            this.command = Command.NO_COMMAND;
            return;
        }

        String cmd = stringTokenizer.nextToken().toUpperCase();
        switch (cmd) {
            case "CONNECT":
                if (!stringTokenizer.hasMoreTokens()){
                    throw new IllegalArgumentException("Missing IP Address");
                }
                this.command = Command.CONNECT;
                String ip = stringTokenizer.nextToken();
                arguments.add(ip);
                String port = stringTokenizer.nextToken();
                arguments.add(port);
                break;

            case "QUIT":
                this.command = Command.QUIT;
                break;

            case "START":
                this.command = Command.START;
                break;

                default:
                    this.command = Command.GUESS;
                    arguments.add(cmd);
        }
    }
    public String getArgument(int index){
        return arguments.get(index);
    }

    public Command getCommand() {
        return command;
    }
}
