package client.ui;

import client.connection.IMessageService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

public class AuthGui implements AuthHandler {

    private final JFrame frame;
    final JDialog dialog;
    JLabel dialogLabel;

    private IMessageService iMessageService;

    public AuthGui(IMessageService iMessageService){
        this.iMessageService = iMessageService;
        iMessageService.setAuthHandler(this);
        iMessageService.connectToServer();

        // prepare frame
        frame = new JFrame("Authentication");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(300, 300));
        Container pane = frame.getContentPane();
        pane.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JTextField loginField = new JTextField(20);
        JLabel loginLabel = new JLabel("login");
        JPasswordField passwordField = new JPasswordField(20);
        JLabel passwordLabel = new JLabel("password");
        JButton confirmButton = new JButton("confirm");

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iMessageService.auth(loginField.getText(), passwordField.getText());
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        frame.add(loginLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 5;
        gbc.weightx=1;
        frame.add(loginField, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        frame.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 5;
        gbc.weightx=1;
        frame.add(passwordField, gbc);

        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        frame.add(confirmButton, gbc);

        frame.pack();

        // prepare error window
        dialog = new JDialog(frame, "Error", true);
        dialog.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        JPanel dialogPanel = new JPanel();
        dialogLabel = new JLabel();
        dialogPanel.add(dialogLabel);
        dialog.getContentPane().add(dialogPanel);
        dialog.pack();
    }

    public void showForm(){
        frame.setVisible(true);
    }

    @Override
    public void onAuthOk(String nick) {
        //TODO закрыть окно правильным образом
        frame.setVisible(false);
        ChatGui chatGui = new ChatGui(iMessageService, nick);
        chatGui.showForm();
    }

    @Override
    public void onError(String error) {
        dialogLabel.setText(error);
        dialog.setVisible(true);

    }

    private void closeAuthGui(){
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }
}
