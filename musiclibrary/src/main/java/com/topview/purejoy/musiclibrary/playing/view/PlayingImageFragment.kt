package com.topview.purejoy.musiclibrary.playing.view

import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.fragment.app.activityViewModels
import com.topview.purejoy.common.base.CommonFragment
import com.topview.purejoy.common.widget.RoundImageView
import com.topview.purejoy.musiclibrary.R
import com.topview.purejoy.musiclibrary.common.util.loadBitmap
import com.topview.purejoy.musiclibrary.playing.viewmodel.PlayingViewModel

class PlayingImageFragment : CommonFragment() {
    private val viewModel: PlayingViewModel by activityViewModels()
    private lateinit var imageView: RoundImageView
    override fun getLayoutId(): Int {
        return R.layout.fragment_music_playing_image
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        imageView = view.findViewById(R.id.music_playing_round_img)
        view.findViewById<RelativeLayout>(R.id.playing_image_layout).setOnClickListener {
            val manager = requireActivity().supportFragmentManager
            manager.beginTransaction().hide(this)
                .show(manager.findFragmentByTag(PlayingLrcFragment::class.java.simpleName)!!).commit()
        }
        viewModel.currentItem.observe(this.viewLifecycleOwner) {
            loadBitmap(imageView, url = it?.al?.picUrl)
        }
        viewModel.playState.observe(this.viewLifecycleOwner) {
            if (it) {
                imageView.startAnimator(duration = 10000)
            } else {
                imageView.pauseAnimator()
            }
        }

    }

    override fun onDestroyView() {
        imageView.cancelAnimator()
        super.onDestroyView()
    }
}