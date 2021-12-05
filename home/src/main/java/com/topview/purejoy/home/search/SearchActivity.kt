package com.topview.purejoy.home.search

import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.topview.purejoy.common.base.binding.BindingActivity
import com.topview.purejoy.home.R
import com.topview.purejoy.home.databinding.ActivityHomeSearchBinding
import com.topview.purejoy.home.search.content.recommend.SearchContentRecommendFragment
import com.topview.purejoy.home.search.tab.SearchContentTabFragment

class SearchActivity : BindingActivity<ActivityHomeSearchBinding>(),
    SearchKeywordListener {

    /**
     * 存放用户搜索的关键字
     * */
    private val keywordLiveData: MutableLiveData<String> = MutableLiveData()
    private lateinit var searchView: SearchView

    val onQueryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            query?.let {
                keywordLiveData.value = it
            }
            // 用户点击搜索后，让SearchView失去焦点，这么做的目的是为了收起软键盘
            searchView.clearFocus()
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        initBinding()
        observe()
        addFragment(R.id.home_fl_fragment_layout, SearchContentRecommendFragment.newInstance())
    }

    private fun initView() {
        searchView = binding.homeSvBox.apply {
            onActionViewExpanded()
        }
    }

    private fun observe() {
        keywordLiveData.observe(this, {
            val tabFragment = findFragment(SearchContentTabFragment::class.java.simpleName)
            // 若找不到SearchContentTabFragment，则将它替换到容器中
            if (tabFragment == null) {
                replaceAndAddToBackStack(
                    R.id.home_fl_fragment_layout,
                    SearchContentTabFragment.newInstance()
                )
            }
        })
    }

    private fun initBinding() {
        binding.onQueryTextListener = onQueryTextListener
    }

    override fun getLayoutId(): Int = R.layout.activity_home_search

    override fun getKeywordLiveData(): LiveData<String> = keywordLiveData
}
