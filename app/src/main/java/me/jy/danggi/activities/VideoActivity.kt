package me.jy.danggi.activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import io.realm.Realm
import io.realm.Sort
import me.jy.danggi.BR
import me.jy.danggi.R
import me.jy.danggi.activities.adapter.VideoAdapter
import me.jy.danggi.database.DataHelper
import me.jy.danggi.databinding.ActivityVideoBinding
import me.jy.danggi.model.Video

class VideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoBinding
    private lateinit var realm: Realm
    private var adapter:VideoAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video)
        binding.setVariable(BR.activity, this)

        realm = Realm.getDefaultInstance()

        initToolbar()
        initRecyclerView()
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbarVideo)
    }

    private fun initRecyclerView() {
        adapter = VideoAdapter()

        adapter?.clickSubject?.subscribe { data ->
            goToWriteVideo(data)
        }
        adapter?.longClickSubject?.subscribe { data ->
            createDialog(data).show()
        }
        binding.recyclerviewVideo.layoutManager = LinearLayoutManager(this)
        binding.recyclerviewVideo.adapter = adapter
        binding.recyclerviewVideo.setHasFixedSize(true)

        adapter?.updateItemList(realm.where(Video::class.java).sort("writeDate", Sort.DESCENDING).findAll())
    }

    private fun goToWriteVideo(item:Video) {
        val intent = Intent(this, WriteVideoActivity::class.java)
        intent.putExtra("itemId", item.id)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    private fun createDialog(item: Video): Dialog { //다이얼로그 생성
        val builder = AlertDialog.Builder(this)
        return builder.setTitle(item.content.substring(0, item.content.length/2))
                .setItems(R.array.menus) { _, which ->
                    when (which) {
                        0 -> DataHelper.deleteVideo(realm, item.id)
                        1 -> shareItem(item)
                    }
                }.create()
    }


    private fun shareItem(item:Video) {
        val sendIntent= Intent(Intent.ACTION_SEND)
        sendIntent.putExtra(Intent.EXTRA_STREAM,  Uri.parse(item.uri))
        sendIntent.type = "video/*"
        startActivity(Intent.createChooser(sendIntent, getString(R.string.menu_share)))
    }

    fun onFabBtnClick(v: View) { //플로팅버튼클릭
        startActivity(Intent(applicationContext, WriteVideoActivity::class.java))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_text -> {
                startActivity(Intent(this, MainActivity::class.java))
                return true
            }
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
        adapter=null
    }
}
