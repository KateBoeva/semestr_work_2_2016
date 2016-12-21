package ru.kpfu.itis.Balda;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by katemrrr on 5.12.16.
 */

class PlayTurn implements Serializable {
    private char newCharacter;
    private int index;
    private ArrayList<Integer> sequence;

    PlayTurn(char newCharacter, int index) {
        this.newCharacter = newCharacter;
        this.index = index;
        this.sequence = new ArrayList<>();
    }

    char getNewCharacter() {
        return newCharacter;
    }

    int getIndex() {
        return index;
    }

    ArrayList<Integer> getSequence() {
        return sequence;
    }

    void addIndex(int i) {
        sequence.add(i);
    }
}
