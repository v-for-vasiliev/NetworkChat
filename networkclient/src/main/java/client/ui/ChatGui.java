package client.ui;

import client.connection.MessageService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.input.ReversedLinesFileReader;

public class ChatGui implements ChatHandler {

    // GUI elements
    private JFrame frame;
    private JTextField textField;
    private JButton sendButton;
    private JScrollPane scrollPane;
    private JList<String> messageList;
    private DefaultListModel<String> messageListModel;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private final JDialog dialog;
    private JLabel dialogLabel;
    private JTextField enterNewNick;

    private MessageService messageService;
    private String selfNick;
    private String selfLogin;

    private File historyFile;

    public ChatGui(MessageService messageService, String selfNick, String selfLogin) {


        if (messageService != null) {
            this.messageService = messageService;
            messageService.setChatHandler(this);
            this.selfNick = selfNick;
            this.selfLogin = selfLogin;
        }

        historyFile = new File("history/history_"+ selfLogin + ".txt");

        // prepare frame
        frame = new JFrame("Network Chat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(500, 400));
        Container pane = frame.getContentPane();
        pane.setLayout(new BorderLayout());

        // prepare menu panel
        JPanel northPanel = new JPanel();
        prepareMenuPanel(northPanel);

        // prepare nick change window
        dialog = new JDialog(frame, "Change nick", true);
        prepareNickChangeWindow(dialog);

        //prepare text input elements
        JPanel southPanel = new JPanel();
        prepareTextInputElements(southPanel);

        //prepare message list with scroll pane
        try {
            prepareMessageList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //prepare user list
        JPanel westPanel = new JPanel();
        prepareUserList(westPanel);

        //add elements to the main panel
        frame.add(northPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(southPanel, BorderLayout.SOUTH);
        frame.add(westPanel, BorderLayout.EAST);
        frame.pack();

        //create if not exists history file
        createHistoryFile();
    }

    public void prepareMenuPanel(JPanel northPanel){
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
    }

    public void prepareNickChangeWindow(JDialog dialog){
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
    }

    public void prepareTextInputElements(JPanel southPanel){
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
                    messageListModel.addElement("You: " + textField.getText());
                    try(FileWriter fileWriter = new FileWriter(historyFile, true)){
                        fileWriter.write("\n" + "You: " + textField.getText());
                    } catch (IOException ex){
                        ex.printStackTrace();
                    }
                    scrollToBottom();
                    if (messageService != null) {
                        messageService.sendPublicMessage(getSelfNick(), textField.getText());
                    }
                    textField.setText("");
                }
            }
        });

        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!textField.getText().isEmpty()) {
                    messageListModel.addElement("You: " + textField.getText());
                    try(FileWriter fileWriter = new FileWriter(historyFile, true)){
                        fileWriter.write("\n" + "You: " + textField.getText());
                    } catch (IOException ex){
                        ex.printStackTrace();
                    }
                    scrollToBottom();
                    if (messageService != null) {
                        messageService.sendPublicMessage(getSelfNick(), textField.getText());
                    }
                    textField.setText("");
                }
            }
        });
    }

    public void prepareMessageList() throws IOException {
        messageList = new JList<>();
        messageListModel = new DefaultListModel<>();
        //messageListModel.ensureCapacity(100);
        messageList.setModel(messageListModel);
        List<String> lastMessages = new ArrayList<>();
        String line;
        if(historyFile.exists()){
            ReversedLinesFileReader rlfr = new ReversedLinesFileReader(historyFile);
            for (int i = 100; i > 0; i--){
                line = rlfr.readLine();
                if (line.equals(getSelfLogin())){
                    break;
                }
                lastMessages.add(line);
            }
            for (int i = lastMessages.size()-1; i >= 0; i--){
                messageListModel.addElement(lastMessages.get(i));
            }
        }
        scrollPane = new JScrollPane(messageList);
        scrollToBottom();
    }

    public void prepareUserList(JPanel westPanel){
        userList = new JList<>();
        //userList.setBackground(Color.BLUE);
        userListModel = new DefaultListModel<>();
        userListModel.addElement(selfNick);
        userList.setModel(userListModel);
        westPanel.add(userList);
    }

    public void createHistoryFile(){
        if(!historyFile.exists()){
            try {
                historyFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try(FileWriter fileWriter = new FileWriter(historyFile, true)){
                fileWriter.write(getSelfLogin());
            } catch (IOException ex){
                ex.printStackTrace();
            }
        }
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
        messageListModel.addElement(from + ": " + message);
        try(FileWriter fileWriter = new FileWriter(historyFile, true)){
            fileWriter.write("\n" + from + ": " + message);
        } catch (IOException ex){
            ex.printStackTrace();
        }
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

    public String getSelfLogin() {
        return selfLogin;
    }

    public static void main(String[] args) {
        ChatGui chatGui = new ChatGui(null, null, null);
        chatGui.showForm();
    }
}
