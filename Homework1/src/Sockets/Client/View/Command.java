package Sockets.Client.View;

/**
 * the commands the user can do while playing
 */
public enum Command {
    CONNECT("Connect to the server. " + "USAGE: 'CONNECT <IP> <PORT>' for specified IP and Port OR 'CONNECT', to use default"),

    GUESS("Send a Guess to the server. " + "USAGE: 'GUESS <LETTER/WORD>'"),

    NEWWORD("Start the game/give a new word. " + "USAGE: 'NEWWORD'"),

    DISCONNECT("Disconnect from the server. " + "USAGE: 'DISCONNECT'"),

    HELP("Gives a list with commands."),

    QUIT("Quit the program"),

    INCORRECT_FORMAT("When no correct input");

    private final String descriptor;

    Command(String descriptor){
        this.descriptor = descriptor;
    }

    public String getDescriptor(){
        return this.descriptor;
    }
}
