package org.github.jimu.msg.bean;


import org.github.jimu.msg.ConsumeOn;
import org.github.jimu.msg.EventListener;
import org.github.jimu.msg.Utils;

/**
 * <p><b>Package:</b> org.github.jimu.msg.bean </p>
 * <p><b>Project:</b> jimu-core </p>
 * <p><b>Classname:</b> ConsumerMeta </p>
 * <p><b>Description:</b> mate info of the consumer </p>
 * Created by leobert on 2018/7/30.
 */
public final class ConsumerMeta<T extends EventBean> {
    private ConsumeOn consumeOn;
    private EventListener<T> eventListener;
    private String process;

    public ConsumeOn getConsumeOn() {
        return consumeOn;
    }

    public EventListener<T> getEventListener() {
        return eventListener;
    }

    public String getProcess() {
        return process;
    }

    private ConsumerMeta(Builder<T> builder) {
        consumeOn = builder.consumeOn;
        eventListener = builder.eventListener;
        process = builder.process;
        Utils.checkNotNull(eventListener, "eventListener cannot be null," +
                "call ConsumerMate.Builder.eventListener before build");

        Utils.checkNotNull(process, "process cannot be null, you can use empty string for the default main process");
    }


    public static <T extends EventBean> Builder<T> newBuilder() {
        return new Builder<T>();
    }


    public static final class Builder<T extends EventBean> {
        private ConsumeOn consumeOn = ConsumeOn.Main;
        private EventListener<T> eventListener;
        /**
         * "" mean the default process when application is created
         */
        private String process = "";

        private Builder() {
        }

        public Builder<T> consumeOn(ConsumeOn val) {
            consumeOn = val;
            return this;
        }


        public Builder<T> process(String val) {
            process = val;
            return this;
        }

        public Builder<T> currentProcess() {
            process = Utils.getProcessName();
            return this;
        }

        public Builder<T> eventListener(EventListener<T> val) {
            eventListener = val;
            return this;
        }

        public ConsumerMeta<T> build() {
            return new ConsumerMeta<>(this);
        }
    }
}
