package com.topview.purejoy.home.search.content.recommend

import com.topview.purejoy.common.base.binding.BindingFragment
import com.topview.purejoy.home.R
import com.topview.purejoy.home.databinding.HomeFragmentSearchContentRecommendBinding

class SearchContentRecommendFragment :
    BindingFragment<HomeFragmentSearchContentRecommendBinding>() {

    override fun getLayoutId(): Int = R.layout.home_fragment_search_content_recommend

    companion object {
        @JvmStatic
        fun newInstance() =
            SearchContentRecommendFragment()
    }
}