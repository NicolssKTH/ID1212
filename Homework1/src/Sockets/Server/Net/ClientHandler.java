package Sockets.Server.Net;
import Sockets.Server.Controller.ServerController;
import Sockets.Common.Constants;
import Sockets.Common.MsgType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for handling client actions and responses
 */
class ClientHandler implements Runnable{
    private final Socket clientSocket;
    private final ServerController contr;
    private String currentWord;
    private String hiddenWord;
    private int currentScore;
    private int tries;
    private volatile boolean connected;
    private boolean playing;
    private List<String> guesses;


    ClientHandler(ServerController controller, Socket clientSocket){
        this.contr = controller;
        this.clientSocket = clientSocket;
        connected = true;
        this.currentScore = 0;
    }

    /**
     * Close the client socket
     */
    private void disconnectClient(){
        try {
            clientSocket.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        connected = false;
    }

    /**
     * Check if the player has guessed all characters correct
     * @return True or false
     */
    private boolean completeWord(){
        return currentWord.equals(hiddenWord);
    }

    /**
     * Message when winning the game.
     * @return the winning screen
     */
    private String gameDone(){
        currentScore++;
        playing = false;
        String response = "YEEEY, You completed the word: " + currentWord + " with " + tries + " tries remaining. " +
                "Current score: " + currentScore + ". type 'NEWWORD' to play again";
        return response;
    }

    /**
     * Game over message and decrementing score
     * @return game over screen
     */
    private String gameOver(){
        currentScore--;
        playing = false;
        String response = "Game Over. The word was " + currentWord + ". Your score is " + currentScore + ". type 'NEWWORD' to play again";
        return response;
    }

    /**
     * Message when guessing a correct letter, also check if the word is complete
     * @return a message for correct guess
     */
    private String correctGuess(){
        if (completeWord()){
            return gameDone();
        }
        String response = "Correct guess! Current word: " + hiddenWord + ", " + tries + "Guesses left";
        return response;
    }

    /**
     * Message when guessing wrong letter
     * @return message when guessing wrong letter
     */
    private String inCorrectGuess(){
        String response = "incorrect guess! Current word: " + hiddenWord + ", " + tries + " Guesses left";
        return response;
    }

    /**
     * message when you try to guess on something else than letters
     * @return reponse when entering invalid guess
     */
    private String invalidGuess() {
        String response = "You can only guess on a letter or the entire word";
        return response;
    }

    /**
     * Initiate a new game by taking a new word and resetting hidden word with underscore,
     * resetting remaining tries and guessed letters
     */
    private void newGame(){
        currentWord = contr.getWord();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < currentWord.length(); i++){
            sb.append("_ ");
        }
        hiddenWord = sb.toString();
        tries = currentWord.length();
        guesses = new ArrayList<String>();
    }

    /**
     * check if the guess was correct
     * @param newHidden an updated hidden word
     * @return true or false depending if the guess was correct
     */
    private boolean checkResponse(String newHidden){
        if (newHidden.equals(hiddenWord)){
            return false;
        }else {
            return true;
        }
    }

    /**
     * Check if the given sting only contains letters
     * @param s the given sting
     * @return true if the string only contains letters, else false
     */
    private boolean enteredLetter(String s) {
        for (char c : s.toCharArray()){
            if (!Character.isLetter(c)){
                return false;
            }
        }
        return true;
    }

    /**
     * gives info about current game
     * @return message
     */
    private String getInfo(){
        String response = "Current word is " + currentWord.length() + " characters. You have " + tries + " guesses remaining";
        return response;
    }

    /**
     * Check if the guess has already been done, if not add the guess to the guessed letters.
     * if you guessed correct, add the letter to the hidden word, else decrement tries
     * @param guess the player guess(letter or word)
     * @return appropriate message depending if the guess was correct or not
     */
    private String guess(String guess){
        if (!enteredLetter(guess)){
            return "Incorrect format, you can only enter letters";
        }
        if (guesses.contains(guess)){
            return "You have already made this guess";
        }
        guesses.add(guess);
        String newHidden = contr.processGuess(guess, currentWord, hiddenWord);
        if (newHidden == null){
            return invalidGuess();
        }
        boolean succesful = checkResponse(newHidden);
        if (succesful){
            hiddenWord = newHidden;
            return correctGuess();
        }else {
            tries--;
            if (tries == 0){
                return gameOver();
            }else {
                return inCorrectGuess();
            }
        }
    }

    /**
     * Create a client messenger object to handle IO stream from user.
     * @param client the client socket
     * @param flush if autoFlush is going to be used
     * @return a new Client messenger object
     * @throws IOException
     */
    private ClientMessenger newMessenger(Socket client, boolean flush) throws IOException{
        try {
            BufferedReader clientReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter clientWriter = new PrintWriter(client.getOutputStream(), flush);
            return new ClientMessenger(clientReader, clientWriter, connected);
        }catch (IOException e){
            throw new IOException("Error creating IOstream: " + e);
        }
    }

    /**
     * The method which is ran by the client thread. the method is running until client disconnect.
     */
    @Override
    public void run() {
        try {
            boolean autoFlush = true;
            ClientMessenger client = newMessenger(clientSocket, autoFlush);
            while (connected){
                Message msg = new Message(client.readLine());
                switch (msg.type){
                    case NEWWORD:
                        if (!playing){
                            client.respond("Starting new game");
                            playing = true;
                            newGame();
                            client.respond(getInfo());
                        }else {
                            client.respond("Already playing, are you sure you want a new word? YES/NO");
                            String line = client.readLine().toUpperCase();
                            if (line.contains("YES")){
                                client.respond("Startion a new game");
                                newGame();
                            }else{
                                client.respond("Continuing");
                                client.respond(getInfo());
                            }
                        }
                        break;
                    case DISCONNECT:
                        disconnectClient();
                        break;
                    case GUESS:
                        if (!playing){
                            client.respond("You are not in a game. Type NEWWORD to start");
                            break;
                        }
                        if (msg.body == null){
                            client.respond("error when parsing msg body, try again");
                        }else {
                            client.respond(guess(msg.body));
                        }
                        break;
                    case RESPONSE:
                        client.respond("Illegal type");
                    default:
                        throw new IllegalArgumentException("Error when parsing message: " + msg.fullMsg);
                }
            }
            client.disconnected();
        } catch (IOException ioe){
            disconnectClient();
            System.out.println("Disconnection");
        }
    }

    private static class ClientMessenger {
        private BufferedReader clientReader;
        private PrintWriter clientWriter;
        private volatile boolean connected;

        private ClientMessenger(BufferedReader reader, PrintWriter writer, boolean connected){
            clientReader = reader;
            clientWriter = writer;
            this.connected = connected;
        }

        /**
         * Send a response to the user with correct format
         * @param message the message
         */
        private void respond(String message){
            if (connected){
                clientWriter.println(MsgType.RESPONSE.toString() + Constants.DELIMETER + message);
            }
        }

        /**
         * read a line from the user socket
         * @return the line read from the user
         * @throws IOException
         */
        private String readLine() throws IOException{
            if (!connected)
                return null;
            return clientReader.readLine();
        }

        /**
         * disconnect from the server
         */
        private void disconnected() {
            connected = false;
        }
    }

    /**
     * Class for handling different part of a message
     */
    private static class Message{
        private MsgType type;
        private String body;
        private String fullMsg;

        private Message(String fullMsg){
            parse(fullMsg);
            this.fullMsg = fullMsg;
        }

        private void parse(String fullMsg){
            try {
                String[] message = fullMsg.split(Constants.DELIMETER);
                type = MsgType.valueOf(message[Constants.TYPE_INDEX].toUpperCase());
                if (message.length > Constants.MESSAGE_INDEX){
                    body = message[Constants.MESSAGE_INDEX].toLowerCase();
                }else {
                    body = null;
                }
            } catch (Throwable throwable){
                throw new IllegalArgumentException("Error when parsing message " + throwable);
            }
        }
    }
}

