package common.message;

import com.google.gson.Gson;

public class PrivateMessage {

    public String from;
    public String to;
    public String message;

    public PrivateMessage(String from, String to, String message) {
        this.from = from;
        this.to = to;
        this.message = message;
    }

}
