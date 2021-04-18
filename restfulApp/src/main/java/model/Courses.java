package model;


import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonValue;
import org.bson.types.ObjectId;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "courses")
@XmlAccessorType(XmlAccessType.FIELD)
public class Courses implements Serializable {

    @JsonUnwrapped
    @XmlElement(name = "course")
    private List<Course> courses;

    public Courses () {
            courses = new ArrayList<>();
    }

    @JsonValue
    public List<Course> getCourses() {
        return courses;
    }

    @JsonValue
    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public Course getCourse(ObjectId id) {
        for(Course c: courses) {
            if(c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    public void addCourse(Course course) {
        courses.add(course);
    }

}
