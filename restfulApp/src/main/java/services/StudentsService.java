package services;

import model.Model;
import model.Student;
import model.Students;
import org.bson.types.ObjectId;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Date;

@Path("/students/")
public class StudentsService {

    Model model = Model.getInstance( );

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getStudents(@QueryParam("index") int index, @QueryParam("birthday") String date, @QueryParam("birthdayCompare") String order, @QueryParam("firstName") String firstName, @QueryParam("lastName") String lastName) {
        Students students = new Students();
        Date formattedDate = null;
        if (date != null && date.length() > 0) {
            formattedDate = new DateParamConverterProvider("yyyy-MM-dd").getConverter(Date.class, Date.class, null).fromString(date);
        }
        students.setStudents(model.getStudents(index, formattedDate, order, firstName, lastName));
        return Response.ok(students).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response addStudent(Student student) {
        Object studentId = model.addStudent(student);
        if (studentId != null) {
            String locationValue = "/students/" + student.getIndex();
            student.setId((ObjectId) studentId);
            return Response.status(201).entity(student).header("Location", locationValue).build();
        } else {
            return Response.status(400).build();
        }

    }
}

