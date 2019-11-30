package client.connection;

import common.Message;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Network implements Closeable {

    private String serverAddr;
    private int serverPort;

    private MessageService messageService;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public Network(String serverAddr, int serverPort) {
        this.serverAddr = serverAddr;
        this.serverPort = serverPort;
    }

    public void openConnection() throws IOException {
        socket = new Socket(serverAddr, serverPort);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        listenToMessages();
    }

    public void listenToMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        String strFromServer = in.readUTF();
                        Message inMsg = Message.fromJson(strFromServer);
                        messageService.handleMessage(inMsg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void sendMessage(Message outMsg) {
        try {
            out.writeUTF(outMsg.toJson());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
