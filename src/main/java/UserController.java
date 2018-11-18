import spark.Request;
import spark.Response;
import spark.Route;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;

public class UserController {
    public static Route joinEvent = (Request req, Response res) -> {
        String eventId = req.params(":eventId");
        String category = req.params(":category");
        JoinBody body = Main.JSON.readValue(req.body(), JoinBody.class);
        writeUserToEvt(category, eventId, body.userId);
        writeEventToUser(category, eventId, body.userId);
        adjustPoints(body.userId, -250);
        return category+"/"+eventId;
    };
    public static void writeUserToEvt(String category, String eventId, String userId) throws Exception {
        File eventFile = Paths.get(Main.ROOT, category, eventId+".txt").toFile();
        System.out.println(eventFile);
        if(!eventFile.exists()) {
            throw new Exception("event DNE");
        }
        EventObj eventObj = Main.JSON.readValue(eventFile, EventObj.class);
        if(eventObj.members == null) {
            eventObj.members = new HashSet<>();
        }
        if(eventObj.members.contains(userId)) {
            throw new Exception("already member");
        }
        if(eventObj.members.size() < eventObj.maxMembers) {
            eventObj.members.add(userId);
        }
        Main.JSON.writeValue(eventFile, eventObj);
    }
    public static void writeEventToUser(String category, String eventId, String userId) throws Exception {
        File userFile = new File(Main.ROOT, userId+".txt");
        UserObj user = new UserObj();
        if(!userFile.exists()) {
            userFile.createNewFile();
        }
        else {
            user = Main.JSON.readValue(userFile, UserObj.class);
        }
        if(user.events == null) {
            user.events = new HashSet<>();
        }
        user.events.add(category+"/"+eventId);
        Main.JSON.writeValue(userFile, user);
    }
    public static void adjustPoints(String userId, int delta) throws Exception {
        File userFile = new File(Main.ROOT, userId+".txt");
        UserObj user = new UserObj();
        if(!userFile.exists()) {
            userFile.createNewFile();
        }
        else {
            user = Main.JSON.readValue(userFile, UserObj.class);
        }
        if(user.events == null) {
            user.events = new HashSet<>();
        }
        user.points += delta;
        Main.JSON.writeValue(userFile, user);
    }
    public static Route userEvents = (Request req, Response res) -> {
        String userId = req.params(":userId");
        Path userFile = Paths.get(Main.ROOT, userId+".txt");
        if(!Files.exists(userFile)) {
            UserObj obj = new UserObj();
            Main.JSON.writeValue(userFile.toFile(), obj);
        }
        return Files.readAllLines(userFile).get(0);
    };
}
