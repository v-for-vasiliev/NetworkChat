package client.ui;

import java.util.List;

public interface ChatHandler {

    void onNewMessage(String from, String message);

    void onUserListChanged(List<String> usersOnline);
}
