import java.io.IOException;
import java.util.*;

public class EventObj extends MakeEventBody {
    public String id;
    public boolean verified;
    public String category;
    public Set<String> members;
    public EventObj(){super();}
    public EventObj(MakeEventBody e) {
        super();
        this.creatorId = e.creatorId;
        this.eventName = e.eventName;
        this.members = new HashSet<>();
        this.members.add(e.creatorId);
        this.id = UUID.randomUUID().toString();
        this.verified = false;
        if(maxMembers < 3) {
            maxMembers = 3;
        }
        if (maxMembers > 10) {
            maxMembers = 10;
        }
    }
    public byte[] toBytes() {
        try {
            return Main.JSON.writeValueAsBytes(this);
        }
        catch(IOException e) {
            e.printStackTrace();
            return "{}".getBytes();
        }
    }
}