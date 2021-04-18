package services;

import model.Grade;
import model.Grades;
import model.Model;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;

@Path("/students/{index}/grades")
public class GradesService {

    Model model = Model.getInstance( );

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getGrades(@PathParam("index") int index, @QueryParam("course") String courseId, @QueryParam("name") String courseName, @QueryParam("value") double value, @QueryParam("valueCompare") String order,@QueryParam("date") String date, @QueryParam("dateCompare") String dateOrder) {
        Grades grades = new Grades();
        Date formattedDate = null;
        if (date != null && date.length() > 0) {
            formattedDate = new DateParamConverterProvider("yyyy-MM-dd").getConverter(Date.class, Date.class, null).fromString(date);
        }
        grades.setGrades(model.getGrades(index, courseId, value, order, courseName, formattedDate, dateOrder));
        return Response.ok(grades).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response addGrade(@PathParam("index") int index, Grade grade) {
        try {
            Object outObj = model.addGrade(index, grade);
            if (outObj == null) {
                return Response.status(404).build();
            } else {
                String locationValue = "/students/" + index + "/grades/" + outObj;
                return Response.status(201).entity(grade).header("Location", locationValue).build();
            }
        } catch (Exception ex) {
            System.out.println(ex.fillInStackTrace());
            return Response.status(400).build();
        }
    }
}