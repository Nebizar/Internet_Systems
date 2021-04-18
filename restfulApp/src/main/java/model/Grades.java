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

@XmlRootElement(name = "grades")
@XmlAccessorType(XmlAccessType.FIELD)
public class Grades implements Serializable {

    @JsonUnwrapped
    @XmlElement(name = "grade")
    private List<Grade> grades;

    public Grades() {
        grades = new ArrayList<>();
    }

    @JsonValue
    public List<Grade> getGrades() {
        return grades;
    }

    @JsonValue
    public void setGrades(List<Grade> grades) {
        this.grades = grades;
    }

    public Grade getGrade(ObjectId id) {
        for(Grade c: grades) {
            if(c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    public void addGrade (Grade grade) {
        grades.add(grade);
    }

}
