package com.topview.purejoy.video

import com.topview.purejoy.video.util.ProgressUtil
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * 针对ProgressUtil的单元测试
 */
class ProgressTextUnitTest {
    @Test
    fun zeroTextCorrect() {
        assertEquals("00:00", ProgressUtil.toTimeText(0L))
        assertEquals("00:00", ProgressUtil.toTimeText(0F))
    }

    @Test
    fun fullTextCorrect() {
        assertEquals("01:00", ProgressUtil.toTimeText(1000L * 60))
        assertEquals("01:59", ProgressUtil.toTimeText(1000L * 119 + 999L))
        assertEquals("9999:00", ProgressUtil.toTimeText(1000L * 60 * 9999))
    }

    @Test
    fun kbBytesTextCorrect() {
        assertEquals("999KB", ProgressUtil.toTrafficBytes(999 * 1024))
        assertEquals("9KB", ProgressUtil.toTrafficBytes(9 * 1024))
        assertEquals("0KB", ProgressUtil.toTrafficBytes(1))
        assertEquals("0.01KB", ProgressUtil.toTrafficBytes(10))
        assertEquals("1.25KB", ProgressUtil.toTrafficBytes(5 * 256))
    }

    @Test
    fun mbBytesTextCorrect() {
        assertEquals("1023MB", ProgressUtil.toTrafficBytes(1023 * 1024 * 1024))
        assertEquals("1.25MB", ProgressUtil.toTrafficBytes(5 * 1024 * 256))
        assertEquals("10MB", ProgressUtil.toTrafficBytes(10 * 1024 * 1024))
        assertEquals("2.5MB", ProgressUtil.toTrafficBytes(5 * 1024 * 512))
    }
}