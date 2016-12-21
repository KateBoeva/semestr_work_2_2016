package ru.kpfu.itis.Balda;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.List;

/**
 * Created by katemrrr on 3.12.16.
 */

public class GameFrame extends JFrame {
    public static void main(String[] args) {
        GameFrame gf = new GameFrame(true, null);
        gf.setWord("балда");
    }

    private JPanel textFields;
    private JPanel playerOne;
    private JPanel playerTwo;
    private JLabel status = new JLabel("Ожидание противника!");
    private JButton cancelButton = new JButton("Сбросить");
    private JButton sendButton = new JButton("Отправить");
    private ObjectOutputStream outputStream;

    private Vocabulary vocabulary = new Vocabulary();
    private PlayTurn currentTurn = null;
    private ArrayList<PlayTurn> myTurns = new ArrayList<>();
    private ArrayList<PlayTurn> opponentTurns = new ArrayList<>();
    private ArrayList<String> allPlayedWords = new ArrayList<>();
    private boolean waitingOpponent;

    GameFrame(boolean first, ObjectOutputStream os) {

        waitingOpponent = !first;
        outputStream = os;

        textFields = new JPanel(new GridLayout(5,5));

        for (int i = 0; i < 25; i++) {
            JTextField tf = new JTextField();

            tf.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) { }

                @Override
                public void keyPressed(KeyEvent e) {
                    char ch = e.getKeyChar();
                    JTextField tf = (JTextField) e.getSource();
                    if (!Character.isLetter(ch) || !tf.getText().isEmpty()) {
                        e.consume();
                    } else {
                        currentTurn = new PlayTurn(ch, getTextFieldIndex(tf));
                        status.setText("");
                        tf.setText(String.valueOf(ch));
                        updateTextFieldsEditable();
                        cancelButton.setEnabled(true);
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) { }
            });

            tf.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (currentTurn != null) {
                        JTextField field = (JTextField) e.getSource();
                        String letter = field.getText();
                        int index = getTextFieldIndex(field);
                        ArrayList<Integer> selected = currentTurn.getSequence();
                        if (!letter.isEmpty() && !selected.contains(index)) {
                            if (selected.isEmpty() ||
                                    getNeighbourIndicesForIndex(index).contains(selected.get(selected.size()-1))) {
                                System.out.println("Selected a letter: " + letter);
                                field.setBackground(Color.lightGray);
                                currentTurn.addIndex(index);
                                field.setText(superscript(String.valueOf(selected.size())) + letter);
                                status.setText(status.getText() + letter);
                                sendButton.setEnabled(selected.contains(currentTurn.getIndex()));
                            }
                        }
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) { }

                @Override
                public void mouseReleased(MouseEvent e) { }

                @Override
                public void mouseEntered(MouseEvent e) { }

                @Override
                public void mouseExited(MouseEvent e) { }
            });

            tf.setHorizontalAlignment(JTextField.CENTER);
            tf.setFont(new Font("Courier", Font.BOLD, 36));
            textFields.add(tf);
        }

        updateTextFieldsEditable();

        add(textFields, BorderLayout.CENTER);

        playerOne = createPlayerPane(" Твои слова: ");
        playerTwo = createPlayerPane(" Противник: ");

        add(playerOne, BorderLayout.WEST);
        add(playerTwo, BorderLayout.EAST);

        JPanel footer = new JPanel(new GridLayout(1, 3));

        cancelButton.setEnabled(false);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((JTextField) textFields.getComponent(currentTurn.getIndex())).setText("");
                restore();
            }
        });

        footer.add(cancelButton);

        status.setHorizontalAlignment(JLabel.CENTER);

        if (first) {
            status.setText("Введите букву!");
        }

        footer.add(status);

        sendButton.setEnabled(false);
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!vocabulary.isCorrectNoun(status.getText())) {
                    JOptionPane.showMessageDialog(null, "Слово не найдено в словаре.");
                } else if (allPlayedWords.contains(status.getText())) {
                    JOptionPane.showMessageDialog(null, "Слово уже было использовано.");
                } else {
                    send();
                    restore();
                }
            }
        });

        footer.add(sendButton);

        add(footer, BorderLayout.SOUTH);

        setBounds(100, 100, 600, 400);
        setTitle("Балда");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }

    void setWord(String word) {
        if (word.length() != 5) { return; }
        for (int i = 10; i < 15; i++) {
            JTextField tf = (JTextField) textFields.getComponent(i);
            tf.setText(String.valueOf(word.charAt(i - 10)));
        }
        allPlayedWords.add(word);
        updateTextFieldsEditable();
    }

    private void addWordFromMe(boolean me, String word) {
        JPanel playerPane = me ? playerOne : playerTwo;
        playerPane.add(new JLabel(word + " [" + word.length() + "]"));

        allPlayedWords.add(word);
        waitingOpponent = me;

        // check if game is over
        endTheGameIfNeeded();
    }

    boolean isGameOver() {
        for (Component component : textFields.getComponents()) {
            if (((JTextField) component).getText().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    void opponentPlayTurn(PlayTurn playTurn) {
        JTextField tf = (JTextField) textFields.getComponent(playTurn.getIndex());
        tf.setText(String.valueOf(playTurn.getNewCharacter()));

        String word = "";
        for (int i : playTurn.getSequence()) {
            word += ((JTextField) textFields.getComponent(i)).getText();
        }

        opponentTurns.add(playTurn);
        addWordFromMe(false, word);
        restore();
    }

    private void endTheGameIfNeeded() {

        if (!isGameOver()) { return; }

        String winner = "Draw!";

        int myWordsLength = countTotalLength(myTurns);
        int opponentsWordsLength = countTotalLength(opponentTurns);

        if (myWordsLength > opponentsWordsLength) {
            winner = "You win!";
        } else if (myWordsLength < opponentsWordsLength) {
            winner = "You loose!";
        }

        JOptionPane.showMessageDialog(null, "Game Over! " + winner);
        setVisible(false);
    }

    private int countTotalLength(ArrayList<PlayTurn> playTurns) {
        int total = 0;
        for (PlayTurn playTurn : playTurns) {
            total += playTurn.getSequence().size();
        }
        return total;
    }

    private void send() {

        myTurns.add(currentTurn);

        try {
            outputStream.writeObject(currentTurn);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        addWordFromMe(true, status.getText());
    }

    private static JPanel createPlayerPane(String playerName) {
        JPanel pane = new JPanel(new GridLayout(20, 1));
        JLabel nameLabel = new JLabel(playerName);
        pane.add(nameLabel, BorderLayout.PAGE_START);
        return pane;
    }

    private int getTextFieldIndex(JTextField textField) {
        Component[] components = textFields.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i].equals(textField)) {
                return i;
            }
        }
        return -1;
    }

    private void updateTextFieldsEditable() {
        Component[] components = textFields.getComponents();
        for (int i = 0; i < components.length; i++) {
            JTextField tf = (JTextField) components[i];
            if (currentTurn == null && !waitingOpponent && tf.getText().isEmpty()) {
                tf.setEnabled(false);
                for (int neighbourIndex: getNeighbourIndicesForIndex(i)) {
                    JTextField neighbour = getFieldSafe(neighbourIndex);
                    if (neighbour != null && !neighbour.getText().isEmpty()) {
                        tf.setEnabled(true);
                        break;
                    }
                }
            } else {
                tf.setEnabled(false);
            }
        }
    }

    private List<Integer> getNeighbourIndicesForIndex(int i) {
        List<Integer> neighbours = new ArrayList<>();

        if (i > 4) {
            // not top edge
            neighbours.add(i - 5);
        }

        if (i < 20) {
            // not bottom edge
            neighbours.add(i + 5);
        }

        if (i % 5 != 0) {
            // not left edge
            neighbours.add(i - 1);
        }

        if ((i + 1) % 5 != 0) {
            // not right edge
            neighbours.add(i + 1);
        }

        return neighbours;
    }

    private JTextField getFieldSafe(int i) {
        Component[] components = textFields.getComponents();
        if (i >= 0 && i < components.length) {
            return (JTextField) components[i];
        }
        return null;
    }

    private void restore() {

        currentTurn = null;
        status.setText(waitingOpponent ? "Ожидайте хода противника!" : "Введите букву!");

        updateTextFieldsEditable();

        for (Component component : textFields.getComponents()) {
            JTextField tf = (JTextField) component;
            String text = tf.getText();
            if (!text.isEmpty()) {
                tf.setText(text.substring(text.length() - 1));
            }
            tf.setBackground(Color.white);
        }

        cancelButton.setEnabled(false);
        sendButton.setEnabled(false);
    }

    private static String superscript(String str) {
        str = str.replaceAll("0", "⁰");
        str = str.replaceAll("1", "¹");
        str = str.replaceAll("2", "²");
        str = str.replaceAll("3", "³");
        str = str.replaceAll("4", "⁴");
        str = str.replaceAll("5", "⁵");
        str = str.replaceAll("6", "⁶");
        str = str.replaceAll("7", "⁷");
        str = str.replaceAll("8", "⁸");
        str = str.replaceAll("9", "⁹");
        return str;
    }
}
