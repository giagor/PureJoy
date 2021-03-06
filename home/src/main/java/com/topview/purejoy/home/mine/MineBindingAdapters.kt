package com.topview.purejoy.home.mine

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.topview.purejoy.common.entity.User
import com.topview.purejoy.common.widget.compose.RoundedCornerImageView
import com.topview.purejoy.home.R

@BindingAdapter("userPortrait")
fun setUserPortrait(riv: RoundedCornerImageView, user: User?) {
    if (user?.avatarUrl != null) {
        riv.loadImageRequest = user.avatarUrl
    } else {
        val defaultUserPortrait = R.drawable.home_img_default_user_portrait
        riv.loadImageRequest = defaultUserPortrait
    }
}

@BindingAdapter("userNickName")
fun setUserNickname(tv: TextView, user: User?) {
    if (user != null) {
        tv.text = user.nickname
    } else {
        val defaultUserNickname = tv.context.getString(R.string.home_mine_nickname_tips)
        tv.text = defaultUserNickname
    }
}

@BindingAdapter("loginTips")
fun setLoginTips(tv: TextView, user: User?) {
    if (user != null) {
        tv.text = tv.context.getString(R.string.home_mine_switch_account)
    } else {
        tv.text = tv.context.getString(R.string.home_mine_go_login)
    }
}

@BindingAdapter("logoutTips")
fun logoutTips(tv: TextView, user: User?){
    if (user != null) {
        tv.visibility = View.VISIBLE
    } else {
        tv.visibility = View.GONE
    }
}

