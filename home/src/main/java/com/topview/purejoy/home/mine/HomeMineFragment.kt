package com.topview.purejoy.home.mine

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View

import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.topview.purejoy.common.app.CommonApplication
import com.topview.purejoy.common.base.binding.BindingFragment
import com.topview.purejoy.common.entity.User
import com.topview.purejoy.common.music.service.MusicService
import com.topview.purejoy.common.music.util.ExecutorInstance
import com.topview.purejoy.common.router.CommonRouter
import com.topview.purejoy.common.util.UserManager
import com.topview.purejoy.common.util.clear
import com.topview.purejoy.common.util.showToast
import com.topview.purejoy.home.R
import com.topview.purejoy.home.databinding.FragmentHomeMineBinding
import com.topview.purejoy.home.router.HomeRouter
import java.io.File

import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.facade.annotation.Route
import com.topview.purejoy.common.mvvm.fragment.MVVMFragment
import com.topview.purejoy.common.router.CommonRouter
import com.topview.purejoy.home.R
import com.topview.purejoy.home.databinding.FragmentHomeMineBinding
import com.topview.purejoy.home.router.HomeRouter
import com.topview.purejoy.home.util.getAndroidViewModelFactory


@Route(path = HomeRouter.FRAGMENT_HOME_MINE)
class HomeMineFragment : MVVMFragment<HomeMineViewModel, FragmentHomeMineBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        initEvent()
    }

    private val tipDialog: AlertDialog by lazy {
        val builder = AlertDialog.Builder(this.requireContext())
        builder.setMessage(R.string.home_clear_cache_msg).setPositiveButton(R.string.ensure) { dialog, _ ->
            dialog.dismiss()
            if (!progressDialog.isShowing) {
                clearCache()
                progressDialog.show()
            }
        }.setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }
        builder.create()
    }

    private val progressDialog: AlertDialog by lazy {
        val builder = AlertDialog.Builder(this.requireContext())
        builder.setCancelable(false).setView(R.layout.simple_dialog)
        builder.create()
    }

    private val handler: Handler = Handler(Looper.getMainLooper())

    override fun getLayoutId(): Int = R.layout.fragment_home_mine

    private fun initEvent() {
        binding.tvLoginTipsClickListener = View.OnClickListener {
            HomeRouter.routeToLoginActivity()
        }

        binding.goDownloadManageClickListener = View.OnClickListener {
            CommonRouter.routeToDownloadManageActivity()
        }

        binding.aboutPageClickListener = View.OnClickListener {
            HomeRouter.routeToAboutActivity()
        }

        binding.clearCacheClickListener = View.OnClickListener {
            if (!tipDialog.isShowing) {
                tipDialog.show()
            }
        }

    }

    @ExperimentalCoilApi
    private fun clearCache() {
        ExecutorInstance.getInstance().execute {
            var size = ExecutorInstance.getInstance().submit {
                clear(File(requireContext().cacheDir, MusicService.MUSIC_CACHE_DIR)) { f ->
                    false
                }
            }.get()
            val gf = Glide.getPhotoCacheDir(requireContext())
            gf?.let {
                size += ExecutorInstance.getInstance().submit {
                    clear(it) { f ->
                        false
                    }
                }.get()
            }
            val cf = ImageLoader(requireContext()).diskCache
            cf?.let {
                size += ExecutorInstance.getInstance().submit {
                    clear(it.directory) { f ->
                        false
                    }
                }.get()
            }
            handler.post {
                if (progressDialog.isShowing) {
                    progressDialog.dismiss()
                }
                showToast(this.requireContext(), "清理完成," +
                        "释放空间${String.format("%.2f", size * 1.0 / 1024 / 1024)}MB", Toast.LENGTH_SHORT)
            }
        }
    }

    override fun onDestroy() {
        if (tipDialog.isShowing) {
            tipDialog.dismiss()
        }
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    override fun createFactory(): ViewModelProvider.Factory = getAndroidViewModelFactory()

    override fun getViewModelClass(): Class<HomeMineViewModel> {
        return HomeMineViewModel::class.java
    }
}