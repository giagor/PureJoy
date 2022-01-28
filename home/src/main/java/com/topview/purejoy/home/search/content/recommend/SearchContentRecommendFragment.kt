package com.topview.purejoy.home.search.content.recommend

import com.alibaba.android.arouter.facade.annotation.Route
import com.topview.purejoy.common.base.binding.BindingFragment
import com.topview.purejoy.home.R
import com.topview.purejoy.home.databinding.FragmentHomeSearchContentRecommendBinding
import com.topview.purejoy.home.router.HomeRouter.FRAGMENT_HOME_SEARCH_RECOMMEND

@Route(path = FRAGMENT_HOME_SEARCH_RECOMMEND)
class SearchContentRecommendFragment :
    BindingFragment<FragmentHomeSearchContentRecommendBinding>() {

    override fun getLayoutId(): Int = R.layout.fragment_home_search_content_recommend
}