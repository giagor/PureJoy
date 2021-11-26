package com.topview.purejoy.home.search.tab.adapter.binding

import androidx.databinding.BindingAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

@BindingAdapter("vp2Adapter")
fun setVp2Adapter(vp : ViewPager2,adapter : FragmentStateAdapter){
    vp.adapter = adapter
}