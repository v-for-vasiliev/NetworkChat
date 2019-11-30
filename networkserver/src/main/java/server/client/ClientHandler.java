package server.client;


import common.Command;
import common.Message;
import common.message.AuthMessage;
import common.message.PrivateMessage;
import common.message.PublicMessage;
import server.ChatServer;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ClientHandler {

    private ChatServer chatServer;

    private String nick;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public ClientHandler(Socket socket, ChatServer chatServer) {
        try {
            this.socket = socket;
            this.chatServer = chatServer;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

            Thread thread = new Thread(() -> {
                try {
                    authentication();
                    readMessages();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e){
                    e.printStackTrace();
                } catch (SQLException e){
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            });
            thread.setDaemon(true);
            thread.start();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create client handler", e);
        }

    }

    private void readMessages() throws IOException, SQLException, ClassNotFoundException {
        while (true) {
            String clientMessage = in.readUTF();
            System.out.printf("Message '%s' from client %s%n", clientMessage, nick);
            Message m = Message.fromJson(clientMessage);
            switch (m.command) {
                case PUBLIC_MESSAGE:
                    PublicMessage publicMessage = m.publicMessage;
                    chatServer.broadcastMessage(m, this);
                    break;
                case PRIVATE_MESSAGE:
                    PrivateMessage privateMessage = m.privateMessage;
                    chatServer.sendPrivateMessage(privateMessage.to, privateMessage.message);
                    break;
                case CHANGE_NICK:
                    chatServer.changeNick(m.nickChangeMessage.login, m.nickChangeMessage.oldNick, m.nickChangeMessage.newNick);
                    break;
                case END:
                    return;
            }
        }
    }

    private void closeConnection() {
        chatServer.unsubscribe(this);
        chatServer.broadcastMessage(Message.createPublic("Server", nick + " is offline"));
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Failed to close socket!");
            e.printStackTrace();
        }
    }

    // "/auth login password"
    private void authentication() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(120 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (this) {
                    try {
                        if (nick == null) {
                            sendMessage(Message.createAuthError("Connection failed: Authentication time exceeded"));
                            Thread.sleep(100);
                            socket.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        while (true) {
            String clientMessage = in.readUTF();
            Message message = Message.fromJson(clientMessage);
            if (message.command == Command.AUTH_MESSAGE) {
                AuthMessage authMessage = message.authMessage;
                String login = authMessage.login;
                String password = authMessage.password;
                String nick = null;
                try {
                    nick = chatServer.getAuthService().getNickByLoginPass(login, password);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if (nick.equals("Nick not found")) {
                    sendMessage(Message.createAuthError("Wrong login/password"));
                    continue;
                }

                if (chatServer.isNickBusy(nick)) {
                    sendMessage(Message.createAuthError("Login is already in use"));
                    continue;
                }

                sendMessage(Message.createAuthOk(nick));
                this.nick = nick;
                chatServer.broadcastMessage(Message.createPublic("Server", nick + " is online"));
                chatServer.subscribe(this);
                break;
            }
        }
    }

    public void sendMessage(Message message)  {
        try {
            out.writeUTF(message.toJson());
        } catch (IOException e) {
            System.err.println("Failed to send message to user " + nick + " : " + message);
            e.printStackTrace();
        }
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}
