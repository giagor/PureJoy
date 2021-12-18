// IPCListenerController.aidl
package com.topview.purejoy.common;

import com.topview.purejoy.common.IPCDataSetChangeListener;
import com.topview.purejoy.common.IPCItemChangeListener;
import com.topview.purejoy.common.IPCModeChangeListener;
import com.topview.purejoy.common.IPCPlayStateChangeListener;

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