package common.message;

import com.google.gson.Gson;

public class PublicMessage {

    public String from;
    public String message;

    public PublicMessage(String from, String message) {
        this.from = from;
        this.message = message;
    }

}
