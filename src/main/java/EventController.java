import jjil.algorithm.Gray8Rgb;
import jjil.algorithm.RgbAvgGray;
import jjil.core.Image;
import jjil.core.Rect;
import jjil.core.RgbImage;
import jjil.j2se.RgbImageJ2se;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.imageio.ImageIO;
import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class EventController {
    public static Route readCategory = (Request req, Response res) -> {
        String cat = req.params(":category");
        File categoryDir = new File(Main.ROOT, cat);
        if(!categoryDir.exists()) {
            categoryDir.mkdir();
        }
        LinkedList<EventObj> events = new LinkedList<>();
        File[] eventFiles = categoryDir.listFiles();
        for(int i = 0; i < eventFiles.length; i++) {
            File eventFile = eventFiles[i];
            events.addLast(Main.JSON.readValue(eventFile, EventObj.class));
        }
        return Main.JSON.writeValueAsString(events);
    };
    public static Route putInCategory = (Request req, Response res) -> {
        String cat = req.params(":category");
        MakeEventBody body = Main.JSON.readValue(req.body(), MakeEventBody.class);
        Path categoryFolder = Paths.get(Main.ROOT, cat);
        EventObj eventObj = new EventObj(body);
        eventObj.category = cat;
        if(!Files.exists(categoryFolder)) {
            Files.createDirectory(categoryFolder);
        }
        Files.write(Paths.get(categoryFolder.toString(), eventObj.id+".txt"), eventObj.toBytes());
        UserController.writeEventToUser(cat, eventObj.id, body.creatorId);
        UserController.adjustPoints(body.creatorId, -150);
        return eventObj.id;
    };
    public static Route eventDetails = (Request req, Response res) -> {
        String cat = req.params(":category");
        String eventId = req.params(":eventId");
        Path eventFile = Paths.get(Main.ROOT, cat, eventId+".txt");
        if(!Files.exists(eventFile)) {
            res.status(404);
            return "Event Does Not Exist";
        }
        return Files.readAllLines(eventFile).get(0);
    };
    public static Route verifyAttend = (Request req, Response res) -> {
        String cat = req.params(":category");
        String eventId = req.params(":eventId");
        Path eventFile = Paths.get(Main.ROOT, cat, eventId+".txt");
        EventObj eventObj = Main.JSON.readValue(eventFile.toFile(), EventObj.class);
        if(eventObj.members.size() < 3) {
            return "Must have at least 3 to verify";
        }
        if(!eventObj.verified) {
            File faceFile = saveImgTmp(req);
            int faces = facesInImg(faceFile);
            if(faces >= eventObj.members.size()) {
                for(String user : eventObj.members) {
                    UserController.adjustPoints(user, 1000);
                }
                eventObj.verified = true;
            }
            else {
                return "INVALID VERIFICATION, INSUFFICIENT FACES";
            }
            Files.write(eventFile, eventObj.toBytes());
            return "VERIFIED";
        }
        else {
            res.status(400);
            return "ALREADY VERIFIED";
        }
    };
    public static File saveImgTmp(Request req) throws Exception {
        req.raw().setAttribute("org.eclipse.jetty.multipartConfig",
                new MultipartConfigElement("/tmp", 100000000, 100000000, 1024));
        Part uploadedFile = req.raw().getPart("image_upload");
        String ext = uploadedFile.getContentType().split("/")[1];
        Path filePath = Paths.get(Main.ROOT, UUID.randomUUID().toString()+"."+ext);
        try (final InputStream in = uploadedFile.getInputStream()) {
            Files.copy(in, filePath);
        }
        return filePath.toFile();
    }
    public static int facesInImg(File f) throws Exception {
        try {
            BufferedImage bi = ImageIO.read(f);
            RgbImage im = RgbImageJ2se.toRgbImage(bi);
            RgbAvgGray toGray = new RgbAvgGray();
            toGray.push(im);
            InputStream is = Main.class.getResourceAsStream("/haar/HCSB.txt");
            jjilexample.Gray8DetectHaarMultiScale detectHaar = new jjilexample.Gray8DetectHaarMultiScale(is, 1, 60);
            List<Rect> rects = detectHaar.pushAndReturn(toGray.getFront());
            Image i = detectHaar.getFront();
            Gray8Rgb g2rgb = new Gray8Rgb();
            g2rgb.push(i);
            RgbImageJ2se conv = new RgbImageJ2se();
            conv.toFile((RgbImage)g2rgb.getFront(), Paths.get(Main.ROOT,"YEET.jpg").toString());
            return rects.size();
        } catch (Throwable e) {
            e.printStackTrace();
            return 0;
        }
    }
}
