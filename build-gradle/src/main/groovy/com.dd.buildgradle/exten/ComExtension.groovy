package com.dd.buildgradle.exten

class ComExtension {

    /**
     * 是否自动注册组件，true则会使用字节码插入的方式自动注册代码
     * false的话，需要手动使用反射的方式来注册
     */
    boolean isRegisterCompoAuto = false

    /**
     * 当前组件的applicationName，用于字节码插入。
     * 当isRegisterCompoAuto==true的时候是必须的
     */
    String applicationName

    /**
     * 是否使用自动转换.api为.java文件，开启后需要会自动生成componentservice中的代码
     * 注意开启后每次都会删除后再生成
     */
    boolean isNeedApiToJava = false

}