package com.topview.purejoy.home.search

import android.os.Bundle
import androidx.appcompat.widget.SearchView
import com.topview.purejoy.common.base.CommonActivity
import com.topview.purejoy.home.R
import com.topview.purejoy.home.search.content.SearchContentTabFragment

class SearchActivity : CommonActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val searchView: SearchView = findViewById(R.id.search_view)
        searchView.onActionViewExpanded()
        addFragment(R.id.home_fl_fragment_layout, SearchContentTabFragment.newInstance())
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_home_search
    }
}