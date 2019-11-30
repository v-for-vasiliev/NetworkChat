package client.ui;

import client.connection.MessageService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ChatGui implements ChatHandler {

    // GUI elements
    private JFrame frame;
    private JTextField textField;
    private JButton sendButton;
    private JScrollPane scrollPane;
    private JList<String> messageList;
    private DefaultListModel<String> listModel;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private final JDialog dialog;
    private JLabel dialogLabel;
    private JTextField enterNewNick;

    private MessageService messageService;
    private String selfNick;
    private String selfLogin;

    public ChatGui(MessageService messageService, String selfNick, String selfLogin) {


        if (messageService != null) {
            this.messageService = messageService;
            messageService.setChatHandler(this);
            this.selfNick = selfNick;
            this.selfLogin = selfLogin;
        }

        // prepare frame
        frame = new JFrame("Network Chat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(300, 300));
        Container pane = frame.getContentPane();
        pane.setLayout(new BorderLayout());

        // prepare menu panel
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new GridLayout());
        JButton changeNick = new JButton("Change nick");
        changeNick.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDialog();
            }
        });
        northPanel.add(changeNick);
        northPanel.add(new JButton("Private message"));

        // prepare nick change window
        dialog = new JDialog(frame, "Change nick", true);
        Container dialogPane = dialog.getContentPane();
        dialogPane.setLayout(new BoxLayout(dialogPane, BoxLayout.Y_AXIS));
        dialog.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        JPanel dialogPanel = new JPanel();
        dialogLabel = new JLabel("Enter new nick");
        enterNewNick = new JTextField(20);
        JButton confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                messageService.changeNick(selfLogin, getSelfNick(), enterNewNick.getText());
                setSelfNick(enterNewNick.getText());
                enterNewNick.setText("");
                dialog.setVisible(false);

            }
        });

        dialogPanel.add(dialogLabel);
        dialogPanel.add(enterNewNick);
        dialogPanel.add(confirmButton);
        dialogPane.add(dialogPanel);
        dialog.pack();


        //prepare text input elements
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        textField = new JTextField();
        sendButton = new JButton("Send");
        gbc.weightx = 1.0;
        southPanel.add(textField, gbc);
        gbc.weightx = 0.0;
        southPanel.add(sendButton, gbc);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!textField.getText().isEmpty()) {
                    listModel.addElement("You: " + textField.getText());
                    scrollToBottom();
                    messageService.sendPublicMessage(getSelfNick(), textField.getText());
                    textField.setText("");
                }
            }
        });

        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!textField.getText().isEmpty()) {
                    listModel.addElement("You: " + textField.getText());
                    scrollToBottom();
                    if (messageService != null) {
                        messageService.sendPublicMessage(getSelfNick(), textField.getText());
                    }

                    textField.setText("");
                }
            }
        });

        //prepare messages list with scroll bar
        messageList = new JList<>();
        listModel = new DefaultListModel<>();
        messageList.setModel(listModel);
        scrollPane = new JScrollPane(messageList);

        //prepare users list
        JPanel westPanel = new JPanel();
        userList = new JList<>();
        //userList.setBackground(Color.BLUE);
        userListModel = new DefaultListModel<>();
        userListModel.addElement(selfNick);
        userList.setModel(userListModel);
        westPanel.add(userList);

        //add elements to the main panel
        frame.add(northPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(southPanel, BorderLayout.SOUTH);
        frame.add(westPanel, BorderLayout.EAST);
        frame.pack();
    }


    public void showForm() {
        frame.setVisible(true);
    }

    private void scrollToBottom() {
        int lastIndex = messageList.getModel().getSize() - 1;
        if (lastIndex >= 0) {
            messageList.ensureIndexIsVisible(lastIndex);
        }
    }

    public void closeGui() {
        // send exit message to server
        System.exit(2);
    }

    @Override
    public void onNewMessage(String from, String message) {
        listModel.addElement(from + ": " + message);
        scrollToBottom();
    }

    @Override
    public void onUserListChanged(List<String> usersOnline) {
        userListModel.clear();
        userListModel.addElement(selfNick);
        for (String nick : usersOnline) {
            if (!selfNick.equals(nick)) {
                userListModel.addElement(nick);
            }
        }
    }

    public void showDialog(){
        dialog.setVisible(true);
    }

    public String getSelfNick() {
        return selfNick;
    }

    public void setSelfNick(String selfNick) {
        this.selfNick = selfNick;
    }

    public static void main(String[] args) {
        ChatGui chatGui = new ChatGui(null, null, null);
        chatGui.showForm();
    }
}
