package org.github.jimu.msg

import javassist.CtClass

class ServiceInfoBean {
    String fullProcessName
    CtClass serviceClass

    ServiceInfoBean(String fullProcessName, CtClass serviceClass) {
        this.fullProcessName = fullProcessName
        this.serviceClass = serviceClass
    }

    @Override
    String toString() {
        return "ServiceInfoBean{" +
                "fullProcessName='" + fullProcessName + '\'' +
                ", serviceClass=" + serviceClass +
                '}'
    }
}