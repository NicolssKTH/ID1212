package IV1212.server.model;

public class Game {
    private WordSelector wordSelector;
    private String chosenWord;
    private  String hiddenWord;
    private int score;
    private int remainingAttempts;

    public Game() {
        this.score = 0;
        this.wordSelector = new WordSelector();
    }

    public String startRound() {
        chosenWord = chooseWord();
        System.out.println(chosenWord);
        remainingAttempts = chosenWord.length();
        hiddenWord = chosenWord.replaceAll("[a-zA-Z]", "_");

        return buildMessage();
    }

    private String chooseWord() {
        return wordSelector.chooseRandomWord().toUpperCase();
    }

    public String validateGuess(String guess){
        if (guess.length() == 1) {
            validateLetter(guess);
        } else {
            validateWord(guess);
        }
        return buildMessage();
    }

    private String buildMessage() {
        return hiddenWord + " " + remainingAttempts + " " + score;
    }

    private void validateWord(String word) {
        if (word.toUpperCase().equals(chosenWord)) {
            score++;
            hiddenWord = word.toUpperCase();
            chosenWord = null;
            return;
        }else{
            remainingAttempts--;
            return;
        }
    }

    private void validateLetter(String letter) {
        letter = letter.toUpperCase();

        if (!chosenWord.contains(letter)) {
            if (remainingAttempts <= 1) {
                if (score > 0) score--;
                hiddenWord = chosenWord;
                chosenWord = null;
                return;
            }
            remainingAttempts--;
            return;
        }

        char[] chosenCharArray = chosenWord.toCharArray();
        char[] currentCharArray = hiddenWord.toCharArray();
        char letterChar = letter.charAt(0);

        for (int i = 0; i < chosenCharArray.length; i++) {
            if (chosenCharArray[i] == letterChar) {
                currentCharArray[i] = letterChar;
            }
        }
        hiddenWord = String.valueOf(currentCharArray);

        if (hiddenWord.equals(chosenWord)){
            validateWord(hiddenWord);
        }
    }

    public String getChosenWord() {
        return chosenWord;
    }
}
