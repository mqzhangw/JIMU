## AndroidComponent使用指南
首先我们看一下demo的代码结构，然后根据这个结构图再次从单独调试（发布）、组件交互、UI跳转、集成调试、代码边界和生命周期等六个方面深入分析，之所以说“再次”，是因为上一篇文章我们已经讲了这六个方面的原理，这篇文章更侧重其具体实现。

![AndroidComponent结构图.png](http://upload-images.jianshu.io/upload_images/6650461-92c8e8a0a078f6ef.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/640)

 代码中的各个module基本和图中对应，从上到下依次是：
  - app是主项目，负责集成众多组件，控制组件的生命周期
  - reader和share是我们拆分的两个组件
   - componentservice中定义了所有的组件提供的服务
  - basicres定义了全局通用的theme和color等公共资源
   - basiclib中是公共的基础库，一些第三方的库（okhttp等）也统一交给basiclib来引入

图中没有体现的module有两个，一个是componentlib，这个是我们组件化的基础库，像Router/UIRouter等都定义在这里；另一个是build-gradle，这个是我们组件化编译的gradle插件，也是整个组件化方案的核心。
我们在demo中要实现的场景是：主项目app集成reader和share两个组件，其中reader提供一个读书的fragment给app调用（组件交互），share提供一个activity来给reader来调用（UI跳转）。主项目app可以动态的添加和卸载share组件（生命周期）。而集成调试和代码边界是通过build-gradle插件来实现的。
### 1 单独调试和发布
单独调试的配置与上篇文章基本一致，通过在组件工程下的gradle.properties文件中设置一个isRunAlone的变量来区分不同的场景，唯一的不同点是在组件的build.gradle中不需要写下面的样板代码：
```
if(isRunAlone.toBoolean()){
apply plugin: 'com.android.application'
}else{  
 apply plugin: 'com.android.library'
}
```
而只需要引入一个插件com.dd.comgradle（源码就在build-gradle）,在这个插件中会自动判断apply com.android.library还是com.android.application。实际上这个插件还能做更“智能”的事情，这个在集成调试章节中会详细阐述。
     单独调试所必须的AndroidManifest.xml、application、入口activity等类定义在src/main/runalone下面，这个比较简单就不赘述了。
     如果组件开发并测试完成，需要发布一个release版本的aar文件到中央仓库，只需要把isRunAlone修改为false，然后运行assembleRelease命令就可以了。这里简单起见没有进行版本管理，大家如果需要自己加上就好了。值得注意的是，发布组件是唯一需要修改isRunAlone=false的情况，即使后面将组件集成到app中，也不需要修改isRunAlone的值，既保持isRunAlone=true即可。所以实际上在Androidstudio中，是可以看到三个application工程的，随便点击一个都是可以独立运行的，并且可以根据配置引入其他需要依赖的组件。这背后的工作都由com.dd.comgradle插件来默默完成。

![项目中有三个application工程.png](http://upload-images.jianshu.io/upload_images/6650461-cabaa38ecd2e6a97.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
### 2 组件交互
在这里组件的交互专指组件之间的数据传输，在我们的方案中使用的是接口+实现的方式，组件之间完全面向接口编程。
在demo中我们让reader提供一个fragment给app使用来说明。首先reader组件在componentservice中定义自己的服务
```
public interface ReadBookService {
    Fragment getReadBookFragment();
}
```
然后在自己的组件工程中，提供具体的实现类ReadBookServiceImpl：
```
public class ReadBookServiceImpl implements ReadBookService {
    @Override
    public Fragment getReadBookFragment() {
        return new ReaderFragment();
    }
}
```
提供了具体的实现类之后，需要在组件加载的时候把实现类注册到Router中，具体的代码在ReaderAppLike中，ReaderAppLike相当于组件的application类，这里定义了onCreate和onStop两个生命周期方法，对应组件的加载和卸载。
```
public class ReaderAppLike implements IApplicationLike {
    Router router = Router.getInstance();
    @Override
    public void onCreate() {
        router.addService(ReadBookService.class.getSimpleName(), new ReadBookServiceImpl());
    }
    @Override
    public void onStop() {
        router.removeService(ReadBookService.class.getSimpleName());
    }
}
```
在app中如何使用如reader组件提供的ReaderFragment呢？注意此处app是看不到组件的任何实现类的，它只能看到componentservice中定义的ReadBookService，所以只能面向ReadBookService来编程。具体的实例代码如下：
```
Router router = Router.getInstance();
if (router.getService(ReadBookService.class.getSimpleName()) != null) {
    ReadBookService service = (ReadBookService) router.getService(ReadBookService.class.getSimpleName());
    fragment = service.getReadBookFragment();
    ft = getSupportFragmentManager().beginTransaction();
    ft.add(R.id.tab_content, fragment).commitAllowingStateLoss();
}
```
这里需要注意的是由于组件是可以动态加载和卸载的，因此在使用ReadBookService的需要进行判空处理。我们看到数据的传输是通过一个中央路由Router来实现的，这个Router的实现其实很简单，其本质就是一个HashMap，具体代码大家参见源码。
     通过上面几个步骤就可以轻松实现组件之间的交互，由于是面向接口，所以组件之间是完全解耦的。至于如何让组件之间在编译阶段不不可见，是通过上文所说的com.dd.comgradle实现的，这个在第一篇文章中已经讲到，后面会贴出具体的代码。
### 3 UI跳转
页面（activity）的跳转也是通过一个中央路由UIRouter来实现，不同的是这里增加了一个优先级的概念。（这块的代码参考了我之前在网易的技术老大的实现思路，在这里表示感谢，老大你永远是我的老大[色]）。具体的实现就不在这里赘述了，代码还是很清晰的。
页面的跳转通过短链的方式，例如我们要跳转到share页面，只需要调用
```
UIRouter.getInstance().openUri(getActivity(), "componentdemo://share", null);
```
具体是哪个组件响应componentdemo://share这个短链呢？这就要看是哪个组件处理了这个schme和host，在demo中share组件在自己实现的ShareUIRouter中声明了自己处理这个短链，具体代码如下：
```
private static final String SCHME = "componentdemo";
private static final String SHAREHOST = "share";
public boolean openUri(Context context, Uri uri, Bundle bundle) {
    if (uri == null || context == null) {
        return true;
    }
    String host = uri.getHost();
    if (SHAREHOST.equals(host)) {
        Intent intent = new Intent(context, ShareActivity.class);
        intent.putExtras(bundle == null ? new Bundle() : bundle);
        context.startActivity(intent);
        return true;
    }
    return false;
}
```
在这里如果已经组件已经响应了这个短链，就返回true，这样更低优先级的组件就不会接收到这个短链。
目前根据schme和host跳转的逻辑是开发人员自己编写的，这块后面要修改成根据注解生成。这部分已经有一些优秀的开源项目可以参考，如ARouter等。
### 4 集成调试
 集成调试可以认为由app或者其他组件充当host的角色，引入其他相关的组件一起参与编译，从而测试整个交互流程。在demo中app和reader都可以充当host的角色。在这里我们以app为例。
首先我们需要在根项目的gradle.properties中增加一个变量mainmodulename，其值就是工程中的主项目，这里是app。设置为mainmodulename的module，其isRunAlone永远是true。
然后在app项目的gradle.properties文件中增加两个变量：
```
debugComponent=readercomponent,com.mrzhang.share:sharecomponent
compileComponent=readercomponent,sharecomponent
```
其中debugComponent是运行debug的时候引入的组件，compileComponent是release模式下引入的组件。我们可以看到debugComponent引入的两个组件写法是不同的，这是因为组件引入支持两种语法，module或者modulePackage:module，前者直接引用module工程，后者使用componentrelease中已经发布的aar。
        注意在集成调试中，要引入的reader和share组件是不需要把自己的isRunAlone修改为false的。我们知道一个application工程是不能直接引用（compile）另一个application工程的，所以如果app和组件都是isRunAlone=true的话在正常情况下是编译不过的。秘密就在于com.dd.comgradle会自动识别当前要调试的具体是哪个组件，然后把其他组件默默的修改为library工程，这个修改只在当次编译生效。
      如何判断当前要运行的是app还是哪个组件呢？这个是通过task来判断的，判断的规则如下：
  - assembleRelease → app
-  app:assembleRelease或者 :app:assembleRelease → app
- sharecomponent:assembleRelease 或者:sharecomponent:assembleRelease→ sharecomponent

上面的内容要实现的目的就是每个组件可以直接在Androidstudio中run，也可以使用命令进行打包，这期间不需要修改任何配置，却可以自动引入依赖的组件。这在开发中可以极大加快工作效率。
### 5 代码边界
至于依赖的组件是如何集成到host中的，其本质还是直接使用compile project（...）或者compile modulePackage:module@aar。那么为啥不直接在build.gradle中直接引入呢，而要经过com.dd.comgradle这个插件来进行诸多复杂的操作？原因在第一篇文章中也讲到了，那就是组件之间的完全隔离，也可以称之为代码边界。如果我们直接compile组件，那么组件的所有实现类就完全暴露出来了，使用方就可以直接引入实现类来编程，从而绕过了面向接口编程的约束。这样就完全失去了解耦的效果了，可谓前功尽弃。
那么如何解决这个问题呢？我们的解决方式还是从分析task入手，只有在assemble任务的时候才进行compile引入。这样在代码的开发期间，组件是完全不可见的，因此就杜绝了犯错误的机会。具体的代码如下：
```
  /**
 * 自动添加依赖，只在运行assemble任务的才会添加依赖，因此在开发期间组件之间是完全感知不到的，这是做到完全隔离的关键
 * 支持两种语法：module或者modulePackage:module,前者之间引用module工程，后者使用componentrelease中已经发布的aar
 * @param assembleTask
 * @param project
 */
private void compileComponents(AssembleTask assembleTask, Project project) {
    String components;
    if (assembleTask.isDebug) {
        components = (String) project.properties.get("debugComponent")
    } else {
        components = (String) project.properties.get("compileComponent")
    }
    if (components == null || components.length() == 0) {
        return;
    }
    String[] compileComponents = components.split(",")
    if (compileComponents == null || compileComponents.length == 0) {
        return;
    }
    for (String str : compileComponents) {
        if (str.contains(":")) {
            File file = project.file("../componentrelease/" + str.split(":")[1] + "-release.aar")
            if (file.exists()) {
                project.dependencies.add("compile", str + "-release@aar")
            } else {
                throw new RuntimeException(str + " not found ! maybe you should generate a new one ")
            }
        } else {
            project.dependencies.add("compile", project.project(':' + str))
        }
    }
}
```
### 6 生命周期
在上一篇文章中我们就讲过，组件化和插件化的唯一区别是组件化不能动态的添加和修改组件，但是对于已经参与编译的组件是可以动态的加载和卸载的，甚至是降维的。
首先我们看组件的加载，使用章节5中的集成调试，可以在打包的时候把依赖的组件参与编译，此时你反编译apk的代码会看到各个组件的代码和资源都已经包含在包里面。但是由于每个组件的唯一入口ApplicationLike还没有执行oncreate()方法，所以组件并没有把自己的服务注册到中央路由，因此组件实际上是不可达的。
在什么时机加载组件以及如何加载组件？目前com.dd.comgradle提供了两种方式，字节码插入和反射调用。
-  字节码插入模式是在dex生成之前，扫描所有的ApplicationLike类（其有一个共同的父类），然后通过javassisit在主项目的Application.onCreate()中插入调用ApplicationLike.onCreate()的代码。这样就相当于每个组件在application启动的时候就加载起来了。
 -  反射调用的方式是手动在Application.onCreate()中或者在其他合适的时机手动通过反射的方式来调用ApplicationLike.onCreate()。之所以提供这种方式原因有两个：对代码进行扫描和插入会增加编译的时间，特别在debug的时候会影响效率，并且这种模式对Instant Run支持不好；另一个原因是可以更灵活的控制加载或者卸载时机。
      这两种模式的配置是通过配置com.dd.comgradle的Extension来实现的，下面是字节码插入的模式下的配置格式，添加applicatonName的目的是加快定位Application的速度。
```
combuild {
    applicatonName = 'com.mrzhang.component.application.AppApplication'
    isRegisterCompoAuto = true
}
```
demo中也给出了通过反射来加载和卸载组件的实例，在APP的首页有两个按钮，一个是加载分享组件，另一个是卸载分享组件，在运行时可以任意的点击按钮从而加载或卸载组件，具体效果大家可以运行demo查看。

![加载和卸载示例.png](http://upload-images.jianshu.io/upload_images/6650461-7094079541c8a011.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
