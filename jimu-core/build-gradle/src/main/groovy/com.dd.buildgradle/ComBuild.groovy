package com.dd.buildgradle

import com.dd.buildgradle.exten.ComExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import com.dd.buildgradle.util.StringUtil
import org.gradle.api.Task
import osp.leobert.magnet.Log
import osp.leobert.magnet.plugin.manifest.ManifestMergerImpl
import osp.leobert.magnet.plugin.manifest.XmlnsSweeper

class ComBuild implements Plugin<Project> {

    //默认是app，直接运行assembleRelease的时候，等同于运行app:assembleRelease
    String compileModule = "app"

    ComExtension comBuild

    void apply(Project project) {
        comBuild = project.extensions.create('combuild', ComExtension)

        String taskNames = project.gradle.startParameter.taskNames.toString()
        Log.info("taskNames is " + taskNames)
        String module = project.path.replace(":", "")
        Log.info("current module is " + module)
        AssembleTask assembleTask = getTaskInfo(project.gradle.startParameter.taskNames)

        if (assembleTask.isAssemble) {
            fetchMainModuleName(project, assembleTask)
            Log.info("compile module is : " + compileModule)
        }

        if (!project.hasProperty("isRunAlone")) {
            throw new RuntimeException("you should set isRunAlone in " + module + "'s gradle.properties")
        }

        //对于isRunAlone==true的情况需要根据实际情况修改其值，
        // 但如果是false，则不用修改
        boolean isRunAlone = Boolean.parseBoolean((project.properties.get("isRunAlone")))
        String mainModuleName = project.rootProject.property("mainmodulename")
        if (isRunAlone && assembleTask.isAssemble) {
            //对于要编译的组件和主项目，isRunAlone修改为true，其他组件都强制修改为false
            //这就意味着组件不能引用主项目，这在层级结构里面也是这么规定的
            isRunAlone = module == compileModule || module == mainModuleName
        }
        project.setProperty("isRunAlone", isRunAlone)

        //根据配置添加各种组件依赖，并且自动化生成组件加载代码
        if (isRunAlone) {
            project.afterEvaluate {
                Log.info("target manifest is: " + comBuild.targetManifest.toString())
                //merge manifest
                project.extensions.android.applicationVariants.all { variant ->
                    // find seal process task
                    String variantName = variant.name.capitalize()
                    Task processManifestTask = project.tasks["process${variantName}Manifest"]
                    processManifestTask.outputs.upToDateWhen { false }

                    def mergeTask = project.task("runaloneMerge${variantName}Manifest").doLast {

                        Log.info("runaloneMerge...Manifest, mergeEnabled? " + comBuild.enableManifestMerge)
                        if (comBuild.enableManifestMerge) {
                            def manifestMergerImpl = new ManifestMergerImpl(new File(comBuild.originalManifest),
                                    new File(comBuild.runAloneManifest))
                            manifestMergerImpl.merge(project.getLogger())
                            manifestMergerImpl.dest(comBuild.targetManifest)
                        }
                    }
                    processManifestTask.dependsOn mergeTask

                    processManifestTask.doLast {
                        def processManifestOutputFilePath = comBuild.targetManifest
                        Log.info("start handle xmlns:" + processManifestOutputFilePath)

                        File manifestFile = new File(processManifestOutputFilePath)
                        if (manifestFile.exists() /*&& manifestFile.name == "AndroidManifest.xml"*/) {
                            String manifestFileContent = manifestFile.text
                            StringBuilder builder = new StringBuilder(manifestFileContent)

                            manifestFile.text = builder.toString()
                        }
                        XmlnsSweeper.sweep(processManifestOutputFilePath)
                    }
                }
            }

            project.apply plugin: 'com.android.application'
            if (module != mainModuleName) {

                project.android.sourceSets {
                    main {
                        //使用新特性中的合并manifest产物
                        manifest.srcFile 'src/main/runalone/mergedManifest.xml'
//                        manifest.srcFile 'src/main/runalone/runaloneManifest.xml'
                        java.srcDirs = ['src/main/java', 'src/main/runalone/java', 'src/main/runalone/kotlin']
                        res.srcDirs = ['src/main/res', 'src/main/runalone/res']
                        assets.srcDirs = ['src/main/assets', 'src/main/runalone/assets']
                        jniLibs.srcDirs = ['src/main/jniLibs', 'src/main/runalone/jniLibs']
                    }
                }
            }
            Log.info("apply plugin is " + 'com.android.application')
            if (assembleTask.isAssemble && module.equals(compileModule)) {
                compileComponents(assembleTask, project)
                project.android.registerTransform(new ComCodeTransform(project))
            }
        } else {
            project.apply plugin: 'com.android.library'
            Log.info("apply plugin is " + 'com.android.library')
        }

    }

    /**
     * 根据当前的task，获取要运行的组件，规则如下：
     * assembleRelease ---app
     * app:assembleRelease :app:assembleRelease ---app
     * sharecomponent:assembleRelease :sharecomponent:assembleRelease ---sharecomponent
     * @param assembleTask
     */
    private void fetchMainModuleName(Project project, AssembleTask assembleTask) {
        if (!project.rootProject.hasProperty("mainmodulename")) {
            throw new RuntimeException("you should set compilemodule in rootproject's gradle.properties")
        }
        if (assembleTask.modules.size() > 0 && assembleTask.modules.get(0) != null
                && assembleTask.modules.get(0).trim().length() > 0
                && !assembleTask.modules.get(0).equals("all")) {
            compileModule = assembleTask.modules.get(0)
        } else {
            compileModule = project.rootProject.property("mainmodulename")
        }
        if (compileModule == null || compileModule.trim().length() <= 0) {
            compileModule = "app"
        }
    }

    private AssembleTask getTaskInfo(List<String> taskNames) {
        AssembleTask assembleTask = new AssembleTask()
        for (String task : taskNames) {
            if (task.toUpperCase().contains("ASSEMBLE")
                    || task.contains("aR")
                    || task.contains("asR")
                    || task.contains("asD")
                    || task.toUpperCase().contains("TINKER")
                    || task.toUpperCase().contains("INSTALL")
                    || task.toUpperCase().contains("RESGUARD")) {
                if (task.toUpperCase().contains("DEBUG")) {
                    assembleTask.isDebug = true
                }
                assembleTask.isAssemble = true
                Log.info("debug assembleTask info:"+task)
                String[] strs = task.split(":")
                assembleTask.modules.add(strs.length > 1 ? strs[strs.length - 2] : "all")
                break
            }
        }
        return assembleTask
    }

    /**
     * 自动添加依赖，只在运行assemble任务的才会添加依赖，因此在开发期间组件之间是完全感知不到的，这是做到完全隔离的关键
     * 支持两种语法：module或者groupId:artifactId:version(@aar),前者之间引用module工程，后者使用maven中已经发布的aar
     * @param assembleTask
     * @param project
     */
    private void compileComponents(AssembleTask assembleTask, Project project) {
        String components
        if (assembleTask.isDebug) {
            components = (String) project.properties.get("debugComponent")
        } else {
            components = (String) project.properties.get("compileComponent")
        }

        if (components == null || components.length() == 0) {
            Log.info("there is no add dependencies ")
            return
        }
        String[] compileComponents = components.split(",")
        if (compileComponents == null || compileComponents.length == 0) {
            Log.info("there is no add dependencies ")
            return
        }
        for (String str : compileComponents) {
            Log.info("comp is " + str)
            str = str.trim()
            if (str.startsWith(":")) {
                str = str.substring(1)
            }
            // 是否是maven 坐标
            if (StringUtil.isMavenArtifact(str)) {
                /**
                 * 示例语法:groupId:artifactId:version(@aar)
                 * compileComponent=com.luojilab.reader:readercomponent:1.0.0
                 * 注意，前提是已经将组件aar文件发布到maven上，并配置了相应的repositories
                 */
                project.dependencies.add("compile", str)
                Log.info("add dependencies lib  : " + str)
            } else {
                /**
                 * 示例语法:module
                 * compileComponent=readercomponent,sharecomponent
                 */
                project.dependencies.add("compile", project.project(':' + str))
                Log.info("add dependencies project : " + str)
            }
        }
    }

    private class AssembleTask {
        boolean isAssemble = false
        boolean isDebug = false
        List<String> modules = new ArrayList<>()
    }

}