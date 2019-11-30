package common.message;

public class NickChangeMessage {

    public String login;
    public String oldNick;
    public String newNick;

    public NickChangeMessage(String login, String oldNick, String newNick) {
        this.login = login;
        this.oldNick = oldNick;
        this.newNick = newNick;
    }
}
