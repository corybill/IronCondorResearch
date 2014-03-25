package code.com.corybill.spring;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 2/13/13
 * Time: 8:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class MySpringLoader {
    private static Logger log = Logger.getLogger(MySpringLoader.class);
    private static MySpringLoader instance;
    private static ApplicationContext context;

    private MySpringLoader(){
        try{
            context = new ClassPathXmlApplicationContext(
                    new String[] {"resources/applicationContext.xml"});
        }catch(Exception e){
            e.printStackTrace();
            log.error(e.getStackTrace());
        }
    }

    public static MySpringLoader getInstance(){
        if(instance == null){
            instance = new MySpringLoader();
        }
        return instance;
    }

    public ApplicationContext getContext() {
        return context;
    }
}
