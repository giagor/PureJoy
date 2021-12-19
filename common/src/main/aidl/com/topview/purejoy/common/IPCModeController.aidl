// IPCModeController.aidl
package com.topview.purejoy.common;

// Declare any non-default types here with import statements
// 播放模式控制器
interface IPCModeController {
    void nextMode();
    int currentMode();
}