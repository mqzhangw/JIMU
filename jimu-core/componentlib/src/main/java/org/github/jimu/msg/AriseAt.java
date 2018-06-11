package org.github.jimu.msg;

import com.luojilab.component.componentlib.log.ILogger;

/**
 * <p><b>Package:</b> org.github.jimu.msg.bean </p>
 * <p><b>Project:</b> JIMU </p>
 * <p><b>Classname:</b> EventType </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2018/4/25.
 */
public class AriseAt {

    private String processFullName;
    private boolean isLocal;

    private AriseAt(String processFullName, boolean isLocal) {
        this.processFullName = processFullName;
        this.isLocal = isLocal;
    }

    public String getProcessFullName() {
        return processFullName;
    }

    public static AriseAt local() {
        return new AriseAt(Utils.getProcessName(), true);
    }

    public static AriseAt remote(String processFullName) {
        return new AriseAt(processFullName, false);
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void log() {
        ILogger.logger.monitor("AriseAt:{ processFullName:" + processFullName + "}");
    }
}
