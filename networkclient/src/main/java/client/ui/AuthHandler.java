package client.ui;

public interface AuthHandler {

    void onAuthOk(String nick);

    void onError(String error);
}
