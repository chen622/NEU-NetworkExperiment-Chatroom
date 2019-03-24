import java.util.ArrayList;
import java.util.List;

public class Json {
    private int function;//0:用户上下线  1:所有在线用户 2:消息 3:客服
    private int type = -1;//0:上线|广播  1:下线|私聊 2:申请客服 3:分配
    private String sender = "";
    private String addressee = "";
    private String message = "";
    private List<String> name = new ArrayList<>();

    public Json(int function){
        this.function = function;
    }

    public int getFunction() {
        return function;
    }

    public void setFunction(int function) {
        this.function = function;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getAddressee() {
        return addressee;
    }

    public void setAddressee(String addressee) {
        this.addressee = addressee;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getName() {
        return name;
    }

    public void addName(String name) {
        this.name.add(name);
    }
}
