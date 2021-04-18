package services;

import model.*;
import org.bson.types.ObjectId;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/courses/{id}")
public class CourseService {

    Model model = Model.getInstance( );

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Course getCourse(@PathParam("id") String courseId) {
        Course course = model.getCourse(courseId);
        if (course != null) {
            return course;
        }
        throw new NotFoundException();
        //throw new NotFoundException();
    }

    @PUT
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response editCourse(@PathParam("id") String courseId, Course course) {
        if (model.getCourse(courseId) == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            int out = model.modifyCourse(courseId, course);
            if (out == 1) return Response.status(204).build();
            else return Response.status(400).build();
        }
    }

    @DELETE
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteCourse(@PathParam("id") String courseId) {
        if (model.getCourse(courseId) == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        else {
            model.deleteGradesFromCourse(new ObjectId(courseId));
            model.deleteCourse(courseId);
            return Response.status(204).build();
        }
    }
}