package client.connection;

import client.ui.AuthHandler;
import client.ui.ChatHandler;
import common.Message;

import java.io.IOException;

public class MessageServiceImpl implements MessageService {

    private Network network;
    private AuthHandler authHandler;
    private ChatHandler chatHandler;

    public MessageServiceImpl(Network network) {
        this.network = network;
        network.setMessageService(this);
    }

    @Override
    public void connectToServer(){
        try {
            network.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendPublicMessage(String from, String publicMessage) {
        Message publicMsg = Message.createPublic(from, publicMessage);
        network.sendMessage(publicMsg);
    }

    @Override
    public void sendPrivateMessage(String from, String to,String privateMessage) {
        Message privateMsg = Message.createPrivate(from, to, privateMessage);
        network.sendMessage(privateMsg);
    }

    @Override
    public void auth(String login, String pass) {
        Message authMsg = Message.createAuth(login, pass);
        network.sendMessage(authMsg);
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.command) {
            case AUTH_OK:
                authHandler.onAuthOk(message.authOkMessage.nickname);
                break;
            case AUTH_ERROR: {
                authHandler.onError(message.authErrorMessage.errorMsg);
                break;
            }
            case PRIVATE_MESSAGE: {
                //processPrivateMessage(message);
                break;
            }
            case PUBLIC_MESSAGE: {
                chatHandler.onNewMessage(message.publicMessage.from, message.publicMessage.message);
                break;
            }
            case CLIENT_LIST:
                chatHandler.onUserListChanged(message.clientListMessage.online);
                break;
            default:
                throw new IllegalArgumentException("Unknown command type: " + message.command);
        }
    }

    @Override
    public void setAuthHandler(AuthHandler authHandler) {
        this.authHandler = authHandler;
    }

    @Override
    public void setChatHandler(ChatHandler chatHandler) {
        this.chatHandler = chatHandler;
    }
}
