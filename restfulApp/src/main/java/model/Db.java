package model;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Db {

    private Datastore datastore;

    public Datastore getDatastore() {
        return this.datastore;
    }

    public void setDatastore(Datastore datastore) {
        this.datastore = datastore;
    }
    
    public Db() throws UnknownHostException, MongoException, ParseException {

        String dbName = "datastore";
        MongoClient mongo = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        Morphia morphia = new Morphia();
        Datastore datastore = morphia.createDatastore(mongo, dbName);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        datastore.delete(datastore.createQuery(Course.class));
        datastore.delete(datastore.createQuery(Student.class));
        datastore.delete(datastore.createQuery(Grade.class));
        if (datastore.getCount(Course.class) == 0 && datastore.getCount(Student.class) == 0) {

            // Add data to database for testing
            Students studentsList = new Students();
            Student student = new Student(1, "Krzysztof", "Pasiewicz", format.parse("1997-12-02"));
            datastore.save(student);

            Student student2 = new Student(2, "Paulina", "Kowal", format.parse("1994-12-01"));
            datastore.save(student2);

            Courses courses = new Courses();
            Course course = new Course("Architektura Sieci","Nauczyciel1");
            courses.addCourse(course);

            Course course2 = new Course("Programowanie", "Nauczyciel2");
            courses.addCourse(course2);

            Object courseId = datastore.save(course).getId();
            Object course2Id = datastore.save(course2).getId();

            Grades grades = new Grades();
            Grade grade = new Grade(5.0, format.parse("2020-02-15"), course, (ObjectId) courseId, student.getIndex(), student);
            datastore.save(grade);
            grades.addGrade(grade);

            grade = new Grade(4.0, format.parse("2020-03-15"), course2, (ObjectId) course2Id, student.getIndex(), student);
            datastore.save(grade);
            grades.addGrade(grade);

            grade = new Grade(3.0, format.parse("2012-03-15"), course, (ObjectId) courseId, student2.getIndex(), student2);
            datastore.save(grade);
            grades.addGrade(grade);

            this.datastore = datastore;

            studentsList.addStudent(student);
        }
    }
}
