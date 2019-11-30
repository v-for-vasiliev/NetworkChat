package client.connection;

import client.ui.AuthHandler;
import client.ui.ChatHandler;
import common.Message;

import java.io.Closeable;
import java.io.IOException;

public interface MessageService {

    void connectToServer();

    void sendPublicMessage (String from, String publicMessage);

    void sendPrivateMessage (String from, String to, String privateMessage);

    void auth(String login, String pass);

    void changeNick(String login, String oldNick, String newNick);

    void handleMessage(Message message);

    void setAuthHandler(AuthHandler authHandler);

    void setChatHandler(ChatHandler chatHandler);

}
