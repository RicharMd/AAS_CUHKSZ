import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.formula.functions.T;
import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.springframework.security.core.parameters.P;

import java.io.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class Client {
    public static ConnectedAssetAdministrationShellManager manager=new ConnectedAssetAdministrationShellManager(new AASRegistryProxy(SchoolAAS.REGISTRYPATH));
    public static volatile boolean running= true;
    public static volatile int Day= 0;
    public static volatile int Time= 0;
    public static Map<String, IHandler> iHandlerMap=new HashMap<>();
    public static volatile Queue<String> taskQueue=new LinkedList<>();

    public static volatile CountDownLatch currentTaskNum ;
    public static Scanner scanner=new Scanner(System.in);
    public static void main(String[] args) {
        System.out.println("This is CUHKSZ AAS Simulation Client System......");
        // 初始化连接到 SchoolAAS 的管理器
        try {
            AASRegistryProxy proxy = new AASRegistryProxy(SchoolAAS.REGISTRYPATH);
            manager = new ConnectedAssetAdministrationShellManager(proxy);

            // 尝试从注册表中获取所有注册的AAS以验证连接
            List<AASDescriptor> shells = proxy.lookupAll();
            if (shells == null || shells.isEmpty()) {
                System.out.println("No AAS found in the registry. Connection might not be properly established.");
            } else {
                System.out.println("Successfully connected to SchoolAAS Registry at " + SchoolAAS.REGISTRYPATH);
                System.out.println("Found the following AAS models in the registry:");

                // 遍历每个 AAS 描述符并输出相关信息
                for (AASDescriptor shell : shells) {
                    System.out.println("AAS ID: " + shell.getIdentifier().getId());
                    System.out.println("Submodels:");

                    // 遍历并输出每个子模型的 ID
                    for (SubmodelDescriptor submodel : shell.getSubmodelDescriptors()) {
                        System.out.println("    Submodel ID: " + submodel.getIdentifier().getId());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to connect to SchoolAAS Registry at " + SchoolAAS.REGISTRYPATH);
            e.printStackTrace();
            return; // 连接失败，退出程序
        }

        //模拟一周
        IHandler weekSimulation=manager ->{
          new Thread(()->{

              //学生集取出
              ISubmodel submodel=manager.retrieveSubmodel(SchoolAAS.schoolAASID,SchoolAAS.studentSubmodelID);

              IProperty students=(IProperty) submodel.getSubmodelElement("students");
//              System.out.println("已经取出");

              Map<String,Student> studentMap;
              try {
                  ObjectMapper objectMapper=new ObjectMapper();

                  studentMap=objectMapper.readValue((String) students.getValue(), new TypeReference<Map<String, Student>>() {});
//                          System.out.println("已经取出学生映射");

              } catch (IOException e) {
                  throw new RuntimeException(e);
              }

              //教授集取出
              ISubmodel submodel2=manager.retrieveSubmodel(SchoolAAS.schoolAASID,SchoolAAS.professorSubmodelID);

              IProperty professors=(IProperty) submodel2.getSubmodelElement("professors");
//              System.out.println("已经取出");

              Map<String,Professor> professorMap;
              try {
                  ObjectMapper objectMapper=new ObjectMapper();

                  professorMap=objectMapper.readValue((String) professors.getValue(), new TypeReference<Map<String, Professor>>() {});
//                          System.out.println("已经取出学生映射");

              } catch (IOException e) {
                  throw new RuntimeException(e);
              }

              //课程集取出
              ISubmodel submodel3=manager.retrieveSubmodel(SchoolAAS.schoolAASID,SchoolAAS.classroomSubmodelID);

              IProperty classroom=(IProperty) submodel3.getSubmodelElement("classrooms");
//              System.out.println("已经取出");

              Map<String,Classroom> classroomMap;
              try {
                  ObjectMapper objectMapper=new ObjectMapper();

                  classroomMap=objectMapper.readValue((String) classroom.getValue(), new TypeReference<Map<String, Classroom>>() {});
//                          System.out.println("已经取出学生映射");

              } catch (IOException e) {
                  throw new RuntimeException(e);
              }
              int i=0;


              while(i<5&&running){

//                  System.out.println(running);
//                  System.out.println("大循环没挺");
                  System.out.println(Schedule.DAYS_OF_WEEK[Day]);
                  System.out.println("################################################################");

                  while (Time<4&&running){
//                      System.out.println(running);
//                      System.out.println("小循环没挺");
                      System.out.println(Schedule.TIME_SLOTS[Time]);
                      System.out.println("---------------------------------------------------");
                      //正常是2条线程，因为还没加入教授所以用1条来测试
                      CountDownLatch countDownLatch=new CountDownLatch(2);



                      //遍历
                      int finalTime = Time;
                      int finalDay = Day;
                      new Thread(()->{
//                          System.out.println("进入遍历线程");
                          if(running){
//                              System.out.println("进入遍历");
//                              System.out.println("学生映射的大小：" + studentMap.size());
                           for(Student student:studentMap.values()){
                               //当教室类出来后会有具体的教室
//                               System.out.println("!!");
                               System.out.println(Schedule.TIME_SLOTS[finalTime]+" || "+student.Name+" is going to take "+ student.Courses[finalDay][finalTime]+" from "+student.Room+" to "+Schedule.courseSchedules.get(student.Courses[finalDay][finalTime])[finalDay]);
                               //动态改变教室类
                                Classroom preClassroom=classroomMap.get(student.Room);
                                Classroom nextClassroom=classroomMap.get(Schedule.courseSchedules.get(student.Courses[finalDay][finalTime])[finalDay]);
                                if(preClassroom!=null){
                                    for(Student student1:preClassroom.getStudents()){
                                        if(student1.Room.equals(student.Room)){
                                            preClassroom.getStudents().remove(student1);
                                            break;
                                        }
                                    }
                                }

                               //改变学生当前位置

                                   student.setRoom(Schedule.courseSchedules.get(student.Courses[finalDay][finalTime])[finalDay]);
                                   nextClassroom.addStudent(student);


                               try {
                                   Thread.sleep(500);
                               } catch (InterruptedException e) {
                                   throw new RuntimeException(e);
                               }
                           }


                          }
                          countDownLatch.countDown();

                      }).start();
                      //遍历
                      new Thread(()->{
//                          System.out.println("进入遍历线程");
                          if(running){
//                              System.out.println("进入遍历");
//                              System.out.println("学生映射的大小：" + studentMap.size());
                              for(Professor professor:professorMap.values()){
                                  if(professor.getCoursesTaught()[finalDay][finalTime]!=null){
                                      //当教室类出来后会有具体的教室
//                               System.out.println("!!");
                                      System.out.println(Schedule.TIME_SLOTS[finalTime]+" || "+professor.getName()+" is going to teach "+ professor.getCoursesTaught()[finalDay][finalTime]+" from "+professor.getRoom()+" to "+Schedule.courseSchedules.get(professor.getCoursesTaught()[finalDay][finalTime]+" by "+professor.getName())[finalDay]);
                                      //动态改变教室类
                                      Classroom preClassroom=classroomMap.get(professor.getRoom());
                                      Classroom nextClassroom=classroomMap.get(Schedule.courseSchedules.get(professor.getCoursesTaught()[finalDay][finalTime]+" by "+professor.getName())[finalDay]);
                                      if(preClassroom!=null){
                                          preClassroom.getProfessors().remove(0);
                                      }
                                      //改变学生当前位置

                                          professor.setRoom(Schedule.courseSchedules.get(professor.getCoursesTaught()[finalDay][finalTime]+" by "+professor.getName())[finalDay]);
                                      if(!nextClassroom.getProfessors().isEmpty()){
                                          nextClassroom.getProfessors().remove(0);
                                      }
                                          nextClassroom.getProfessors().add(professor);


                                      try {
                                          Thread.sleep(500);
                                      } catch (InterruptedException e) {
                                          throw new RuntimeException(e);
                                      }
                                  }

                              }


                          }
                          countDownLatch.countDown();
                      }).start();

                      try {
                          countDownLatch.await();
                          System.out.println();
                          for(Classroom classroom1:classroomMap.values()){

                              System.out.println(classroom1.getRoomNumber()+" now has those Students: " +Arrays.toString(classroom1.getStudents().toArray() ));
                              System.out.println(classroom1.getRoomNumber()+" now has those Professors: " +Arrays.toString(classroom1.getProfessors().toArray()) );
                              System.out.println(classroom1.getRoomNumber()+" now is being used to taught  " +classroom1.getCourses()[finalDay][finalTime]);
                              System.out.println();




                              try {
                                  Thread.sleep(500);
                              } catch (InterruptedException e) {
                                  throw new RuntimeException(e);
                              }
                          }
                      } catch (InterruptedException e) {
                          throw new RuntimeException(e);
                      }

                      try {
                          System.out.println("---------------------------------------------------");
                          Thread.sleep(1000);
                      } catch (InterruptedException e) {
                          throw new RuntimeException(e);
                      }

                      if(Time==3){
                          for(Student student:studentMap.values()){
                              student.setRoom("Dorm");
                          }
                          for(Professor professor:professorMap.values()){
                              professor.setRoom("Dorm");
                          }
                          for(Classroom classroom1:classroomMap.values()){
                              classroom1.getProfessors().clear();
                              classroom1.getStudents().clear();
                          }
                          i++;
                          Day++;
                          Day=Day%5;
                      }
                      Time++;
                  }

                  Time=Time%4;

                  try {
                      System.out.println("################################################################");
                      Thread.sleep(2000);
                  } catch (InterruptedException e) {
                      throw new RuntimeException(e);
                  }
;
              }
          }).start();
          currentTaskNum.countDown();
        };
        iHandlerMap.put("weekSimulation", weekSimulation);
//        weekSimulation.handle(manager);
        //模拟一天
        IHandler daySimulation= manager ->{

                new Thread(()->{

                    //学生集取出
                    ISubmodel submodel=manager.retrieveSubmodel(SchoolAAS.schoolAASID,SchoolAAS.studentSubmodelID);

                    IProperty students=(IProperty) submodel.getSubmodelElement("students");
//              System.out.println("已经取出");

                    Map<String,Student> studentMap;
                    try {
                        ObjectMapper objectMapper=new ObjectMapper();

                        studentMap=objectMapper.readValue((String) students.getValue(), new TypeReference<Map<String, Student>>() {});
//                          System.out.println("已经取出学生映射");

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    //教授集取出
                    ISubmodel submodel2=manager.retrieveSubmodel(SchoolAAS.schoolAASID,SchoolAAS.professorSubmodelID);

                    IProperty professors=(IProperty) submodel2.getSubmodelElement("professors");
//              System.out.println("已经取出");

                    Map<String,Professor> professorMap;
                    try {
                        ObjectMapper objectMapper=new ObjectMapper();

                        professorMap=objectMapper.readValue((String) professors.getValue(), new TypeReference<Map<String, Professor>>() {});
//                          System.out.println("已经取出学生映射");

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    //课程集取出
                    ISubmodel submodel3=manager.retrieveSubmodel(SchoolAAS.schoolAASID,SchoolAAS.classroomSubmodelID);

                    IProperty classroom=(IProperty) submodel3.getSubmodelElement("classrooms");
//              System.out.println("已经取出");

                    Map<String,Classroom> classroomMap;
                    try {
                        ObjectMapper objectMapper=new ObjectMapper();

                        classroomMap=objectMapper.readValue((String) classroom.getValue(), new TypeReference<Map<String, Classroom>>() {});
//                          System.out.println("已经取出学生映射");

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    System.out.println(Schedule.DAYS_OF_WEEK[Day]);
                    System.out.println("################################################################");

                    while (Time <4&&running){

//                      System.out.println(running);
//                      System.out.println("小循环没挺");
                        System.out.println(Schedule.TIME_SLOTS[Time]);
                        System.out.println("---------------------------------------------------");
                        //正常是2条线程，因为还没加入教授所以用1条来测试
                        CountDownLatch countDownLatch=new CountDownLatch(2);



                        //遍历
                        int finalTime = Time;
                        int finalDay = Day;
                        new Thread(()->{
//                          System.out.println("进入遍历线程");
                            if(running){
//                              System.out.println("进入遍历");
//                              System.out.println("学生映射的大小：" + studentMap.size());
                                for(Student student:studentMap.values()){
                                    //当教室类出来后会有具体的教室
//                               System.out.println("!!");
                                    System.out.println(Schedule.TIME_SLOTS[finalTime]+" || "+student.Name+" is going to take "+ student.Courses[finalDay][finalTime]+" from "+student.Room+" to "+Schedule.courseSchedules.get(student.Courses[finalDay][finalTime])[finalDay]);
                                    //动态改变教室类
                                    Classroom preClassroom=classroomMap.get(student.Room);
                                    Classroom nextClassroom=classroomMap.get(Schedule.courseSchedules.get(student.Courses[finalDay][finalTime])[finalDay]);
                                    if(preClassroom!=null){
                                        for(Student student1:preClassroom.getStudents()){
                                            if(student1.Room.equals(student.Room)){
                                                preClassroom.getStudents().remove(student1);
                                                break;
                                            }
                                        }
                                    }

                                    //改变学生当前位置

                                    student.setRoom(Schedule.courseSchedules.get(student.Courses[finalDay][finalTime])[finalDay]);
                                    nextClassroom.addStudent(student);


                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                }


                            }
                            countDownLatch.countDown();

                        }).start();
                        //遍历
                        new Thread(()->{
//                          System.out.println("进入遍历线程");
                            if(running){
//                              System.out.println("进入遍历");
//                              System.out.println("学生映射的大小：" + studentMap.size());
                                for(Professor professor:professorMap.values()){
                                    if(professor.getCoursesTaught()[finalDay][finalTime]!=null){
                                        //当教室类出来后会有具体的教室
//                               System.out.println("!!");
                                        System.out.println(Schedule.TIME_SLOTS[finalTime]+" || "+professor.getName()+" is going to teach "+ professor.getCoursesTaught()[finalDay][finalTime]+" from "+professor.getRoom()+" to "+Schedule.courseSchedules.get(professor.getCoursesTaught()[finalDay][finalTime]+" by "+professor.getName())[finalDay]);
                                        //动态改变教室类
                                        Classroom preClassroom=classroomMap.get(professor.getRoom());
                                        Classroom nextClassroom=classroomMap.get(Schedule.courseSchedules.get(professor.getCoursesTaught()[finalDay][finalTime]+" by "+professor.getName())[finalDay]);
                                        if(preClassroom!=null){
                                            preClassroom.getProfessors().remove(0);
                                        }
                                        //改变学生当前位置

                                        professor.setRoom(Schedule.courseSchedules.get(professor.getCoursesTaught()[finalDay][finalTime]+" by "+professor.getName())[finalDay]);
                                        if(!nextClassroom.getProfessors().isEmpty()){
                                            nextClassroom.getProfessors().remove(0);
                                        }
                                        nextClassroom.getProfessors().add(professor);


                                        try {
                                            Thread.sleep(500);
                                        } catch (InterruptedException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }

                                }


                            }
                            countDownLatch.countDown();
                        }).start();

                        try {
                            countDownLatch.await();
                            System.out.println();
                            for(Classroom classroom1:classroomMap.values()){

                                System.out.println(classroom1.getRoomNumber()+" now has those Students: " +Arrays.toString(classroom1.getStudents().toArray() ));
                                System.out.println(classroom1.getRoomNumber()+" now has those Professors: " +Arrays.toString(classroom1.getProfessors().toArray()) );
                                System.out.println(classroom1.getRoomNumber()+" now is being used to taught  " +classroom1.getCourses()[finalDay][finalTime]);
                                System.out.println();




                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        try {
                            System.out.println("---------------------------------------------------");
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        if(Time==3){
                            for(Professor professor:professorMap.values()){
                                professor.setRoom("Dorm");
                            }
                            for(Classroom classroom1:classroomMap.values()){
                                classroom1.getProfessors().clear();
                                classroom1.getStudents().clear();
                            }

                            Day++;
                            Day=Day%5;
                        }
                        Time++;


                    }
                    Time=Time%4;


                            System.out.println("################################################################");




                    currentTaskNum.countDown();
                }).start();
        };
        iHandlerMap.put("daySimulation",daySimulation);
//        daySimulation.handle(manager);

        //模拟1h
        IHandler timeSimulation= manager ->{

            new Thread(()->{

                //学生集取出
                ISubmodel submodel=manager.retrieveSubmodel(SchoolAAS.schoolAASID,SchoolAAS.studentSubmodelID);

                IProperty students=(IProperty) submodel.getSubmodelElement("students");
//              System.out.println("已经取出");

                Map<String,Student> studentMap;
                try {
                    ObjectMapper objectMapper=new ObjectMapper();

                    studentMap=objectMapper.readValue((String) students.getValue(), new TypeReference<Map<String, Student>>() {});
//                          System.out.println("已经取出学生映射");

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                //教授集取出
                ISubmodel submodel2=manager.retrieveSubmodel(SchoolAAS.schoolAASID,SchoolAAS.professorSubmodelID);

                IProperty professors=(IProperty) submodel2.getSubmodelElement("professors");
//              System.out.println("已经取出");

                Map<String,Professor> professorMap;
                try {
                    ObjectMapper objectMapper=new ObjectMapper();

                    professorMap=objectMapper.readValue((String) professors.getValue(), new TypeReference<Map<String, Professor>>() {});
//                          System.out.println("已经取出学生映射");

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                //课程集取出
                ISubmodel submodel3=manager.retrieveSubmodel(SchoolAAS.schoolAASID,SchoolAAS.classroomSubmodelID);

                IProperty classroom=(IProperty) submodel3.getSubmodelElement("classrooms");
//              System.out.println("已经取出");

                Map<String,Classroom> classroomMap;
                try {
                    ObjectMapper objectMapper=new ObjectMapper();

                    classroomMap=objectMapper.readValue((String) classroom.getValue(), new TypeReference<Map<String, Classroom>>() {});
//                          System.out.println("已经取出学生映射");

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                System.out.println(Schedule.DAYS_OF_WEEK[Day]);
                System.out.println("################################################################");

                if (running){
//                      System.out.println(running);
//                      System.out.println("小循环没挺");
                    System.out.println(Schedule.TIME_SLOTS[Time]);
                    System.out.println("---------------------------------------------------");
                    //正常是2条线程，因为还没加入教授所以用1条来测试
                    CountDownLatch countDownLatch=new CountDownLatch(2);



                    //遍历
                    int finalTime = Time;
                    int finalDay = Day;
                    new Thread(()->{
//                          System.out.println("进入遍历线程");
                        if(running){
//                              System.out.println("进入遍历");
//                              System.out.println("学生映射的大小：" + studentMap.size());
                            for(Student student:studentMap.values()){
                                //当教室类出来后会有具体的教室
//                               System.out.println("!!");
                                System.out.println(Schedule.TIME_SLOTS[finalTime]+" || "+student.Name+" is going to take "+ student.Courses[finalDay][finalTime]+" from "+student.Room+" to "+Schedule.courseSchedules.get(student.Courses[finalDay][finalTime])[finalDay]);
                                //动态改变教室类
                                Classroom preClassroom=classroomMap.get(student.Room);
                                Classroom nextClassroom=classroomMap.get(Schedule.courseSchedules.get(student.Courses[finalDay][finalTime])[finalDay]);
                                if(preClassroom!=null){
                                    for(Student student1:preClassroom.getStudents()){
                                        if(student1.Room.equals(student.Room)){
                                            preClassroom.getStudents().remove(student1);
                                            break;
                                        }
                                    }
                                }

                                //改变学生当前位置

                                student.setRoom(Schedule.courseSchedules.get(student.Courses[finalDay][finalTime])[finalDay]);
                                nextClassroom.addStudent(student);


                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }


                        }
                        countDownLatch.countDown();

                    }).start();
                    //遍历
                    new Thread(()->{
//                          System.out.println("进入遍历线程");
                        if(running){
//                              System.out.println("进入遍历");
//                              System.out.println("学生映射的大小：" + studentMap.size());
                            for(Professor professor:professorMap.values()){
                                if(professor.getCoursesTaught()[finalDay][finalTime]!=null){
                                    //当教室类出来后会有具体的教室
//                               System.out.println("!!");
                                    System.out.println(Schedule.TIME_SLOTS[finalTime]+" || "+professor.getName()+" is going to teach "+ professor.getCoursesTaught()[finalDay][finalTime]+" from "+professor.getRoom()+" to "+Schedule.courseSchedules.get(professor.getCoursesTaught()[finalDay][finalTime]+" by "+professor.getName())[finalDay]);
                                    //动态改变教室类
                                    Classroom preClassroom=classroomMap.get(professor.getRoom());
                                    Classroom nextClassroom=classroomMap.get(Schedule.courseSchedules.get(professor.getCoursesTaught()[finalDay][finalTime]+" by "+professor.getName())[finalDay]);
                                    if(preClassroom!=null){
                                        preClassroom.getProfessors().remove(0);
                                    }
                                    //改变学生当前位置

                                    professor.setRoom(Schedule.courseSchedules.get(professor.getCoursesTaught()[finalDay][finalTime]+" by "+professor.getName())[finalDay]);
                                    if(!nextClassroom.getProfessors().isEmpty()){
                                        nextClassroom.getProfessors().remove(0);
                                    }
                                    nextClassroom.getProfessors().add(professor);


                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                }

                            }


                        }
                        countDownLatch.countDown();
                    }).start();                    //遍历
                    for(Student student:studentMap.values()){
                        student.setRoom("Dorm");
                    }


                    try {
                        countDownLatch.await();
                        for(Classroom classroom1:classroomMap.values()){

                            System.out.println(classroom1.getRoomNumber()+" now has those Students: " +Arrays.toString(classroom1.getStudents().toArray() ));
                            System.out.println(classroom1.getRoomNumber()+" now has those Professors: " +Arrays.toString(classroom1.getProfessors().toArray()) );
                            System.out.println(classroom1.getRoomNumber()+" now is being used to taught  " +classroom1.getCourses()[finalDay][finalTime]);
                            System.out.println();




                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    try {
                        System.out.println("---------------------------------------------------");
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if(Time==3){
                        for(Professor professor:professorMap.values()){
                            professor.setRoom("Dorm");
                        }
                        for(Classroom classroom1:classroomMap.values()){
                            classroom1.getProfessors().clear();
                            classroom1.getStudents().clear();
                        }
                        Day++;
                        Day=Day%5;
                    }
                    Time++;
                    Time=Time%4;

                }

                System.out.println("################################################################");



                currentTaskNum.countDown();
            }).start();
        };
        iHandlerMap.put("timeSimulation",timeSimulation);



        //说明书
        iHandlerMap.put("instructionBook", new IHandler() {
            @Override
            public void handle(ConnectedAssetAdministrationShellManager manager) {
                System.out.println("You can call corresponding method by typing those KEY WORD......");
                for(String s:iHandlerMap.keySet()){
                    System.out.println(s);

                }

                System.out.println("stop");
                System.out.println("exit");
                currentTaskNum.countDown();
            }
        });

        //获取AAS信息
        iHandlerMap.put("getInformationFromAAS", new IHandler() {
            @Override
            public void handle(ConnectedAssetAdministrationShellManager manager) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Scanner scanner2=new Scanner(System.in);

                        String AASID_STR="";
                        do{
                            System.out.println("Please type '#' before your command......");
                            System.out.println("Please type AAS ID......");
                            AASID_STR=scanner2.nextLine();
//                            System.out.println("成功scanner"+AASID_STR);
                        }while (running&&!AASID_STR.contains("#"));
                        AASID_STR=new StringTokenizer(AASID_STR,"#").nextToken();
                        if(!running){
                            currentTaskNum.countDown();
                            return;
                        }
                        IAssetAdministrationShell assetAdministrationShell;
                        System.out.println("成功捕获AASid"+AASID_STR);
                        try {
                            assetAdministrationShell=manager.retrieveAAS(new CustomId(AASID_STR));
                            System.out.println("成功捕获AAS");
                        } catch (Exception e) {
                            System.out.println("Wrong AAS ID or Don't have a related AAS in server......");
                            currentTaskNum.countDown();
                            return;
                        }
                        System.out.println("What you want to get:\nJson file of this AAS (Type #Jsonfile) ? \nSubmodel of this AAS ?(Type #Subm) ?");
                        String key="";
                        do{
                            System.out.println("Please type '#' before your command......");
                            key=scanner2.nextLine();
                        }while (running&&!key.contains("#"));
                        key=new StringTokenizer(key,"#").nextToken();
                        if(!running){
                            currentTaskNum.countDown();
                            return;
                        }
                        if(key.equals("Jsonfile")||key.equals("Subm")){
                            if(key.equals("Jsonfile")){

                                try {
                                    Process process = Runtime.getRuntime().exec("cmd /c curl -X GET http://localhost:4001/aasServer/shells -H \"Accept: application/json\"");
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                                    StringBuilder content= new StringBuilder();
                                    String line;
                                    // 若读取当前行不为空，就将其输出
                                    while ((line = reader.readLine()) != null) {
                                        content.append(line);
                                    }
                                    ObjectMapper objectMapper=new ObjectMapper();
                                    try {
                                        objectMapper.writeValue(new File(AASID_STR+".json"), objectMapper.readValue(content.toString(), Object.class));
                                        System.out.println("JSON file created successfully.");
                                    } catch (IOException e) {
                                        System.err.println("Error writing JSON file: " + e.getMessage());
                                    }



                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                String SubMID_STR="";
                                System.out.println("Please type Submodel ID......");
                                do{
                                    System.out.println("Please type '#' before your command......");

                                    SubMID_STR=scanner2.nextLine();
                                }while (running&&!SubMID_STR.contains("#"));
                                SubMID_STR=new StringTokenizer(SubMID_STR,"#").nextToken();
                                if(!running){
                                    currentTaskNum.countDown();
                                    return;
                                }
                                ISubmodel submodel;

                                try {
                                    submodel=manager.retrieveSubmodel(new CustomId(AASID_STR),new CustomId(SubMID_STR));
                                } catch (Exception e) {
                                    System.out.println("Wrong Submodel ID or Don't have a related Submodel in server......");
                                    currentTaskNum.countDown();
                                    return;
                                }

                                System.out.println("What you want to get:\nJson file of this Submodel (Type #Jsonfile) ? \nProperty of this Submodel ?(Type #Property) ?");
                                String key2="";
                                do{
                                    System.out.println("Please type '#' before your command......");
                                    key2=scanner2.nextLine();
                                }while (running&&!key2.contains("#"));
                                key2=new StringTokenizer(key2,"#").nextToken();
                                if(!running){
                                    currentTaskNum.countDown();
                                    return;
                                }
                                if(key2.equals("Jsonfile")||key2.equals("Property")){
                                    if(key2.equals("Jsonfile")){

                                        try {
                                            Process process = Runtime.getRuntime().exec("cmd /c curl -X GET http://localhost:4001/aasServer/shells"+AASID_STR+"/aas/submodels/" +submodel.getIdShort()+"/submodel"+" -H \"Accept: application/json\"");
                                            System.out.println("cmd /c curl -X GET http://localhost:4001/aasServer/shells/"+AASID_STR+"/aas/submodels/" +submodel.getIdShort()+"/submodel"+"-H \"Accept: application/json\"");
                                            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                                            StringBuilder content= new StringBuilder();
                                            String line;
                                                    // 若读取当前行不为空，就将其输出
                                            while ((line = reader.readLine()) != null) {
                                                content.append(line);
                                            }
                                            ObjectMapper objectMapper=new ObjectMapper();
                                            try {
                                                objectMapper.writeValue(new File(SubMID_STR+".json"), objectMapper.readValue(content.toString(), Object.class));
                                                System.out.println("JSON file created successfully.");
                                            } catch (IOException e) {
                                                System.err.println("Error writing JSON file: " + e.getMessage());
                                            }



                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    else {
                                        String PropertyID_STR="";
                                        System.out.println("Please type Property ID......");
                                        do{


                                            System.out.println("Please type '#' before your command......");
                                            PropertyID_STR=scanner2.nextLine();
                                        }while (running&&!PropertyID_STR.contains("#"));
                                        PropertyID_STR=new StringTokenizer(PropertyID_STR,"#").nextToken();
                                        if(!running){
                                            currentTaskNum.countDown();
                                            return;
                                        }
                                        IProperty property;

                                        try {
                                            property=(IProperty) submodel.getSubmodelElement(PropertyID_STR);
//                                            System.out.println(currentTaskNum.getCount());

                                        } catch (Exception e) {
                                            System.out.println("Wrong Property ID or Don't have a related Property in server......");
                                            currentTaskNum.countDown();
                                            return;
                                        }

                                        System.out.println("What you want to do:\nAccess the Property(Type #Access) ? \nChange the Value of Property?(Type #Change)?");
                                        String movement="";
                                        do{


                                            System.out.println("Please type '#' before your command......");
                                            movement=scanner2.nextLine();
                                        }while (running&&!movement.contains("#"));
                                        movement=new StringTokenizer(movement,"#").nextToken();

                                        if(movement.equals("Access")){
                                            System.out.println(property.getValue());
                                        }else {
                                            System.out.println("Changing value must be simple Data Type, or Json String....");
                                            System.out.println("Type down the Data Type.......(#int, #char, #bolean, #String....)\n!!! Now it is only available to Json String, so you don't need to type down any thing....");
                                            String value="";
                                            System.out.println("Type down the Value.......(#....)\n!!! Now it is only available to Json String....");
                                            do{


                                                System.out.println("Please type '#' before your command......");
                                                value=scanner2.nextLine();
                                            }while (running&&!value.contains("#"));
                                            value=new StringTokenizer(value,"#").nextToken();
                                            System.out.println("Are You sure to change it ? (Yes: 1 or No: 2)");
                                            String choose=scanner2.nextLine();
                                            if(choose.equals("1")){
                                                property.setValue(value);
                                            }else {
                                                System.out.println("Successfully Cancel......");
                                            }
                                        }
                                    }
                                } else {
                                    System.out.println("Not available requirement......");

                                }
                            }
                        } else {
                            System.out.println("Not available requirement......");

                        }
                        currentTaskNum.countDown();
//                        System.out.println(currentTaskNum.getCount());
                    }
                }).start();

//                System.out.println(currentTaskNum.getCount());
            }
        });

        System.out.println("You can call corresponding method by typing those KEY WORD......");

        for(String s:iHandlerMap.keySet()){
            System.out.println(s);

        }

        System.out.println("stop");
        System.out.println("exit");
        //模拟突然叫停，然后接着续上的情况
//        try {
//            // 让主线程睡眠5秒（10000毫秒）
//            Thread.sleep(9000);
//        } catch (InterruptedException e) {
//            // 处理异常
//            System.out.println("主线程被中断");
//        }
//        running=false;
//        try {
//            // 让主线程睡眠5秒（5000毫秒）
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            // 处理异常
//            System.out.println("主线程被中断");
//        }
//        System.out.println("");
//        System.out.println("");
//        System.out.println("");
//        running=true;
//        daySimulation.handle(manager);
        //事件响应
        new Thread(()->{
            while(true){
                if(!taskQueue.isEmpty()){
                        System.out.println("Solving "+taskQueue.peek());
                        if(!taskQueue.peek().equals("getInformationFromAAS")){
                            currentTaskNum=new CountDownLatch(1);
                        }
                        iHandlerMap.get(taskQueue.poll()).handle(manager);
                    try {
                        currentTaskNum.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        }).start();
        //输入
        while (true){
        String key=scanner.nextLine();
        if(iHandlerMap.containsKey(key)){
            if(!key.equals("stop")&&!key.equals("exit")){
                if(!taskQueue.contains(key)){
                    taskQueue.offer(key);
                    System.out.println("This task has been added into wait list Successfully, please wait.......");
                    if(key.equals("getInformationFromAAS")){
                        try {
                            currentTaskNum=new CountDownLatch(1);
                            System.out.println("there is no more calling orders until current function ends, please wait.......");
                            currentTaskNum.await();

                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                } else {
                    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    System.out.println("This task has been added into wait list, please don't oder the same task and wait.......");
                    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                }


            }
        } else {
            if(key.equals("stop")||key.equals("exit")) {
                if(key.equals("stop")){
                    running=false;

                    try {
                        Thread.sleep(5000);
                        System.out.println("--------------------------------------");
                        System.out.println("Successfully stop the tasks");
                        System.out.println("--------------------------------------");
                        running=true;
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    System.exit(0);
                }
            } else System.out.println("Not an available order......");
        }
        }
    }





}

interface IHandler {
    public void handle(ConnectedAssetAdministrationShellManager manager);
}