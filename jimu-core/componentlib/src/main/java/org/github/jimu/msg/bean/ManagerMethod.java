package org.github.jimu.msg.bean;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.github.jimu.msg.AriseAt;
import org.github.jimu.msg.EventManager;
import org.github.jimu.msg.notation.AriseProcess;
import org.github.jimu.msg.notation.Consumer;
import org.github.jimu.msg.notation.Event;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import osp.leobert.android.reportprinter.notation.Bug;
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

    @NonNull
    private String ariseProcess = "";
    Class<? extends EventBean> eventClz;
    private int indexOfConsumerMate = -1;

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


    @SuppressWarnings("unchecked")
    @Bug(desc = "check the mapping of the remote bridge,to see if \"\" can map to the default process")
    public void invoke(Object[] args) {

        try {
            ConsumerMeta meta = (ConsumerMeta) args[indexOfConsumerMate];
            String consumerProcess = meta.getProcess();
            AriseAt ariseAt;
            if (ariseProcess.equals(consumerProcess)) {
                ariseAt = AriseAt.local();
            } else {
                ariseAt = AriseAt.remote(ariseProcess);
            }

            EventManager.getInstance().subscribe(eventClz, ariseAt, meta.getConsumeOn(), meta.getEventListener());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static final class Parser {
        static ManagerMethod parse(Method method) {
            Parser parser = new Parser(method,
                    method.getParameterAnnotations(),
                    method.getGenericParameterTypes());
            return parser.parse();
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

        private ManagerMethod parse() {
            int pCount = parameterAnnotationsArray.length;

            //notated in the method,in some cases, the provider can make sure the event is emitted
            //in a specific process which differs from the subscriber's
            AriseProcess from = method.getAnnotation(AriseProcess.class);
            if (from == null) {
                managerMethod.ariseProcess = "";
            } else
                managerMethod.ariseProcess = from.pa();

            Event eventAnno = method.getAnnotation(Event.class);
            if (eventAnno == null) {
                throw error(method, null, "missing notation of Event at the method");
            }

            managerMethod.eventClz = eventAnno.clz();


            for (int i = 0; i < pCount; i++) {
                Annotation[] paramAnnotation = parameterAnnotationsArray[i];

                if (paramAnnotation == null || paramAnnotation.length == 0)
                    throw error(method, null, "missing notation for (parameter #" + (i + 1) + ")");

                parseParameter(i, paramAnnotation);
            }

            if (managerMethod.ariseProcess == null) {
                throw error(method, null, "pa in AriseProcess cannot be null");
            }
            if (managerMethod.indexOfConsumerMate < 0) {
                throw error(method, null, "missing notation of Consumer");
            }
            return managerMethod;
        }

        private void parseParameter(int i, Annotation[] paramAnnotation) {
            boolean hasFoundNotation = false;

            for (Annotation notation :
                    paramAnnotation) {


                if (notation instanceof Consumer) {
                    checkMultiNotation(i, hasFoundNotation);
                    hasFoundNotation = true;
                    if (managerMethod.indexOfConsumerMate >= 0)
                        throw error(method, null, "duplicate notation of Consumer");

                    managerMethod.indexOfConsumerMate = i;
                }
            }
        }

        private void checkMultiNotation(int i, boolean hasFoundNotation) {
            if (hasFoundNotation) {
                throw error(method, null, "cannot both notate with Event and Consumer. at param:" + (i + 1));
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
