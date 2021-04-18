package model;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Students implements Serializable {

    @JsonUnwrapped
    @XmlElement(name="student")
    private List<Student> students;

    public Students() {
        students = new ArrayList<>();

    }

    @JsonValue
    public List<Student> getStudents() {
        return students;
    }

    @JsonValue
    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public Student getStudent(int index) {
        for( Student s: students) {
            if(s.getIndex() == index) {
                return s;
            }
        }
        return null;
    }

    public void addStudent(Student student) {
        students.add(student);
    }
}
