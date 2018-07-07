## JIMU

[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/luojilab/DDComponentForAndroid/pulls)
[![License](https://img.shields.io/badge/License-Apache%202.0-orange.svg)](https://github.com/luojilab/DDComponentForAndroid/blob/master/LICENSE) 

### 项目介绍
JIMU（积木）是一套Android组件化框架，支持组件的代码资源隔离、单独调试、集成调试、组件交互、UI跳转、生命周期等完整功能。

取名为JIMU（积木），其含义是应用这套框架可以做到组件之间的完全隔离，每个组件可以单独运行，同时又可以通过“接口”任意拼接成一个完成APP，这种能力就是我们实施组件化的最终目的。

### 最新版本

[release-note&change-logs](https://github.com/mqzhangw/JIMU/releases) 关注版本变更以及注意事项是个好习惯。

模块|build-gradle|componentlib|router-anno-compiler|router-annotation
---|---|---|---|---
最新版本|[![Download](https://api.bintray.com/packages/zhmqq0527/compbuild/build-gradle/images/download.svg)](https://bintray.com/zhmqq0527/compbuild/build-gradle/_latestVersion)|[![Download](https://api.bintray.com/packages/zhmqq0527/compbuild/componentlib/images/download.svg)](https://bintray.com/zhmqq0527/compbuild/componentlib/_latestVersion)|[![Download](https://api.bintray.com/packages/zhmqq0527/compbuild/router-anno-compiler/images/download.svg)](https://bintray.com/zhmqq0527/compbuild/router-anno-compiler/_latestVersion)|[![Download](https://api.bintray.com/packages/zhmqq0527/compbuild/router-annotation/images/download.svg)](https://bintray.com/zhmqq0527/compbuild/router-annotation/_latestVersion)

仓库2：

模块|build-gradle|componentlib|router-anno-compiler|router-annotation
---|---|---|---|---
最新版本|[![Download](https://api.bintray.com/packages/leobert-lan-oss/maven/build-gradle/images/download.svg)](https://api.bintray.com/packages/leobert-lan-oss/maven/build-gradle/_latestVersion)|[![Download](https://api.bintray.com/packages/leobert-lan-oss/maven/componentlib/images/download.svg)](https://bintray.com/leobert-lan-oss/maven/componentlib/_latestVersion)|[![Download](https://api.bintray.com/packages/leobert-lan-oss/maven/router-anno-compiler/images/download.svg)](https://bintray.com/leobert-lan-oss/maven/router-anno-compiler/_latestVersion)|[![Download](https://api.bintray.com/packages/leobert-lan-oss/maven/router-annotation/images/download.svg)](https://bintray.com/leobert-lan-oss/maven/router-annotation/_latestVersion)

*因为没有创建组织账号，可能会发布到不同的仓库，出现版本差异时请关注下release-note*

### 实现功能：
- 组件可以单独调试
- 杜绝组件之前相互耦合，代码完全隔离，彻底解耦
- 组件之间通过接口+实现的方式进行数据传输
- 使用scheme和host路由的方式进行activity之间的跳转
- 自动生成路由跳转路由表
- 任意组件可以充当host，集成其他组件进行集成调试
- 可以动态对已集成的组件进行加载和卸载
- 支持kotlin组件


### 原理解析
组件化设计思路 [浅谈Android组件化](https://mp.weixin.qq.com/s/RAOjrpie214w0byRndczmg)

原理解释请参考文章[Android彻底组件化方案实践](http://www.jianshu.com/p/1b1d77f58e84)

demo解读请参考文章[Android彻底组件化demo发布](http://www.jianshu.com/p/59822a7b2fad)

### 使用指南
#### 1、主项目引用编译脚本
在根目录的gradle.properties文件中，增加属性：

```ini
mainmodulename=app
```
其中mainmodulename是项目中的host工程，一般为app

在根目录的build.gradle中增加配置

```gradle
buildscript {
    dependencies {
        classpath 'com.github.jimu:build-gradle:A.B.C'
    }
}
```
*current lastest version 1.3.2 has just post a request to includeed in the bintray's jCenter,maybe you cannot fetch it before the request has been approved*

为每个组件引入依赖库，如果项目中存在basiclib等基础库，可以统一交给basiclib引入

```gradle
compile 'com.github.jimu:componentlib:A.B.C'
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


### 组件化讨论群
JIMU的讨论群，群号693097923，欢迎大家加入：

![进群请扫码](https://upload-images.jianshu.io/upload_images/6650461-6adc3ed96ebd8d70.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

