package model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Model {

    private static final Double[] possibleGrades = {2.0,2.5,3.0,3.5,4.0,4.5,5.0};
    public static Model instance = new Model();
    private Datastore datastore;

    public Model() {
    }

    public Datastore getDatastore() {
        return this.datastore;
    }

    public void setDatastore(Datastore datastore) {
        this.datastore = datastore;
    }

    public static void setInstance(Model ourInstance) {
        Model.instance = ourInstance;
    }

    public static Model getInstance() {
        return instance;
    }

    public void initDb() throws ParseException, UnknownHostException {
        Db db = new Db();
        this.datastore = db.getDatastore();
    }

    //VALIDATE METHODS - START
    private static boolean validate(Course course) {
        return !course.getName().equals("") && !course.getLecturer().equals("");
    }

    private static boolean validate(Student student) {
        return !student.getFirstName().equals("") && !student.getLastName().equals("")  && student.getBirthday() != null;
    }

    private static boolean validate(Grade grade) {
        List<Double> list = Arrays.asList(possibleGrades);
        return  grade.getDate() != null && list.contains((Double) grade.getValue());
    }
    //VALIDATE METHODS - STOP
    // GET METHODS - START
    public List<Student> getStudents(int index, Date date, String order, String firstName, String lastName) {
        Query<Student> query = this.datastore.createQuery(Student.class);
        //System.out.println(index);
        if(index != 0)
            query.field("index").equal(index);
        if (firstName != null && !firstName.isEmpty())
            query.field("firstName").containsIgnoreCase(firstName);
        if (lastName != null && !lastName.isEmpty())
            query.field("lastName").containsIgnoreCase(lastName);
        if (date != null) {
            if (order == null ){//&& order.equals("equals")) {
                query.field("birthday").equal(date);
            } else if (order.equals("1")) {
                query.field("birthday").greaterThan(date);
            } else if (order.equals("-1")) {
                query.field("birthday").lessThan(date);
            }
        }
        return query.asList();
    }

    public Student getStudent(int index) {
        return this.datastore.find(Student.class).field("index").equal(index).get();
    }

    public Student getStudent(ObjectId id) {
        return this.datastore.find(Student.class).field("_id").equal(id).get();
    }

    public List<Grade> getGrades(int index, String courseId, double value, String order, String courseName, Date date, String dateOrder) {
        Query<Grade> query = this.datastore.createQuery(Grade.class).field("studentIndex").equal(index);
        if (courseId != null && !ObjectId.isValid(courseId) && !courseId.isEmpty()){
            //System.out.println(courseId + "test");
            return new ArrayList<Grade>();
        }
        Course course = null;
        if (courseName != null && courseName.length() > 0) {
            course = getCourseByName(courseName);
        }
        if (course != null) {
            query.field("courseId").equal(course.getId());
        }
        else if (courseId != null && courseId.length() > 0)
            query.field("courseId").equal(new ObjectId(courseId));
        if (value > 0.0) {
            if (order == null ) {
                query.field("value").equal(value);
            } else if (order.equals("1")) {
                query.field("value").greaterThan(value);
            } else if (order.equals("-1")) {
                query.field("value").lessThan(value);
            }
        }
        if (date != null) {
            if (dateOrder == null ){//&& order.equals("equals")) {
                query.field("date").equal(date);
            } else if (dateOrder.equals("1")) {
                query.field("date").greaterThan(date);
            } else if (dateOrder.equals("-1")) {
                query.field("date").lessThan(date);
            }
        }
        return query.asList();
    }

    public Grade getGrade(int index, String gradeId) {
        ObjectId gradeObjectId = new ObjectId(gradeId);
        //System.out.println(gradeObjectId);
        return this.datastore.find(Grade.class).field("studentIndex").equal(index).field("_id").equal(gradeObjectId).get();
    }

    public List<Course> getCourses(String lecturer, String courseName) {
        Query<Course> query = this.datastore.createQuery(Course.class);
        if (courseName != null && !courseName.isEmpty()) {
            query.field("name").containsIgnoreCase(courseName);
        }
        if (lecturer != null && !lecturer.isEmpty()) {
            query.field("lecturer").containsIgnoreCase(lecturer);
        }
        return query.asList();
    }

    public Course getCourse(String id) {
        ObjectId courseIdObjectId = new ObjectId(id);
        return this.datastore.find(Course.class).field("_id").equal(courseIdObjectId).get();
    }
    // GET METHODS - END
    // POST METHODS - START
    public Object addCourse(Course course) {
        if(validate(course)){
            course.setLinks(null);
            return this.datastore.save(course).getId();
        } else {
            return null;
        }
    }

    public Object addStudent(Student student) {
        int index = incIndex();
        student.setIndex(index);
        if (validate(student)){
            student.setLinks(null);
            return this.datastore.save(student).getId();
        } else {
            return null;
        }
    }

    public Object addGrade(int index, Grade grade) throws Exception{
        //System.out.println(grade);
        Student student = getStudent(index);
        grade.setStudent(student);
        grade.setStudentIndex(index);
        if (validate(grade)) {
            if (getCourse(grade.getCourse().getId().toString()) != null) {
                //System.out.println("INSIE");
                grade.setCourseId(grade.getCourse().getId());
                grade.setCourse(getCourse(grade.getCourseId().toString()));
                grade.setLinks(null);
                return this.datastore.save(grade).getId();
            }
        } else {
            throw new Exception();
        }
        return null;
    }
    // POST METHODS - STOP
    // PUT METHODS - START
    public int modifyStudent(int studentIndex, Student student) {
        Student studentFromDb = getStudent(studentIndex);
        if(validate(student)) {
            studentFromDb.setFirstName(student.getFirstName());
            studentFromDb.setBirthday(student.getBirthday());
            studentFromDb.setLastName(student.getLastName());

            this.datastore.save(studentFromDb);
            return 1;
        } else {
            return -1;
        }
    }

    public int modifyCourse(String courseId, Course course) {
        Course courseFromDb = getCourse(courseId);
        if (validate(course)) {
            courseFromDb.setName(course.getName());
            courseFromDb.setLecturer(course.getLecturer());
            this.datastore.save(courseFromDb);
            return 1;
        } else {
            return -1;
        }
    }

    public int modifyGrade(int index, String gradeId, Grade grade) {
        Grade gradeFromDb = getGrade(index, gradeId);
        Course newCourse = grade.getCourse();
        Course course;
        if(newCourse.getId() == null) course = getCourseByName(newCourse.getName());
        else course = getCourse(newCourse.getId().toString());

        if(course != null) {
            gradeFromDb.setCourse(course);
            gradeFromDb.setCourseId(course.getId());
        } else {
            return 1;
        }
        if(validate(grade)) {
            gradeFromDb.setDate(grade.getDate());
            gradeFromDb.setValue(grade.getValue());
        } else {
            return 2;
        }
        this.datastore.save(gradeFromDb);
        return 0;
    }
    // PUT METHODS - STOP
    // DELETE METHODS - START
    public void deleteCourse(String courseId) {
        this.datastore.delete(this.datastore.find(Course.class).field("_id").equal(new ObjectId(courseId)).get());
    }

    public void deleteStudent(int index) {
        this.datastore.delete(this.datastore.find(Student.class).field("index").equal(index).get());
    }

    public void deleteGrade(int index, ObjectId gradeId) {
        this.datastore.delete(this.datastore.find(Grade.class).field("_id").equal(gradeId).get());
    }
    // DELETE METHODS - STOP
    // MISC
    public Course getCourseByName(String name) {
        Query<Course> query = this.datastore.createQuery(Course.class);
        query.field("name").containsIgnoreCase(name);
        if (query.asList().size() > 0)
            return query.asList().get(0);
        else
            return null;
    }

    public void deleteGradesFromCourse(ObjectId courseId) {
        this.datastore.delete(this.datastore.find(Grade.class).field("courseId").equal(courseId));
    }

    public int incIndex() {
        Query<Student> query = this.datastore.createQuery(Student.class);
        int index = query.asList().get(query.asList().size() - 1).getIndex();
        return index + 1;
    }
}

