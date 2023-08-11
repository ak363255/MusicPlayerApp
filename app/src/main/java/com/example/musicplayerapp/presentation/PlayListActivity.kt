package com.example.musicplayerapp.presentation

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.musicplayerapp.R
import com.example.musicplayerapp.databinding.ActivityPlayListBinding
import com.example.musicplayerapp.databinding.CreatePlaylistLayoutBinding
import com.example.recipecore.domain.model.PlayListModel
import com.example.recipecore.domain.utils.Utility
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import io.grpc.okhttp.internal.Util


class PlayListActivity : BasePlayerActivity() {
    lateinit var binding: ActivityPlayListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_play_list)
        setContentView(binding.root)
        hideToolbar()
        loadSongsFromExternalStroage()
        initUi()
    }

    private fun initUi() {
        addFragmentWithoutBackStack(PlayListFragment.getInstance())
    }

    fun showSnackBar(view: View, message:String, color:Int, positiveBtnText:String, positiveBtnCallback:()->Unit){
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        snackbar.setAction(positiveBtnText,
            View.OnClickListener {
                positiveBtnCallback.invoke()
                snackbar.dismiss()
            })
        snackbar.setDuration(3000)
        val group = snackbar.getView()
        val textView = group.findViewById<TextView>(com.google.android.material.R.id.snackbar_action)
        textView.setTextColor(color)
        group.setBackgroundColor(Color.parseColor("#303030"))
        val txtView = group.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        txtView.maxLines = 5
        snackbar.show()
    }

     fun addFragmentWithoutBackStack(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .add(binding.mainContent.id, fragment, fragment::class.java.simpleName)
            .commitAllowingStateLoss()
    }

     fun addFragmentWithBackStack(frag:Fragment){
        supportFragmentManager.beginTransaction()
            .add(binding.mainContent.id,frag,frag::class.java.simpleName)
            .addToBackStack(frag::class.java.simpleName)
            .commitAllowingStateLoss()
    }
}