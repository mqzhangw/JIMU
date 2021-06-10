## JIMU

[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/luojilab/DDComponentForAndroid/pulls)
[![License](https://img.shields.io/badge/License-Apache%202.0-orange.svg)](https://github.com/luojilab/DDComponentForAndroid/blob/master/LICENSE) 

### 项目介绍
JIMU（积木）是一套Android组件化框架，支持组件的代码资源隔离、单独调试、集成调试、组件交互、UI跳转、生命周期等完整功能。

取名为JIMU（积木），其含义是应用这套框架可以做到组件之间的完全隔离，每个组件可以单独运行，同时又可以通过“接口”任意拼接成一个完成APP，这种能力就是我们实施组件化的最终目的。

### 最新版本

> 2021-06-10补充：
> 因为我的错误操作，导致发布到MavenCentral的版本：
> 
> * build-gradle：1.3.5
> * componentlib: 1.3.3
> * router-anno-compiler: 1.0.1 
> 均出现了Pom文件错误，遗失了dependency，再问题被修正前，可以按照hotfix-bad-pom分支的内容，做紧急修复。致歉 -- leobert

> 2021-05-23补充：
> 受JFrog运营策略影响，项目重新发布到MavenCentral，注意仓库变更

[release-note&change-logs](https://github.com/mqzhangw/JIMU/releases) 关注版本变更以及注意事项是个好习惯。

模块|build-gradle|componentlib|router-anno-compiler|router-annotation
---|---|---|---|---
最新版本|[![Download](https://img.shields.io/maven-central/v/io.github.leobert-lan/jimu-build-gradle.svg?label=Maven%20Central)](https://search.maven.org/artifact/io.github.leobert-lan/jimu-build-gradle)|[![Download](https://img.shields.io/maven-central/v/io.github.leobert-lan/jimu-componentlib.svg?label=Maven%20Central)](https://search.maven.org/artifact/io.github.leobert-lan/jimu-componentlib)|[![Download](https://img.shields.io/maven-central/v/io.github.leobert-lan/jimu-router-anno-compiler.svg?label=Maven%20Central)](https://search.maven.org/artifact/io.github.leobert-lan/jimu-router-anno-compiler)|[![Download](https://img.shields.io/maven-central/v/io.github.leobert-lan/jimu-router-annotation.svg?label=Maven%20Central)](https://search.maven.org/artifact/io.github.leobert-lan/jimu-router-annotation)


### 实现功能：
- 组件可以单独调试
- 杜绝组件之前相互耦合，代码完全隔离，彻底解耦
- 组件之间通过接口+实现的方式进行数据传输
- 使用scheme和host路由的方式进行activity之间的跳转
- 自动生成路由跳转路由表
- 任意组件可以充当host，集成其他组件进行集成调试
- 可以动态对已集成的组件进行加载和卸载
- 支持kotlin组件
- 组件独立运行的Manifest可以基于“壳”和组件原始的Manifest合并生成（from version 1.3.4）
- 组件初始化支持按序（from version 1.3.4）这部分基于[Maat](https://github.com/leobert-lan/Maat), [参考博客](https://juejin.im/post/6884492604370026503/)


### 原理解析
组件化设计思路 [浅谈Android组件化](https://mp.weixin.qq.com/s/RAOjrpie214w0byRndczmg)

原理解释请参考文章[Android彻底组件化方案实践](http://www.jianshu.com/p/1b1d77f58e84)

demo解读请参考文章[Android彻底组件化demo发布](http://www.jianshu.com/p/59822a7b2fad)

按序初始化业务组件请参考文章[组件化：代码隔离也难不倒组件的按序初始化 ](https://juejin.im/post/6884492604370026503/)

单项目，多module背景下，依赖方式的优雅实践探索：[三思系列：组件化场景下module依赖优雅实践方案 ](https://juejin.cn/post/6925629544946892813)

### 使用指南
#### 1、主项目引用编译脚本
在根目录的gradle.properties文件中，增加属性：

```ini
mainmodulename=app
```
其中mainmodulename是项目中的host工程，一般为app

添加mavenCentral仓库

在根目录的build.gradle中增加配置

```gradle
buildscript {
    dependencies {
        classpath 'io.github.leobert-lan:jimu-build-gradle:A.B.C'
    }
}
```
*A.B.C是版本号，最新的版本号可以参考上面的MavenCentral外链*

为每个组件引入依赖库，如果项目中存在basiclib等基础库，可以统一交给basiclib引入

```gradle
compile 'io.github.leobert-lan:jimu-componentlib:A.B.C'
```
*注意GroupId和ArtifactId在重新发布到MavenCentral后已经变更*

```gradle
'componentLib'        : 'io.github.leobert-lan:jimu-componentlib:{version}',
'router_anno'         : 'io.github.leobert-lan:jimu-router-annotation:{version}',
'router_anno_compiler': 'io.github.leobert-lan:jimu-router-anno-compiler:{version}',
```

#### 2、拆分组件为module工程
在每个组件的工程目录下新建文件gradle.properties文件，增加以下配置：

```ini
isRunAlone=true
debugComponent=sharecomponent
compileComponent=sharecomponent
```
上面三个属性分别对应是否单独调试、debug模式下依赖的组件，release模式下依赖的组件。具体使用方式请解释请参见上文第二篇文章

#### 3、应用组件化编译脚本
在组件和host的build.gradle都增加配置：

```gradle
apply plugin: 'com.dd.comgradle'
```

注意：不需要在引用com.android.application或者com.android.library

同时增加以下extension配置：

```gradle
combuild {
    applicationName = 'com.luojilab.reader.runalone.application.ReaderApplication'
    isRegisterCompoAuto = true
}
```
组件注册还支持反射的方式，有关isRegisterCompoAuto的解释请参见上文第二篇文章

##### 1.3.4新特性

```
def projectRoot = project.getRootProject().rootDir.absolutePath

combuild {
    applicationName = 'com.luojilab.reader.runalone.application.ReaderApplication'
    isRegisterCompoAuto = false

    originalManifest = projectRoot + "/readercomponent/src/main/AndroidManifest.xml"

    runAloneManifest = projectRoot + "/readercomponent/src/main/runalone/AndroidManifest.xml"
    targetManifest = projectRoot + "/readercomponent/src/main/runalone/mergedManifest.xml"
    //如果不需要合并，改为false
    enableManifestMerge = true
}

```

增加了5个可配项目：

* useMaat 默认为true，本处没有写，如果你不打算使用Maat，务必改为false，否则会织入代码并发生ClassNotFoundException
* originalManifest 原始manifest文件路径
* runAloneManifest 一个壳manifest，用于指定独立运行时额外需要的权限、Application配置，启动Activity、额外的四大组件，metadata
* targetManifest 合并后输出的manifest，需要先创建文件，runalone使用的manifest；*如不先创建会影响gradle任务，被认为是一个缺失manifest的Component！*
* enableManifestMerge 如果是true，则会在合适的时机执行manifest合并功能，并且插件中增加的如：runaloneMergeDebugManifest等任务会执行合并，否则该任务并不会合并manifest文件

#### 4、混淆
在混淆文件中增加如下配置

```
-keep interface * {
  <methods>;
}
-keep class com.luojilab.component.componentlib.** {*;}
-keep class com.luojilab.gen.router.** {*;}
-keep class * implements com.luojilab.component.componentlib.router.ISyringe {*;}
-keep class * implements com.luojilab.component.componentlib.applicationlike.IApplicationLike {*;}

```

*注意：com.luojilab.component.componentlib和com.luojilab.gen.router包可能在项目迁移的过程中发生过或即将发生变化，文档更新不一定及时，请手工确认一下生成类的包路径。*

关于如何进行组件之间数据交互和UI跳转，请参看 [Wiki](https://github.com/mqzhangw/JIMU/wiki)
关于消息中间件，请参考[v1.3.3releaseNote](https://github.com/mqzhangw/JIMU/releases/tag/v1.3.3)


### 组件化讨论群
JIMU的讨论群，群号693097923，欢迎大家加入：

![进群请扫码](https://upload-images.jianshu.io/upload_images/6650461-6adc3ed96ebd8d70.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

PS:最近千人群满了，建议一些基础性问题仔细琢磨下文章，比较麻烦的问题提issue求助。
