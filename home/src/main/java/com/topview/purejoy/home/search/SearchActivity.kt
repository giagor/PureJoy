package com.topview.purejoy.home.search

import android.os.Bundle
import androidx.appcompat.widget.SearchView
import com.topview.purejoy.common.base.CommonActivity
import com.topview.purejoy.home.R

class SearchActivity : CommonActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val searchView: SearchView = findViewById(R.id.search_view)
        searchView.onActionViewExpanded()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_home_search
    }
}