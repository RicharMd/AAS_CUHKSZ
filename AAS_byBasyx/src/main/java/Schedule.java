import java.util.HashMap;
import java.util.Map;

public class Schedule {
    //    @MYT//与课名映射
    static Map<String, String[]> courseSchedules = new HashMap<>();

//课程对应的教室

    public static String[] course1Schedule = { "Room 101", "Room 101", "Room 101", "Room 101", "Room 101" };  // 语1
    public static String[] course2Schedule = { "Room 101", "Room 101", "Room 101", "Room 101", "Room 101" };  // 语2
    public static String[] course3Schedule = { "Room 101", "Room 101", "Room 101", "Room 101", "Room 101" };  // 数1
    public static String[] course4Schedule = { "Room 102", "Room 102", "Room 102", "Room 102", "Room 102" };  // 数2
    public static String[] course5Schedule = { "Room 102", "Room 102", "Room 102", "Room 102", "Room 102" };  // 英1
    public static String[] course6Schedule = { "Room 102", "Room 102", "Room 102", "Room 103", "Room 102" };  // 英2


    static {
        // 初始化课程日程表
        courseSchedules.put("语1 by Professor A", course1Schedule);
        courseSchedules.put("语2 by Professor A", course2Schedule);
        courseSchedules.put("数1 by Professor B", course3Schedule);
        courseSchedules.put("数2 by Professor B",course4Schedule);
        courseSchedules.put("英1 by Professor C", course5Schedule);
        courseSchedules.put("英2 by Professor C", course6Schedule);
    }
    //数组时间映射

    public static final String[] DAYS_OF_WEEK = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    public static final String[] TIME_SLOTS = {
            "8:00 a.m. - 10:00 a.m.",
            "10:30 a.m. - 12:30 p.m.",
            "1:00 p.m. - 3:00 p.m.",
            "3:30 p.m. - 5:30 p.m."
    };




//学生计划

    public static String[][] StudentSchedule1 = {
            {"英1 by Professor C", "语2 by Professor A", "数1 by Professor B", "数2 by Professor B"},
            {"语1 by Professor A", "语2 by Professor A", "数1 by Professor B", "数2 by Professor B"},
            {"语1 by Professor A", "英2 by Professor C", "英1 by Professor C", "数2 by Professor B"},
            {"英1 by Professor C", "语2 by Professor A", "数1 by Professor B", "数2 by Professor B"},
            {"英1 by Professor C", "语2 by Professor A", "英2 by Professor C", "数2 by Professor B"}
    };

    public static String[][] StudentSchedule2 = {
            {"英1 by Professor C", "语2 by Professor A", "数1 by Professor B", "数2 by Professor B"},
            {"语1 by Professor A", "语2 by Professor A", "英1 by Professor C", "数2 by Professor B"},
            {"语1 by Professor A", "英2 by Professor C", "数1 by Professor B", "数2 by Professor B"},
            {"英1 by Professor C", "语2 by Professor A", "数1 by Professor B", "数2 by Professor B"},
            {"语1 by Professor A", "语2 by Professor A", "数1 by Professor B", "数2 by Professor B"}
    };

    public static String[][] StudentSchedule3 = {
            {"语1 by Professor A", "英2 by Professor C", "数1 by Professor B", "数2 by Professor B"},
            {"英2 by Professor C", "语2 by Professor A", "数1 by Professor B", "数2 by Professor B"},
            {"语1 by Professor A", "英2 by Professor C", "数1 by Professor B", "数2 by Professor B"},
            {"语1 by Professor A", "语2 by Professor A", "数1 by Professor B", "英2 by Professor C"},
            {"英1 by Professor C", "语2 by Professor A", "数1 by Professor B", "数2 by Professor B"}
    };

    public static String[][] StudentSchedule4 = {
            {"语1 by Professor A", "英2 by Professor C", "数1 by Professor B", "数2 by Professor B"},
            {"英2 by Professor C", "语2 by Professor A", "英1 by Professor C", "数2 by Professor B"},
            {"语1 by Professor A", "语2 by Professor A", "英1 by Professor C", "数2 by Professor B"},
            {"语1 by Professor A", "语2 by Professor A", "数1 by Professor B", "数2 by Professor B"},
            {"语1 by Professor A", "语2 by Professor A", "英2 by Professor C", "数2 by Professor B"}
    };

    public static String[][] StudentSchedule5 = {
            {"英1 by Professor C", "英2 by Professor C", "数1 by Professor B", "数2 by Professor B"},
            {"语1 by Professor A", "语2 by Professor A", "英1 by Professor C", "数2 by Professor B"},
            {"语1 by Professor A", "英2 by Professor C", "英1 by Professor C", "数2 by Professor B"},
            {"语1 by Professor A", "语2 by Professor A", "数1 by Professor B", "英2 by Professor C"},
            {"语1 by Professor A", "语2 by Professor A", "英2 by Professor C", "数2 by Professor B"}
    };

    public static String[][] StudentSchedule6 = {
            {"英1 by Professor C", "英2 by Professor C", "数1 by Professor B", "数2 by Professor B"},
            {"英2 by Professor C", "语2 by Professor A", "数1 by Professor B", "数2 by Professor B"},
            {"语1 by Professor A", "英2 by Professor C", "英1 by Professor C", "数2 by Professor B"},
            {"语1 by Professor A", "语2 by Professor A", "数1 by Professor B", "数2 by Professor B"},
            {"语1 by Professor A", "语2 by Professor A", "英2 by Professor C", "数2 by Professor B"}
    };

    public static String[][] StudentSchedule7 = {
            {"英1 by Professor C", "英2 by Professor C", "数1 by Professor B", "数2 by Professor B"},
            {"语1 by Professor A", "语2 by Professor A", "数1 by Professor B", "数2 by Professor B"},
            {"语1 by Professor A", "英2 by Professor C", "英1 by Professor C", "数2 by Professor B"},
            {"语1 by Professor A", "语2 by Professor A", "数1 by Professor B", "英2 by Professor C"},
            {"英1 by Professor C", "语2 by Professor A", "数1 by Professor B", "数2 by Professor B"}
    };

    public static String[][] StudentSchedule8 = {
            {"英1 by Professor C", "语2 by Professor A", "数1 by Professor B", "数2 by Professor B"},
            {"英2 by Professor C", "语2 by Professor A", "数1 by Professor B", "数2 by Professor B"},
            {"语1 by Professor A", "语2 by Professor A", "英1 by Professor C", "数2 by Professor B"},
            {"英1 by Professor C", "语2 by Professor A", "数1 by Professor B", "英2 by Professor C"},
            {"语1 by Professor A", "语2 by Professor A", "英2 by Professor C", "数2 by Professor B"}
    };



    //教授计划
    public static String[][] ProfessorASchedule = {
            {"语1", "语2", null, null},
            {"语1", "语2", null, null},
            {"语1", "语2", null, null},
            {"语1", "语2", null, null},
            {"语1", "语2", null, null}
    };

    public static String[][] ProfessorBSchedule = {
            {null, null, "数1", "数2"},
            {null, null, "数1", "数2"},
            {null, null, "数1", "数2"},
            {null, null, "数1", "数2"},
            {null, null, "数1", "数2"}
    };


    public static String[][] ProfessorCSchedule = {
            {"英1", "英2", null, null},
            {"英2", null, "英1", null},
            {null, null, "英2", "英1"},
            {"英1", null, null, "英2"},
            {"英1", null, "英2", null}
    };



    //教室课表
    public static String[][] classroomSchedule1 = {
            {"语1 by Professor A", "语2 by Professor A", "数1 by Professor B", null},
            {"语1 by Professor A", "语2 by Professor A", "数1 by Professor B", null},
            {"语1 by Professor A", "语2 by Professor A", "数1 by Professor B", null},
            {"语1 by Professor A", "语2 by Professor A", "数1 by Professor B", null},
            {"语1 by Professor A", "语2 by Professor A", "数1 by Professor B", null}
    };



    public static String[][] classroomSchedule2 = {
            {"英1 by Professor C", "英2 by Professor C", null, "数2 by Professor B"},
            {"英2 by Professor C", null, "英1 by Professor C", "数2 by Professor B"},
            {null, "英2 by Professor C", "英1 by Professor C", "数2 by Professor B"},
            {"英1 by Professor C", null, null, "数2 by Professor B"},
            {"英1 by Professor C", null, "英2 by Professor C", "数2 by Professor B"}
    };



    public static String[][] classroomSchedule3 = {
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, "英2 by Professor C"},
            {null, null, null, null}
    };

}


