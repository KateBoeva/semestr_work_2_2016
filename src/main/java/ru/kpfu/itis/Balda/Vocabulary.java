package ru.kpfu.itis.Balda;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by katemrrr on 3.12.16.
 */

class Vocabulary {

    private ArrayList<String> words = new ArrayList<>();
    private ArrayList<String> nouns = new ArrayList<>();

    Vocabulary() { }

    String getRandomWord() {
        if (words.isEmpty()) {
            readFromFile("/Users/katemrrr/Desktop/Balda/src/main/resources/init_words.txt", words); // way to init words
        }
        return words.get(new Random().nextInt(words.size()));
    }

    boolean isCorrectNoun(String word) {
        if (nouns.isEmpty()) {
            readFromFile("/Users/katemrrr/Desktop/Balda/src/main/resources/all_nouns.txt", nouns); // way to all nouns
        }
        return nouns.contains(word);
    }

    private void readFromFile(String fileName, ArrayList<String> arr) {
        try {
            Scanner in = new Scanner(new FileReader(fileName));
            while (in.hasNext()) {
                arr.add(in.next());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
