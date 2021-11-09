// IPCDataController.aidl
package com.topview.purejoy.musiclibrary;
import com.topview.purejoy.musiclibrary.data.Wrapper;

// 操作音乐数据源的接口
interface IPCDataController {
    void add(in Wrapper wrapper);
    void remove(in Wrapper wrapper);
    void addAll(in List<Wrapper> wrapper);
    void clear();
    List<Wrapper> allItems();
    Wrapper current();
}