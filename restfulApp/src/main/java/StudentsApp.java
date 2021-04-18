import com.mongodb.MongoClient;
import main.CustomHeaders;
import model.*;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import services.*;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.text.SimpleDateFormat;

public class StudentsApp {

    public static final URI BASE_URI = UriBuilder.fromUri("http://localhost/").port(8000).build();

    public static void main(String[] args) throws Exception {

        ResourceConfig config = new ResourceConfig(StudentService.class, StudentsService.class,
                GradeService.class, GradesService.class, CourseService.class, CoursesService.class,
                DeclarativeLinkingFeature.class, CustomHeaders.class);

        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, config);
        server.start();

        MongoClient mongo = new MongoClient( "localhost" , 27017 );
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Model model = Model.getInstance();
        model.initDb();

        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
    }
}
