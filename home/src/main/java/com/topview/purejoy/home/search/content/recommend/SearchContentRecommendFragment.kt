package com.topview.purejoy.home.search.content.recommend

import com.topview.purejoy.common.base.binding.BindingFragment
import com.topview.purejoy.home.R
import com.topview.purejoy.home.databinding.FragmentHomeSearchContentRecommendBinding

class SearchContentRecommendFragment :
    BindingFragment<FragmentHomeSearchContentRecommendBinding>() {

    override fun getLayoutId(): Int = R.layout.fragment_home_search_content_recommend

    companion object {
        @JvmStatic
        fun newInstance() =
            SearchContentRecommendFragment()
    }
}