package com.topview.purejoy.home.search

import android.os.Bundle
import androidx.appcompat.widget.SearchView
import com.topview.purejoy.common.base.binding.BindingActivity
import com.topview.purejoy.home.R
import com.topview.purejoy.home.databinding.ActivityHomeSearchBinding
import com.topview.purejoy.home.databinding.HomeActivityHomeBinding
import com.topview.purejoy.home.search.content.tab.SearchContentTabFragment

class SearchActivity : BindingActivity<ActivityHomeSearchBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val searchView: SearchView = binding.homeSvBox
        searchView.onActionViewExpanded()
        addFragment(R.id.home_fl_fragment_layout, SearchContentTabFragment.newInstance())
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_home_search
    }
}