package debtbuddies.GameServer.Communication;

public class MessageWrapper {
    private final String type;
    private final String data;
    public MessageWrapper(String type, String data) {
        this.type = type;
        this.data = data;
    }

    public String getType(){
        return type;
    }

    public String getData(){
        return data;
    }
}
