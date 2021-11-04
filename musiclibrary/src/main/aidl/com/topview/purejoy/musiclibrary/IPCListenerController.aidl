// IPCListenerController.aidl
package com.topview.purejoy.musiclibrary;

import com.topview.purejoy.musiclibrary.IPCDataSetChangeListener;
import com.topview.purejoy.musiclibrary.IPCItemChangeListener;
import com.topview.purejoy.musiclibrary.IPCModeChangeListener;
import com.topview.purejoy.musiclibrary.IPCPlayStateChangeListener;

// IPC回调管理接口
interface IPCListenerController {
    void addItemChangeListener(in IPCItemChangeListener listener);
    void addModeChangeListener(in IPCModeChangeListener listener);
    void addPlayStateChangeListener(in IPCPlayStateChangeListener listener);
    void removeItemChangeListener(in IPCItemChangeListener listener);
    void removeModeChangeListener(in IPCModeChangeListener listener);
    void removePlayStateChangeListener(in IPCPlayStateChangeListener listener);
    void addDataChangeListener(in IPCDataSetChangeListener listener);
    void removeDataChangeListener(in IPCDataSetChangeListener listener);
}