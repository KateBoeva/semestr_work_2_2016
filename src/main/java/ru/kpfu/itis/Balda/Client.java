package ru.kpfu.itis.Balda;

import javax.swing.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;


/**
 * Created by katemrrr on 3.12.16.
 */

public class Client implements Runnable {

    private String host = "localhost";
    private int port = 3456;
    private GameFrame gf;
    private StartGameFrame gameHandler;

    private Client() { }

    Client(String host, int port, StartGameFrame gameHandler) {
        this.host = host;
        this.port = port;
        this.gameHandler = gameHandler;
        Thread thread = new Thread(this);
        thread.start();
    }

    public static void main(String[] args) {
        new Client();
    }

    @Override
    public void run() {

        try {

            Socket s = new Socket();
            s.connect(new InetSocketAddress(host, port), 3000); // 3000ms - timeout

            gameHandler.handleStatus(StartGameFrame.ResponseStatus.WAITING_PARTNER);

            final ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
            os.flush();

            ObjectInputStream is = new ObjectInputStream(s.getInputStream());

            final boolean isFirst = is.readBoolean();
            final String centralWord = (String) is.readObject();

            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    gf = new GameFrame(isFirst, os);
                    gf.setWord(centralWord);
                }
            });

            gameHandler.handleStatus(StartGameFrame.ResponseStatus.GAME_START);

            do {
                final PlayTurn pt = (PlayTurn) is.readObject();
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        gf.opponentPlayTurn(pt);
                    }
                });
            } while (!gf.isGameOver());

            gameHandler.handleStatus(StartGameFrame.ResponseStatus.GAME_OVER);

        } catch (Exception e) {
            gameHandler.handleStatus(StartGameFrame.ResponseStatus.CONNECTION_ERR);
            e.printStackTrace();
        }
    }
}
