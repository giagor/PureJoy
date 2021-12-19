// IPCDataController.aidl
package com.topview.purejoy.common;
import com.topview.purejoy.common.music.data.Wrapper;

// 操作音乐数据源的接口
interface IPCDataController {
    void add(in Wrapper wrapper);
    void addAfter(in Wrapper wrapper, int index);
    void remove(in Wrapper wrapper);
    void addAll(in List<Wrapper> wrapper);
    void clear();
    List<Wrapper> allItems();
    Wrapper current();
}