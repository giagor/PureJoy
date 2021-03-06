// IPCPlayerController.aidl
package com.topview.purejoy.common;

// 音乐播放器的控制接口
interface IPCPlayerController {
    void last();
    void next();
    void playOrPause();
    int duration();
    void seekTo(int progress);
    boolean isPlaying();
    int progress();
    void jumpTo(int index);
}