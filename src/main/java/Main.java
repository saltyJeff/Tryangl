import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Paths;

import static spark.Spark.*;

public class Main {
    public static final ObjectMapper JSON = new ObjectMapper();
    public static final String ROOT = Paths.get("data").toAbsolutePath().toString();
    public static void main(String[] args) throws Exception {
        System.out.println("Executing Triangl Server");
        Files.createDirectories(Paths.get(ROOT));
        System.out.println("DB root is " + ROOT);

        options("/*",
                (request, response) -> {

                    String accessControlRequestHeaders = request
                            .headers("Access-Control-Request-Headers");
                    if (accessControlRequestHeaders != null) {
                        response.header("Access-Control-Allow-Headers",
                                accessControlRequestHeaders);
                    }

                    String accessControlRequestMethod = request
                            .headers("Access-Control-Request-Method");
                    if (accessControlRequestMethod != null) {
                        response.header("Access-Control-Allow-Methods",
                                accessControlRequestMethod);
                    }

                    return "OK";
                });

        before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
        get("/events/:category", EventController.readCategory);
        post("/events/:category", EventController.putInCategory);
        get("/events/:category/:eventId", EventController.eventDetails);
        post("/join/:category/:eventId", UserController.joinEvent);
        get("/users/:userId", UserController.userEvents);
        post("/verify/:category/:eventId", EventController.verifyAttend);
    }
}
