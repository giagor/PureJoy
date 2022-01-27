package com.topview.purejoy.home.search

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.facade.annotation.Route
import com.topview.purejoy.common.base.binding.BindingActivity
import com.topview.purejoy.common.music.data.Wrapper
import com.topview.purejoy.common.music.view.bottom.MusicBottomView
import com.topview.purejoy.home.R
import com.topview.purejoy.home.databinding.ActivityHomeSearchBinding
import com.topview.purejoy.home.router.HomeRouter.ACTIVITY_HOME_SEARCH
import com.topview.purejoy.home.search.content.recommend.SearchContentRecommendFragment
import com.topview.purejoy.home.search.content.song.SearchContentSongFragment
import com.topview.purejoy.home.search.tab.SearchContentTabFragment

@Route(path = ACTIVITY_HOME_SEARCH)
class SearchActivity : BindingActivity<ActivityHomeSearchBinding>(),
    SearchKeywordListener, SearchContentSongFragment.SearchSongPlayListener {

    private val bottomMusicBar: MusicBottomView by lazy {
        MusicBottomView(this)
    }

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
        initEvent()
        observe()
        addFragment(R.id.home_fl_fragment_layout, SearchContentRecommendFragment.newInstance())

        bottomMusicBar.addMusicBottomBar()
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
                    SearchContentTabFragment.newInstance().apply {
                        setSearchSongPlayListener(this@SearchActivity)
                    }
                )
            }
        })
    }

    private fun initEvent() {
        binding.onQueryTextListener = onQueryTextListener
        binding.backClickListener = View.OnClickListener {
            finish()
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_home_search

    override fun getKeywordLiveData(): LiveData<String> = keywordLiveData

    override fun onSearchSongItemClick(position: Int, list: List<Wrapper>) {
        bottomMusicBar.controller.dataController?.clear()
        bottomMusicBar.controller.dataController?.addAll(list)
        bottomMusicBar.controller.playerController?.jumpTo(position)
    }

    override fun searchSongNextPlay(wrapper: Wrapper) {
        bottomMusicBar.controller.dataController?.let { dataController ->
            val datas = dataController.allItems()
            datas?.let { queueSongs ->
                if (queueSongs.isEmpty()) {
                    dataController.add(wrapper)
                    bottomMusicBar.controller.playerController?.jumpTo(0)
                } else {
                    val position = datas.indexOf(dataController.current())
                    dataController.addAfter(wrapper, position)
                }
            }
        }
    }
}
