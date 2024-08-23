import io.moquette.broker.Server;
import io.moquette.broker.config.ClasspathResourceLoader;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.IResourceLoader;
import io.moquette.broker.config.ResourceLoaderConfig;
import org.apache.xmlbeans.ResourceLoader;
import org.apache.xmlbeans.impl.schema.ClassLoaderResourceLoader;
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
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Properties;

//建立一个本地的简易AAS
public class AAS_TEST {
    //首先建立关于注册表和服务器的 urls的公共静态变量
    public static final String REGISTRYPATH = "http://localhost:4000/registry";
    public static final String AASSERVERPATH = "http://localhost:4001/aasServer";

    //关于AAS/Submodle/Property 的 IDS
    public static final IIdentifier OVENASSID=new CustomId("eclipse.basyx.aas.oven");
    public static final IIdentifier DOCUSMID=new CustomId("eclipse.basyx.submodel.documentation");
    public static final String MAXTEMPID="maxTemp";
    //通信协议mqtt
    private  static Server mqttBroker;

    public static void main(String[] args) throws IOException{
        //首先需要开启通信协议，注册表和AAS的托管服务器
        starMqttBroker();
        startAASRegistry();
        startAASServer();

        //创造一个管理器-用于注册和部署AAS
        ConnectedAssetAdministrationShellManager manager=new ConnectedAssetAdministrationShellManager(new AASRegistryProxy(REGISTRYPATH));

        //创造一个AAS，并用管理器将其注册
        Asset asset=new Asset("ovenAsset",new CustomId("eclipse.basyx.asset.oven"), AssetKind.INSTANCE);
        AssetAdministrationShell shell=new AssetAdministrationShell("oven", OVENASSID, asset);

        manager.createAAS(shell,AASSERVERPATH);

        //创造“文件”类子模型
        Submodel docmentationSubmodel=new Submodel("documentationSm",DOCUSMID);

        //创造该子模型性质
        Property maxTemp =new Property(MAXTEMPID,1000);

        //将子性质添加到子模型中
        docmentationSubmodel.addSubmodelElement(maxTemp);

        //将子模型加入AAS
        manager.createSubmodel(shell.getIdentification(),docmentationSubmodel);


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

    protected static void starMqttBroker() throws IOException{
        mqttBroker=new Server();
        IResourceLoader classpathLoader= new ClasspathResourceLoader();
        final IConfig clasPathConfig=new ResourceLoaderConfig(classpathLoader);
        mqttBroker.startServer(clasPathConfig);
    }
}



//王子昂到此一游//nihaohhhhh
//创建client class 模拟对创建AAS的互动
class Client1{
    public static void main(String[] args) {
        //创造一个客户端的管理器和AAS服务器交互
        ConnectedAssetAdministrationShellManager manager=new ConnectedAssetAdministrationShellManager(new AASRegistryProxy(AAS_TEST.REGISTRYPATH));
        //下面将演示读取上面创建的AAS Doc子模型中，maxTemp这一性质


        //通过AAS ID和 Submodel ID 获取服务器中的子模型信息
        ISubmodel submodel = manager.retrieveSubmodel(AAS_TEST.OVENASSID,AAS_TEST.DOCUSMID);

        //通过性质的ID获取maxTemp 性质
        IProperty maxTemp=(IProperty) submodel.getSubmodelElement(AAS_TEST.MAXTEMPID);

        //print
        System.out.println(maxTemp.getIdShort()+" is "+maxTemp.getValue());

        //可以在客户端中改变value
        maxTemp.setValue(114514);

        //print
        System.out.println(maxTemp.getIdShort()+" is "+maxTemp.getValue());
    }
}