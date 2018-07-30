package com.luojilab.componentdemo.msg;

import org.github.jimu.msg.MsgBridgeService;
import org.github.jimu.msg.core.MessageBridgeService;

//@MsgBridgeService(workProcessName = "com.luojilab.componentdemo.application")
// empty can represent the default process
@MsgBridgeService(workProcessName = "")
public class MainProcessMsgService extends MessageBridgeService {
    public MainProcessMsgService() {
    }

}
