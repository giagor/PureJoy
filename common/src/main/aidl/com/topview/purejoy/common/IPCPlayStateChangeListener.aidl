// IPCPlayStateChangeListener.aidl
package com.topview.purejoy.common;

// 播放状态变化时的回调
interface IPCPlayStateChangeListener {
    void playStateChange(boolean state);
}