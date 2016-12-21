package ru.kpfu.itis.Balda;

import java.io.*;
import java.net.Socket;

/**
 * Created by katemrrr on 2.12.16.
 */

class Connection implements Runnable {
    private Socket first;
    private Socket second;
    private Vocabulary vocabulary = new Vocabulary();

    Connection(Socket first, Socket second) {
        this.first = first;
        this.second = second;
        Thread thread = new Thread(this);
        thread.start();
    }
    
    public void run() {
        try {

            ObjectOutputStream os1 = new ObjectOutputStream(first.getOutputStream());
            os1.flush();

            ObjectOutputStream os2 = new ObjectOutputStream(second.getOutputStream());
            os2.flush();

            ObjectInputStream is1 = new ObjectInputStream(first.getInputStream());
            ObjectInputStream is2 = new ObjectInputStream(second.getInputStream());

            String gameWord = vocabulary.getRandomWord();

            os1.writeBoolean(true);
            os1.writeObject(gameWord);
            os1.flush();

            os2.writeBoolean(false);
            os2.writeObject(gameWord);
            os2.flush();

            while (true) {
                os2.writeObject(is1.readObject());
                os2.flush();
                os1.writeObject(is2.readObject());
                os1.flush();
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
