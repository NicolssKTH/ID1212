package Sockets.Server.Model;

import java.io.*;
import java.util.ArrayList;

/**
 * the wordHandler handle everything to do with generating word,
 * guessing word or letter and read from the text file
 */
public class WordHandler {
    private final String WORDS_FILE = "C:\\Users\\nicla\\IdeaProjects\\Homework1\\src\\Sockets\\Resources\\words.txt";
    private ArrayList<String> words;

    public WordHandler(){
        indexWords(readFile(WORDS_FILE));
    }

    /**
     * attemt to read from a file
     * @param path the file path
     * @return a buffered reader
     */
    private BufferedReader readFile(String path) {
        try {
            return new BufferedReader(new FileReader(new File(path)));
        } catch (FileNotFoundException e) {
            System.out.println("File not fount: " + path);
            System.exit(1);
        }
        return null;
    }

    /**
     * put all the words read from the buffered reader in a array list
     * @param reader the buffered reder to read from
     */
    private void indexWords(BufferedReader reader){
        words = new ArrayList();
        try {
            String word = reader.readLine();
            while (word != null){
                words.add(word.toLowerCase());
                word = reader.readLine();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * select a random word from the word list
     * @return a random word from the list
     */
    public String getWord(){
        int index = (int) (Math.random() * words.size());
        return words.get(index);
    }

    /**
     * Replace the underscore in the hidden word with the guessed letter
     * @param letter the guessed letter
     * @param word the word
     * @param current the current hidden word
     * @return the updated hidden word
     */
    private String replaceAll(char letter, String word, String current){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < word.length(); i++){
            char c = word.charAt(i);
            if (c == letter){
                sb.append(c);
            }else {
                sb.append(current.charAt(i));
            }
        }
        return sb.toString();
    }

    /**
     * checks if the word contains the guessed letter, if so replace the underscores where the guessed letter is found
     * @param word the word
     * @param guess the guess
     * @param hidden the current hidden word
     * @return the uppdated hidden word
     */
    private String guessChar(String word, String guess, String hidden){
        String newHidden = hidden;
        if (word.contains(guess)){
            newHidden = replaceAll(guess.charAt(0),word, hidden);
        }
        return newHidden;
    }

    /**
     * Check if the player guessed the correct word
     * @param word the word to be guessed
     * @param guess the player's guess
     * @param hidden the current hidden word
     * @return updated hidden word
     */
    private String guessWord(String word, String guess, String hidden){
        if (word.equals(guess))
            return word;
        return hidden;
    }

    /**
     * called by the controller to process a guess
     * @param guess the guess
     * @param word the word
     * @param hidden the hidden word
     * @return the updated hidden word
     */
    public String processGuess(String guess, String word, String hidden){
        System.out.println(word);
        String guess1 = guess.toLowerCase();
        if (guess.length() == 1){
            return guessChar(word, guess1, hidden);
        }else if(guess.length() == word.length()) {
            return guessWord(word, guess1, hidden);
        }else {
            return null;
        }
    }
}
