import com.fasterxml.jackson.databind.ObjectMapper;
import io.moquette.broker.Server;
import io.moquette.broker.config.ClasspathResourceLoader;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.IResourceLoader;
import io.moquette.broker.config.ResourceLoaderConfig;
import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.components.registry.RegistryComponent;
import org.eclipse.basyx.components.registry.configuration.BaSyxRegistryConfiguration;
import org.eclipse.basyx.components.registry.configuration.RegistryBackend;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SchoolAAS {
    private  static Server mqttBroker;
    public static final String REGISTRYPATH = "http://localhost:4000/registry";
    public static final String AASSERVERPATH = "http://localhost:4001/aasServer";
    public static IIdentifier studentSubmodelID=new CustomId("eclipse.basyx.submodel.student");
    public static IIdentifier professorSubmodelID=new CustomId("eclipse.basyx.submodel.professor");
    public static IIdentifier classroomSubmodelID=new CustomId("eclipse.basyx.submodel.classroom");
    public static IIdentifier schoolAssetID=new CustomId("eclipse.basyx.asset.school");
    public static IIdentifier schoolAASID=new CustomId("eclipse.basyx.aas.school");

    public static void main(String[] args) throws IOException {
        starMqttBroker();
        startAASRegistry();
        startAASServer();

        ConnectedAssetAdministrationShellManager manager=new ConnectedAssetAdministrationShellManager(new AASRegistryProxy(REGISTRYPATH));
        Asset asset=new Asset("school",schoolAssetID, AssetKind.INSTANCE);
        AssetAdministrationShell aas=new AssetAdministrationShell("schoolAAS",schoolAASID,asset);
        ObjectMapper objectMapper=new ObjectMapper();
        //学生子模型
        Submodel studentSubM=new Submodel("studentSubM",studentSubmodelID);
        studentSubM.setDescription(new LangStrings("English","All the properties, functions and movements about students"));
        //学生群体性质
        Map<String,Student> studentMap=new HashMap<>();

        studentMap.put("1001",new Student("Richard","1001",Schedule.StudentSchedule1));
        studentMap.put("1002",new Student("Jack","1002",Schedule.StudentSchedule2));
        studentMap.put("1003",new Student("Jen","1003",Schedule.StudentSchedule3));
        studentMap.put("1004",new Student("Justin","1004",Schedule.StudentSchedule4));
        studentMap.put("1005",new Student("Mark","1005",Schedule.StudentSchedule5));
        studentMap.put("1006",new Student("Copper","1006",Schedule.StudentSchedule6));
        studentMap.put("1007",new Student("Barry","1007",Schedule.StudentSchedule7));
        studentMap.put("1008",new Student("Frank","1008",Schedule.StudentSchedule8));
        String studentMapJson=objectMapper.writeValueAsString(studentMap);
        Property students=new Property("students",studentMapJson);
        studentSubM.addSubmodelElement(students);
        //学生行为以及模拟




        //教授子模型
        Submodel professorSubM = new Submodel("professorSubM", professorSubmodelID);
        professorSubM.setDescription(new LangStrings("English", "All the properties, functions and movements about professors"));

        Map<String, Professor> professorMap = new HashMap<>();
        professorMap.put("P001", new Professor("Professor A", "P001", Schedule.ProfessorASchedule));
        professorMap.put("P002", new Professor("Professor B", "P002", Schedule.ProfessorBSchedule));
        professorMap.put("P003", new Professor("Professor C", "P003", Schedule.ProfessorCSchedule));
        String professorMapJson = objectMapper.writeValueAsString(professorMap);
        Property professors = new Property("professors", professorMapJson);
        professorSubM.addSubmodelElement(professors);


        //教室子模型
        Submodel classroomSubM = new Submodel("classroomSubM", classroomSubmodelID);
        classroomSubM.setDescription(new LangStrings("English","All the properties, functions and movements about classrooms"));

        Map<String, Classroom> classroomMap = new HashMap<>();
        classroomMap.put("Room 101", new Classroom("Room 101", Schedule.classroomSchedule1));
        classroomMap.put("Room 102", new Classroom("Room 102", Schedule.classroomSchedule2));
        classroomMap.put("Room 103", new Classroom("Room 103", Schedule.classroomSchedule3));
        String classroomMapJson = objectMapper.writeValueAsString(classroomMap);
        Property classrooms = new Property("classrooms", classroomMapJson);
        classroomSubM.addSubmodelElement(classrooms);


        //注册

        manager.createAAS(aas,AASSERVERPATH);
        manager.createSubmodel(schoolAASID,studentSubM);
        manager.createSubmodel(schoolAASID,professorSubM);
        manager.createSubmodel(schoolAASID,classroomSubM);
    }









    //开启注册表 "http://localhost:4000/"
    private static void startAASRegistry(){
        BaSyxContextConfiguration contextConfiguration=new BaSyxContextConfiguration(4000,"/registry");
        contextConfiguration.setAccessControlAllowOrigin("*");

        BaSyxRegistryConfiguration registryConfiguration=new BaSyxRegistryConfiguration(RegistryBackend.INMEMORY);

        RegistryComponent registry=new RegistryComponent(contextConfiguration,registryConfiguration);

        registry.startComponent();
    }

    //开启AAS托管服务器 "http://localhost:4001/"
    private static void startAASServer(){
        BaSyxContextConfiguration contextConfiguration=new BaSyxContextConfiguration(4001,"/aasServer");
        contextConfiguration.setAccessControlAllowOrigin("*");

        BaSyxAASServerConfiguration aasServerConfiguration=new BaSyxAASServerConfiguration();
        aasServerConfiguration.loadFromDefaultSource();



        AASServerComponent aasServer=new AASServerComponent(contextConfiguration,aasServerConfiguration);
        aasServer.startComponent();
    }

    protected static void starMqttBroker() throws IOException {
        mqttBroker=new Server();
        IResourceLoader classpathLoader= new ClasspathResourceLoader();
        final IConfig clasPathConfig=new ResourceLoaderConfig(classpathLoader);
        mqttBroker.startServer(clasPathConfig);
    }
}

