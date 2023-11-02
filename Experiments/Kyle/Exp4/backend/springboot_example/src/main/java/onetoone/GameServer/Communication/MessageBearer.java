package onetoone.GameServer.Communication;

import onetoone.GameServer.PlayerClasses.User;

import java.util.*;
import com.google.gson.Gson;

public class MessageBearer {

    private static Gson gson = new Gson();

    private List<User> recipients;

    private MessageWrapper message;

    public MessageBearer(List<User> recipients, String type, Object info){
        this.recipients = recipients;
        this.message = new MessageWrapper(type, gson.toJson(info));
    }

    public List<User> getRecipients() {
        return recipients;
    }

    public String getMessageString(){
        return gson.toJson(message);
    }

}
