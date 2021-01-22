package com.dd.buildgradle

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import javassist.*
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.github.jimu.msg.MsgBridgeService
import org.github.jimu.msg.ServiceInfoBean
import org.gradle.api.Project
import osp.leobert.magnet.Log

class ComCodeTransform extends Transform {

    private Project project
    ClassPool classPool
    String applicationName

    ComCodeTransform(Project project) {
        this.project = project
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException,
            InterruptedException, IOException {

        getRealApplicationName(transformInvocation.getInputs())
        classPool = new ClassPool()
        project.android.bootClasspath.each {
            classPool.appendClassPath((String) it.absolutePath)
        }

        def box = ConvertUtils.toCtClasses(transformInvocation.getInputs(), classPool)

        //要收集的application，一般情况下只有一个
        List<CtClass> applications = new ArrayList<>()
        //要收集的applicationlikes，一般情况下有几个组件就有几个applicationlike
        List<CtClass> activators = new ArrayList<>()

        List<ServiceInfoBean> serviceInfoBeans = []

        for (CtClass ctClass : box) {
            try {

                if (isMsgBridgeService(ctClass)) {
                    MsgBridgeService annotation = ctClass.getAnnotation(MsgBridgeService.class)
                    ServiceInfoBean bean = new ServiceInfoBean(annotation.workProcessName(), ctClass)
                    serviceInfoBeans.add(bean)
                }
            } catch (Exception e) {
                Log.info("exception in isMsgBridgeService:" + e.localizedMessage)
                throw e
            }

            try {

                if (isApplication(ctClass)) {
                    applications.add(ctClass)
                    continue
                }
            } catch (Exception e) {
                Log.info("exception in isApplication:" + e.localizedMessage)
                throw e
            }

            try {
                if (isActivator(ctClass)) {
                    activators.add(ctClass)
                }
            } catch (Exception e) {
                Log.info("exception in isActivator:" + e.localizedMessage)
                throw e
            }

        }
        for (CtClass ctClass : applications) {
            System.out.println("Application is   " + ctClass.getName())
        }
        for (CtClass ctClass : activators) {
            System.out.println("ApplicationLike will be auto register: " + ctClass.getName())
        }

        serviceInfoBeans?.forEach({
            System.out.println("find MessageBridgeService: " + it.toString())
        })

        transformInvocation.inputs.each { TransformInput input ->
            //对类型为jar文件的input进行遍历
            input.jarInputs.each { JarInput jarInput ->
                //jar文件一般是第三方依赖库jar文件
                // 重命名输出文件（同目录copyFile会冲突）
                def jarName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                //生成输出路径
                def dest = transformInvocation.outputProvider.getContentLocation(jarName + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
                //将输入内容复制到输出
                FileUtils.copyFile(jarInput.file, dest)
            }

            //对类型为“文件夹”的input进行遍历
            input.directoryInputs.each { DirectoryInput directoryInput ->
                boolean isRegisterCompoAuto = project.extensions.combuild.isRegisterCompoAuto

                boolean useMaat = project.extensions.combuild.useMaat

                System.out.println(">>>")
                System.out.println(">>>")
                System.out.println(">>>")
                System.out.println(">>>")

                System.out.println("check isRegisterCompoAuto:  "
                        + directoryInput.file.getPath()
                        + " ; isRegisterCompoAuto:"
                        + isRegisterCompoAuto)

                String fileName = directoryInput.file.absolutePath
                File dir = new File(fileName)
                dir.eachFileRecurse { File file ->
                    String filePath = file.absolutePath
                    String classNameTemp = filePath.replace(fileName, "")
                            .replace("\\", ".")
                            .replace("/", ".")
                    if (classNameTemp.endsWith(".class")) {
                        String className = classNameTemp.substring(1, classNameTemp.length() - 6)
                        if (className == applicationName) {

                            injectEventManagerInitializeCode(applications[0], serviceInfoBeans, fileName)

                            //auto register component
                            if (isRegisterCompoAuto)
                                injectApplicationCode(applications.get(0), activators, fileName, useMaat)

                            applications[0].detach()
                        }
                    }
                }

                def dest = transformInvocation.outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes,
                        directoryInput.scopes, Format.DIRECTORY)
                // 将input的目录复制到output指定目录
                FileUtils.copyDirectory(directoryInput.file, dest)

            }
        }
    }

    //osp.leobert.android.maat.Maat


    private void getRealApplicationName(Collection<TransformInput> inputs) {
        applicationName = project.extensions.combuild.applicationName
        if (applicationName == null || applicationName.isEmpty()) {
            throw new RuntimeException("you should set applicationName in combuild")
        }
    }

    private static void injectEventManagerInitializeCode(CtClass ctClassApplication,
                                                         List<CtClass> serviceInfoBeans,
                                                         String patch) {
        System.out.println("injectEventManagerInitializeCode begin")
        ctClassApplication.defrost()
        try {
            CtMethod attachBaseContextMethod = ctClassApplication.getDeclaredMethod("onCreate", null)
            attachBaseContextMethod.insertBefore(generateEventManagerInitializeCode(serviceInfoBeans))
        } catch (CannotCompileException | NotFoundException e) {

            System.out.println("could not found onCreate in Application;   " + e.toString())

            StringBuilder methodBody = new StringBuilder()
            methodBody.append("protected void onCreate() {")
            methodBody.append("super.onCreate();")
            methodBody.append(generateEventManagerInitializeCode(serviceInfoBeans))
            methodBody.append("}")
            ctClassApplication.addMethod(CtMethod.make(methodBody.toString(), ctClassApplication))
        } catch (Exception e) {
            System.out.println("could not create onCreate() in Application;   " + e.toString())
        }
        ctClassApplication.writeFile(patch)
//        ctClassApplication.detach()

        System.out.println("injectEventManagerInitializeCode success ")

    }


    private static void injectApplicationCode(CtClass ctClassApplication,
                                              List<ServiceInfoBean> activators,
                                              String patch,
                                              boolean useMaat) {
        System.out.println("injectApplicationCode begin")
        ctClassApplication.defrost()
        try {
            CtMethod attachBaseContextMethod = ctClassApplication.getDeclaredMethod("onCreate", null)
            attachBaseContextMethod.insertAfter(getAutoLoadComCode(activators, useMaat))
        } catch (CannotCompileException | NotFoundException e) {

            System.out.println("could not found onCreate in Application;   " + e.toString())

            StringBuilder methodBody = new StringBuilder()
            methodBody.append("protected void onCreate() {")
            methodBody.append("super.onCreate();")
            methodBody.append(getAutoLoadComCode(activators, useMaat))
            methodBody.append("}")
            ctClassApplication.addMethod(CtMethod.make(methodBody.toString(), ctClassApplication))
        } catch (Exception e) {
            System.out.println("could not create onCreate() in Application;   " + e.toString())
        }
        ctClassApplication.writeFile(patch)
//        ctClassApplication.detach()

        System.out.println("injectApplicationCode success ")
    }

    static String generateEventManagerInitializeCode(List<ServiceInfoBean> serviceInfoBeans) {
        StringBuilder initializeCodeBuilder = new StringBuilder()
        serviceInfoBeans?.forEach({
            //   EventManager.appendMapper("pname", XXX.class);

            initializeCodeBuilder.append("org.github.jimu.msg.EventManager.appendMapper(\"")
                    .append(it.fullProcessName)
                    .append("\",")
                    .append(it.serviceClass.getName())
                    .append(".class);")
        })
        return initializeCodeBuilder.toString()
    }

    private static String getAutoLoadComCode(List<CtClass> activators, boolean useMaat) {
        StringBuilder autoLoadComCode = new StringBuilder()
        for (CtClass ctClass : activators) {
            autoLoadComCode.append("new " + ctClass.getName() + "()" + ".onCreate();")
        }

        if (useMaat)
            autoLoadComCode.append("osp.leobert.android.maat.Maat.Companion.getDefault().start();")

        return autoLoadComCode.toString()
    }


    private boolean isApplication(CtClass ctClass) {
        try {
            if (applicationName != null && applicationName == ctClass.getName()) {
                return true
            }
        } catch (Exception ignore) {
            println "class not found exception, class name:  " + ctClass.getName()
        }
        return false
    }

    /**
     * check if the class is implement of IApplication and isRegisterCompoAuto
     * @param ctClass target class to be checked
     * @return true if impl& isRegisterCompoAuto
     */
    private static boolean isActivator(CtClass ctClass) {
        try {
            for (CtClass ctClassInter : ctClass.getInterfaces()) {
                if ("com.luojilab.component.componentlib.applicationlike.IApplicationLike" == ctClassInter.name) {
                    boolean hasManualNotation = ctClass.hasAnnotation(Class.forName("com.luojilab.component.componentlib.applicationlike.RegisterCompManual"))
//                    return true
                    System.out.println(">>>> " + ctClass + " manual register?" + hasManualNotation)
                    return !hasManualNotation
                }
            }
        } catch (Exception e) {
            println "isActivator got exception :" + ctClass.getName() + "   ;  " + e.toString()
        }

        return false
    }

    private static boolean isMsgBridgeService(CtClass ctClass) {
        try {
            return ctClass.hasAnnotation(MsgBridgeService.class)
        } catch (Exception e) {
            e.printStackTrace()
        }
        return false
    }

    @Override
    String getName() {
        return "ComponentCode"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

}