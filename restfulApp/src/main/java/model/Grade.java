package model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;
import services.GradeService;
import services.GradesService;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.List;

@XmlRootElement(name = "grade")
@XmlAccessorType(XmlAccessType.FIELD)
public class Grade {

    //@XmlTransient
    //private int id;
    @XmlJavaTypeAdapter(ObjectIdJaxbAdapter.class)
    @JsonSerialize(using = ToStringSerializer.class)
    @XmlElement(name="id")
    @Id
    ObjectId _id;

    @XmlElement
    private double value;

    @XmlElement
    @JsonFormat(shape=JsonFormat.Shape.STRING,
            pattern="yyyy-MM-dd", timezone="CET")
    private Date date;

    @XmlElement
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId courseId;

    @XmlElement
    private int studentIndex;

    @XmlElement
    @Reference
    private Course course;

    @XmlTransient
    @Reference
    private Student student;

    @InjectLinks({
            @InjectLink(resource = GradeService.class, rel = "self",
                    bindings = {@Binding(name="id", value="${instance.id}")}),
            @InjectLink(resource = GradesService.class, rel = "parent")
    })
    @XmlElement(name="link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    List<Link> links;

    public Grade() {
    }

    public Grade(double value, Date date, Course course, ObjectId courseId, int studentIndex, Student student) {
        this.value = value;
        this.date = date;
        this.course = course;
        this.courseId = courseId;
        this.studentIndex = studentIndex;
        this.student = student;
    }

    public ObjectId getId() {
        return _id;
    }

    public void setId(ObjectId _id) {
        this._id = _id;
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ObjectId getCourseId() {
        return courseId;
    }

    public void setCourseId(ObjectId courseId) {
        this.courseId = courseId;
    }

    public int getStudentIndex() {
        return studentIndex;
    }

    public void setStudentIndex(int studentIndex) {
        this.studentIndex = studentIndex;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course idCourse) {
        this.course = idCourse;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}
