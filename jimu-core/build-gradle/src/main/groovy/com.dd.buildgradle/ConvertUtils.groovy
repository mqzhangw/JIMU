package com.dd.buildgradle

import com.android.SdkConstants
import com.android.build.api.transform.TransformInput
import javassist.ClassPool
import javassist.CtClass
import javassist.NotFoundException
import org.apache.commons.io.FileUtils

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.regex.Matcher

class ConvertUtils {
    static boolean ignore(String className) {
        try {
            return className.contains(".R\$") ||
                    className.endsWith("R" + SdkConstants.DOT_CLASS) ||
                    className.endsWith("R2" + SdkConstants.DOT_CLASS) ||
                    className.startsWith("META-INF")

        } catch (Exception e) {
            e.printStackTrace()
        }
        return true
    }

    static List<CtClass> toCtClasses(Collection<TransformInput> inputs, ClassPool classPool) {
        List<String> classNames = new ArrayList<>()
        List<CtClass> allClass = new ArrayList<>()
        inputs.each {
            it.directoryInputs.each {
                def dirPath = it.file.absolutePath
                classPool.insertClassPath(it.file.absolutePath)
                FileUtils.listFiles(it.file, null, true).each {
                    if (it.absolutePath.endsWith(SdkConstants.DOT_CLASS)) {
                        def className = it.absolutePath.substring(dirPath.length() + 1, it.absolutePath.length() - SdkConstants.DOT_CLASS.length())
                                .replaceAll(Matcher.quoteReplacement(File.separator), '.')
                        if (!ignore(className)) {
                            if (classNames.contains(className)) {
                                if (!className.contains("BuildConfig"))
                                    throw new RuntimeException("directoryInputs:You have duplicate classes with the same name : " + className + " please remove duplicate classes ")
                            } else {
                                classNames.add(className)
                            }
                        }
                    }
                }
            }

            it.jarInputs.each {
                classPool.insertClassPath(it.file.absolutePath)
                def jarFile = new JarFile(it.file)
                Enumeration<JarEntry> classes = jarFile.entries()
                try {
                    while (classes.hasMoreElements()) {
                        JarEntry libClass = classes.nextElement()
                        String className = libClass.getName()
                        if (className.endsWith(SdkConstants.DOT_CLASS)) {
                            className = className.substring(0, className.length() - SdkConstants.DOT_CLASS.length()).replaceAll('/', '.')
                            if (!ignore(className)) {
                                if (classNames.contains(className)) {
                                    if (!className.contains("BuildConfig") /*&& !className.contains("module-info")*/)
                                        throw new RuntimeException("jarInputs:You have duplicate classes with the same name : " + className + " please remove duplicate classes ")
                                } else {
                                    classNames.add(className)
                                }
                            }
                        }
                    }
                } finally {
                    jarFile.close()
                }
            }
        }
        classNames.each {
            try {
                allClass.add(classPool.get(it))
            } catch (NotFoundException e) {
                println "class not found exception class name:  $it ,$e.getMessage()"
            }
        }
        return allClass
    }

}