package com.dd.buildgradle.exten

class ComExtension {

    /**
     * 是否自动注册组件，true则会使用字节码插入的方式自动注册代码
     * false的话，需要手动使用反射的方式来注册.
     *
     * <em>
     *      represent the strategy used to load the sub component.
     *      if it is true in the host module(the one you are building),
     *      the plugin will try to generate auto load codes in the Application
     *      class of the host module. caution: Applike notated with
     *      {@link com.luojilab.component.componentlib.applicationlike.RegisterCompManual}
     *      won't be auto load
     * </em>
     */
    boolean isRegisterCompoAuto = false

    /**
     * 当前组件的applicationName，用于字节码插入。
     * 当isRegisterCompoAuto==true的时候是必须的
     */
    String applicationName
}