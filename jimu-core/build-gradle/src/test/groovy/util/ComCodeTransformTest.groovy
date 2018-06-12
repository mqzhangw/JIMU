package util

import com.dd.buildgradle.ComCodeTransform
import javassist.ClassPool
import javassist.CtClass
import org.github.jimu.msg.ServiceInfoBean

public class ComCodeTransformTest {

    @org.junit.Test
    public void testGenerateEventManagerInitializeCode() {

        CtClass ctClass = ClassPool.getDefault().makeClass("com.sss.Test");
        List<ServiceInfoBean> serviceInfoBeans = []
        serviceInfoBeans.add(new ServiceInfoBean("test",ctClass))
        String s = ComCodeTransform.generateEventManagerInitializeCode(serviceInfoBeans)

        System.out.println(s)
    }


}
