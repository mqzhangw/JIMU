package org.github.jimu.msg.bean;

import android.support.annotation.Nullable;

import org.github.jimu.msg.AriseAt;
import org.github.jimu.msg.notation.From;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import osp.leobert.android.reportprinter.notation.ChangeLog;

/**
 * <p><b>Package:</b> org.github.jimu.msg.bean </p>
 * <p><b>Project:</b> jimu-core </p>
 * <p><b>Classname:</b> ManagerMethod </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2018/7/14.
 */
@ChangeLog(version = "1.3.3", changes = {
        "add bean to describe method in component event manager"
})
public class ManagerMethod {
    private static final Map<Method, ManagerMethod> methodCache = new ConcurrentHashMap<>();

    private AriseAt ariseAt;

    public static ManagerMethod parse(Method method) {

        ManagerMethod result = methodCache.get(method);
        if (result != null) return result;

        synchronized (methodCache) {
            result = methodCache.get(method);
            if (result == null) {
                result = Parser.parse(method);
                methodCache.put(method, result);
            }
        }
        return result;
    }

    public void invoke(Object[] args) {


    }

    private static final class Parser {
        static ManagerMethod parse(Method method) {
            Parser parser = new Parser(method,
                    method.getParameterAnnotations(),
                    method.getGenericParameterTypes());
            return null;
        }

        final Method method;
        final Annotation[][] parameterAnnotationsArray;
        final Type[] parameterTypes;

        ManagerMethod managerMethod;

        private Parser(Method method,
                       Annotation[][] parameterAnnotationsArray,
                       Type[] parameterTypes) {
            managerMethod = new ManagerMethod();
            this.method = method;
            this.parameterAnnotationsArray = parameterAnnotationsArray;
            this.parameterTypes = parameterTypes;
        }

        private void parse() {
            int pCount = parameterAnnotationsArray.length;

            From from = method.getAnnotation(From.class);
            if (from == null || from.local()) {
                managerMethod.ariseAt = AriseAt.local();
            } else
                managerMethod.ariseAt = AriseAt.remote(from.pa());


            for (int i = 0; i < pCount; i++) {
                Annotation[] paramAnnotation = parameterAnnotationsArray[i];

                if (paramAnnotation == null || paramAnnotation.length == 0)
                    throw error(method, null, "missing notation for (parameter #" + (1 + 1) + ")");
            }
        }

        static RuntimeException error(Method method, @Nullable Throwable cause, String message,
                                      Object... args) {
            message = String.format(message, args);
            return new IllegalArgumentException(message
                    + "\n    for method "
                    + method.getDeclaringClass().getSimpleName()
                    + "."
                    + method.getName(), cause);
        }
    }
}
