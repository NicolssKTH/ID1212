package Sockets.Client.View;

import static Sockets.Common.Constants.*;

/**
 * som getters
 */
public class CmdLine {
    private final String DELIMETER = " ";
    private String message;
    private Command cmd;
    private String body;

    public CmdLine(String command){
        extract(command);
    }
    public String getMessage(){
        return this.message;
    }

    public Command getCmd() {
        return this.cmd;
    }

    public String getBody(){
        return this.body;
    }

    private void extract(String message){
        this.message = message;
        extractCmd(message);
        extractBody(message);
    }

    private void extractCmd(String command){
        String[] split = command.split(DELIMETER);
        try {
            this.cmd = Command.valueOf(split[TYPE_INDEX].toUpperCase());
        }catch (IllegalArgumentException e){
            this.cmd = Command.INCORRECT_FORMAT;
        }
    }
    private void extractBody(String command){
        String[] commands = command.split(DELIMETER);
        try {
            this.body = commands[MESSAGE_INDEX];
        }catch (IndexOutOfBoundsException e){
            this.body = null;
        }
    }
}
