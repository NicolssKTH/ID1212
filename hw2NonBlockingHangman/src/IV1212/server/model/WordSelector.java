package IV1212.server.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class WordSelector {
    private final List<String> words = new ArrayList<String>();

    public WordSelector() {
        loadFile();
    }

    private void loadFile() {
        try {
            BufferedReader reader = Files.newBufferedReader(Paths.get("./assets/words.txt"));
            String line = reader.readLine();

            while (line != null) {
                String[] wordsLine = line.split(" ");
                Collections.addAll(words, wordsLine);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String chooseRandomWord() {
        Random rand = new Random(System.currentTimeMillis());
        return words.get(rand.nextInt(words.size()));
    }
}
