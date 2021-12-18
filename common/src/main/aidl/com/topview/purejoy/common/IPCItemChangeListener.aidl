// IPCItemChangeListener.aidl
package com.topview.purejoy.common;
import com.topview.purejoy.common.music.data.Wrapper;

// 播放歌曲变化时的回调
interface IPCItemChangeListener {
    void onItemChange(in Wrapper wrapper);
}