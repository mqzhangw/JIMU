package com.luojilab.componentdemo.msg;

import org.github.jimu.msg.MsgBridgeService;
import org.github.jimu.msg.core.MessageBridgeService;

@MsgBridgeService(workProcessName = ":remote")
public class RemoteMsgService extends MessageBridgeService {
    public RemoteMsgService() {
    }
}
