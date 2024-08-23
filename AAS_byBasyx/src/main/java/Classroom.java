import org.springframework.security.core.parameters.P;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Classroom {
    private String roomNumber;
    private String[][] courses;
    private List<Student> students;
    private List<Professor> professors;

    public Classroom(String roomNumber, String[][] courses) {
        this.roomNumber = roomNumber;
        this.courses = courses;
        this.students = new LinkedList<>();
        this.professors = new LinkedList<>();

    }
    public Classroom(){

    }

    // 添加课程
    public void changeCourses(int day, int time, String course) {
        this.courses[day][time] = course;
    }

    // 添加学生
    public void addStudent(Student student) {
        students.add(student);
    }

    // 添加教授
    public void addProfessor(Professor professor) {
        professors.add(professor);
    }

    //get 方法
    public List<Student> getStudents() {
        return students;
    }

    public List<Professor> getProfessors() {
        return professors;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String[][] getCourses() {

        return courses;
    }

    public String toString(){
        return getRoomNumber();
    }
}





