import com.fasterxml.jackson.annotation.JsonProperty;

import javax.lang.model.element.Name;//姓名 (Name): Property("name", "String")
//学号 (StudentID): Property("studentId", "String"）
//课程列表 (Courses): Property("courses", "List<int:String>")
//所在教室 (Room)：
public class Student {
    @JsonProperty("name")
    String Name;

    @JsonProperty("studentID")
    String StudentID;

    @JsonProperty("courses")
    String[][] Courses;

    @JsonProperty("room")
    String Room;
    public Student(String Name,String StudentID,String[][] Courses){
        this.Name=Name;
        this.StudentID=StudentID;
        this.Courses=Courses;
        this.Room="Dorm";
    }
    public Student() {}

        public String getName(){return Name;}
    public String getStudentID(){return StudentID;}
    public String[][] getCourses(){return Courses;}
    public String getRoom(){return Room;}
    public void setRoom(String Room){this.Room=Room;}
    public String toString(){
        return getName();
    }

}
