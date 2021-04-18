package services;

import model.Grade;
import model.Model;
import org.bson.types.ObjectId;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/students/{index}/grades/{id}")
public class GradeService {

    Model model = Model.getInstance( );

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Grade getGrade(@PathParam("index") int index, @PathParam("id") String gradeId) {
        Grade grade = model.getGrade(index, gradeId);
        if (grade != null) {
            return grade;
        }
        throw new NotFoundException();
    }

    @PUT
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response editGrade(@PathParam("index") int index, @PathParam("id") String gradeId, Grade grade) {
        //System.out.println(gradeId);
        int out = model.modifyGrade(index, gradeId, grade);
        //System.out.println(out);
        if(out == 0){
            return Response.status(204).build();
        } else if (out == 1) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.status(400).build();
        }
    }

    @DELETE
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteGrade(@PathParam("index") int index, @PathParam("id") String gradeId) {
        if (model.getGrade(index, gradeId) == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        else {
            model.deleteGrade(index, new ObjectId(gradeId));
            return Response.status(204).build();
        }
    }
}