// IPCItemChangeListener.aidl
package com.topview.purejoy.musiclibrary;

import com.topview.purejoy.musiclibrary.data.Wrapper;

// 播放歌曲变化时的回调
interface IPCItemChangeListener {
    void onItemChange(in Wrapper wrapper);
}