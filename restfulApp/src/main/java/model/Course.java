package model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.bson.types.ObjectId;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.mongodb.morphia.annotations.Id;
import services.CourseService;
import services.CoursesService;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;

@XmlRootElement(name = "course")
@XmlAccessorType(XmlAccessType.FIELD)
public class Course {

    //@XmlElement
    //private int id;
    @XmlJavaTypeAdapter(ObjectIdJaxbAdapter.class)
    @JsonSerialize(using = ToStringSerializer.class)
    @XmlElement(name = "id")
    @Id
    ObjectId _id;

    @XmlElement
    private String name;

    @XmlElement
    private String lecturer;

    @InjectLinks({
            @InjectLink(
                    resource = CourseService.class,
                    rel = "self",
                    bindings = {@Binding(name="id", value="${instance.id}")}),
            @InjectLink(
                    resource = CoursesService.class,
                    rel = "parent"),
    })
    @XmlElement(name="link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    List<Link> links;

    public Course() { }

    public Course(String name, String lecturer) {
        this.name = name;
        this.lecturer = lecturer;
    }

    public ObjectId getId() {
        return _id;
    }

    public void setId(ObjectId id) {
        this._id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

}