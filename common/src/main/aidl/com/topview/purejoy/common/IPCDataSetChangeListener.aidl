// IPCDataSetChangeListener.aidl
package com.topview.purejoy.common;
import com.topview.purejoy.common.music.data.Wrapper;

// 操作数据源的回调
interface IPCDataSetChangeListener {
    // 回调添加、清除的数据，当清除所有数据时，参数为空列表
    void onChange(in List<Wrapper> source);
}