package com.qlcd.loggertools.widget.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.ScreenUtils
import com.qlcd.loggertools.R
import com.qlcd.loggertools.ext.dpToPx

class BaseDialog(
    var viewConverter: DialogViewConverter? = null,
    var dismissListener: DialogInterface.OnDismissListener? = null
) : DialogFragment() {

    companion object {
        private const val VIEW_CONVERTER = "view_converter"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.BaseDialogStyle)

        savedInstanceState?.let {
            viewConverter = it.getParcelable(VIEW_CONVERTER)
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_clean, container, false)
        viewConverter?.convertView(view, this)
        return view
    }

    override fun onStart() {
        super.onStart()
        initParams()
    }

    /**
     * 屏幕旋转等导致DialogFragment销毁后重建时保存数据
     *
     * @param outState
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(VIEW_CONVERTER, viewConverter)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissListener?.onDismiss(dialog)
    }

    private fun initParams() {
        dialog?.window?.let {
            val lp = it.attributes
            // 调节灰色背景透明度[0-1]，默认0.5
            lp.dimAmount = 0.5f
            // 设置dialog方位和对应进出动画
            lp.gravity = Gravity.CENTER
            // 设置dialog宽度
            lp.width = ScreenUtils.getScreenWidth() - 2 * 38.dpToPx.toInt()

            // 设置dialog高度
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.attributes = lp
        }
    }

    fun show(manager: FragmentManager): BaseDialog {
        val ft = manager.beginTransaction()
        if (isAdded) {
            ft.remove(this).commit()
        }
        ft.add(this, System.currentTimeMillis().toString())
        ft.commitNowAllowingStateLoss()
        return this
    }
}