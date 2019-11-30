package server;


import common.Message;
import server.auth.AuthService;
import server.auth.BaseAuthService;
import server.client.ClientHandler;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatServer {

    private static final int PORT = 8189;

    public final AuthService authService = new BaseAuthService();

    private List<ClientHandler> clientsOnline = new ArrayList<>();

    private ServerSocket serverSocket = null;

    public ChatServer() {
        System.out.println("Server is running");

        try {
            serverSocket = new ServerSocket(PORT);
            while (true) {
                System.out.println("Awaiting client connection...");
                Socket socket = serverSocket.accept();
                System.out.println("Client has connected");
                new ClientHandler(socket, this);
            }

        } catch (IOException e) {
            System.err.println("Ошибка в работе сервера. Причина: " + e.getMessage());
            e.printStackTrace();
        } finally {
            shutdownServer();
        }
    }

    private void shutdownServer() {
        try {
        serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        clientsOnline.add(clientHandler);
        broadcastClientsList();
    }

    private void broadcastClientsList() {
        List<String> nicknames = new ArrayList<>();
        for (ClientHandler client : clientsOnline) {
            nicknames.add(client.getNick());
        }

        Message message = Message.createClientList(nicknames);
        broadcastMessage(message);
    }

    public void changeNick(String login, String oldNick, String newNick) throws SQLException, ClassNotFoundException {
        authService.changeNick(login, newNick);
        broadcastMessage(Message.createPublic("Server", oldNick + " changed nick to " + newNick));
        for (ClientHandler client : clientsOnline) {
            if (client.getNick().equals(oldNick)) {
                client.setNick(newNick);
                break;
            }
        }
        broadcastClientsList();
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clientsOnline.remove(clientHandler);
        broadcastClientsList();
    }

    public AuthService getAuthService() {
        return authService;
    }

    public synchronized boolean isNickBusy(String nick) {
        for (ClientHandler client : clientsOnline) {
            if (client.getNick().equals(nick)) {
                return true;
            }
        }

        return false;
    }

    public synchronized void broadcastMessage(Message message, ClientHandler... exceptionList) {
        List<ClientHandler> exceptions = Arrays.asList(exceptionList);
        for (ClientHandler client : clientsOnline) {
            if (!exceptions.contains(client)) {
                client.sendMessage(message);
            }
        }
    }

    public synchronized void sendPrivateMessage(String receivedLogin, String message) {
        for (ClientHandler client : clientsOnline) {
            if (client.getNick().equals(receivedLogin)) {
                //client.sendMessage(message);
                break;
            }
        }
    }
}
