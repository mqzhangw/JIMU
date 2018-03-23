package com.dd.buildgradle.task

import com.android.utils.FileUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskAction

class CopyApiTask extends DefaultTask {

    String compilemodule = "app"
    String components

    @TaskAction
    def copyApiToJava() {
        System.out.println("copyApiToJava begin")

        Copy copy = project.tasks.create("copytask", Copy)
        copy.includeEmptyDirs = false

        FileUtils.deleteDirectoryContents(project.file("../componentservice/src/main/java/"))

        //遍历 Project所依赖的组件 中的全部.api文件以及自己组件中的api文件，然后复制到componentservice库中
        println("------>  compilemodule is " + compilemodule)
        println("------>  need copy component is ：【" + components + "】")
        if (components == null || components.length() == 0) {
            println("------>  no component need copy ")
        } else {
            //通过 逗号分隔 转换为组件名数组
            String[] compileComponents = components.split(",")
            if (compileComponents == null || compileComponents.length == 0) {
                println("------>  no component need copy ")
            } else {
                //copy依赖的库的组件中的api
                for (String str : compileComponents) {
                    println("------>  add component :【" + str + "】")
                    copy.from "../${str}/src/main/java/"
                }
            }
        }

        //copy自己当前组件中的api
        println("------>  add self  component  :【" + compilemodule + "】")
        copy.from "../${compilemodule}/src/main/java/"
        //复制到 componentservice/src/main/java 目录中去
        copy.into "../componentservice/src/main/java/"
        //排除所有的.java文件
        copy.exclude '**/*.java'
        //包括所有的.api文件和ktapi文件
        copy.include '**/*.api'
        copy.include '**/*.ktapi'
        //改名
        copy.rename { String fileName ->
            println "------>  copy file :" + fileName
            fileName.replace('.api', '.java')
                    .replace('.ktapi', '.kt')
        }
        copy.execute()
    }

}