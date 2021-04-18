package services;

import model.Course;
import model.Courses;
import model.Model;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/courses/")
public class CoursesService {

    Model model = Model.getInstance( );

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getCourses(@QueryParam("lecturer") String lecturer, @QueryParam("name") String courseName) {
        Courses courses = new Courses();
        courses.setCourses(model.getCourses(lecturer, courseName));
        return Response.ok(courses).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response addCourse(Course course) {
        Object courseId = model.addCourse(course);
        if (courseId != null ) {
            String locationValue = "/courses/" + courseId;
            return Response.status(201).entity(course).header("Location", locationValue).build();
        } else {
            return Response.status(400).build();
        }
    }
}

