package model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.mongodb.morphia.annotations.*;
import services.StudentService;
import services.StudentsService;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.List;

@XmlRootElement(name="student")
@XmlAccessorType(XmlAccessType.FIELD)
public class Student {

    @XmlTransient
    @Id
    ObjectId _id;

    @XmlElement
    private int index;

    @XmlElement
    private String firstName;

    @XmlElement
    private String lastName;

    @XmlElement
    @JsonFormat(shape=JsonFormat.Shape.STRING,
            pattern="yyyy-MM-dd", timezone="CET")
    private Date birthday;


    @InjectLinks({
            @InjectLink(
                    resource = StudentService.class,
                    rel = "self",
                    bindings = {@Binding(name="index", value="${instance.index}")}),
            @InjectLink(
                    resource = StudentsService.class,
                    rel = "parent"),
    })
    @XmlElement(name="link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    List<Link> links;


    public Student(){ }

    public Student(Integer index, String firstName, String lastName, Date birthday) {
        this.index = index;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
    }

    @XmlJavaTypeAdapter(ObjectIdJaxbAdapter.class)
    public ObjectId getId() {
        return _id;
    }

    public void setId(ObjectId id) {
        this._id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}
