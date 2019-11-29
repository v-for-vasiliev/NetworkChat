package client.ui;

public interface ChatHandler {

    void onNewMessage(String from, String message);

    void onUserListChanged();
}
