package services;

import model.Model;
import model.Student;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/students/{index}")
public class StudentService {

    Model model = Model.getInstance();

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Student getStudent(@PathParam("index") int index) {
        Student student = model.getStudent(index);
        if (student != null){
            return student;
        }
        throw new NotFoundException();
    }

    @PUT
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response editStudent(@PathParam("index") int index, Student student) {
        if (model.getStudent(index) == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            int out = model.modifyStudent(index, student);
            if (out == 1) return Response.status(204).build();
            else return Response.status(400).build();
        }
    }

    @DELETE
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteStudent(@PathParam("index") int index) {
        Student student = model.getStudent(index);
        if(student != null) {
            model.deleteStudent(index);
            return Response.status(204).build();
        }
        else {
            return Response.status(404).build();
        }
    }
}