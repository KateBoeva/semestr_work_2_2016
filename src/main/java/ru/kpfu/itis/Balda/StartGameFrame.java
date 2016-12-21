package ru.kpfu.itis.Balda;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.regex.Pattern;

/**
 * Created by katemrrr on 3.12.16.
 */

public class StartGameFrame extends JFrame {
    public static void main(String[] args) {
        new StartGameFrame();
    }

    private JTextField ipField = new JTextField("127.0.0.1");
    private JTextField portField = new JTextField("3456");
    private JButton actionButton = new JButton("Подключиться");
    private JLabel statusLabel = new JLabel("");

    enum ResponseStatus {
        GAME_START, GAME_OVER, CONNECTION_ERR, WAITING_PARTNER
    }

    private StartGameFrame() {

        setLayout(new GridLayout(6, 1));

        JLabel infoLabel = new JLabel("Введите IP-адрес сервера:");
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel portLabel = new JLabel("Порт сервера:");
        portLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        add(infoLabel);
        add(ipField);
        add(portLabel);
        add(portField);
        add(actionButton);
        add(statusLabel);

        CommonKeyListener keyListener = new CommonKeyListener();
        ipField.addKeyListener(keyListener);
        portField.addKeyListener(keyListener);

        final StartGameFrame gameHandler = this;

        actionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ip = ipField.getText();
                int port = Integer.parseInt(portField.getText());
                if (isValidAddress(ip) && isValidPort(portField.getText())) {
                    new Client(ip, port, gameHandler);
                } else {
                    // wtf?
                    JOptionPane.showMessageDialog(null, "Неверно введенные данные!");
                    updateActionButton();
                }
            }
        });

        setTitle("Балда");
        setBounds(100, 100, 200, 200);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }

    void handleStatus(ResponseStatus status) {
        switch (status) {
            case WAITING_PARTNER:
                actionButton.setEnabled(false);
                statusLabel.setText("Ожидание противника");
                break;
            case CONNECTION_ERR:
                actionButton.setEnabled(true);
                statusLabel.setText("Ошибка подключения");
                break;
            case GAME_START:
                actionButton.setEnabled(false);
                statusLabel.setText("");
                setVisible(false);
                break;
            case GAME_OVER:
                actionButton.setEnabled(true);
                statusLabel.setText("");
                setVisible(true);
                break;
        }
    }

    private void updateActionButton() {
        boolean ipValid = isValidAddress(ipField.getText());
        boolean portValid = isValidPort(portField.getText());
        actionButton.setEnabled(ipValid && portValid);
    }

    private boolean isValidPort(String port) {
        try {
            int p = Integer.parseInt(port);
            return p >= 256 && p <= 65535;
        } catch (Exception e) {
            return false;
        }
    }

    private static final Pattern IP_PATTERN = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    private boolean isValidAddress(String ip) {
        return IP_PATTERN.matcher(ip).matches();
    }

    private class CommonKeyListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
            char c = e.getKeyChar();
            boolean isDot = String.valueOf(c).equals(".");
            if (Character.isDigit(c) || (isDot && e.getSource() == ipField)) {
                return;
            } else {
                e.consume();
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {}

        @Override
        public void keyReleased(KeyEvent e) {
            updateActionButton();
        }
    }
}
