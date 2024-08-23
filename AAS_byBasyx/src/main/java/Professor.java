import java.util.List;

// 自定义数据类型：教授
public class Professor {
    private String name;
    private String professorId;
    private String[][] coursesTaught;
    private String room;

    public Professor(String name, String professorId, String[][] coursesTaught) {
        this.name = name;
        this.professorId = professorId;
        this.coursesTaught = coursesTaught;
        this.room = "Dorm";
    }
    public Professor() {
    }

    // Getter 方法
    public String getName() {
        return name;
    }

    public String getProfessorId() {
        return professorId;
    }

    public String[][] getCoursesTaught() {
        return coursesTaught;
    }

    public String getRoom() {
        return room;
    }

    // Setter 方法
    public void setName(String name) {
        this.name = name;
    }

    public void setProfessorId(String professorId) {
        this.professorId = professorId;
    }

    public void setCoursesTaught(String[][] coursesTaught) {
        this.coursesTaught = coursesTaught;
    }

    public void setRoom(String room) {
        this.room = room;
    }
    public String toString(){
        return getName();
    }
}

