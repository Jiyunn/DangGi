package me.jy.danggi.video

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import io.reactivex.disposables.CompositeDisposable
import io.realm.Realm
import io.realm.Sort
import me.jy.danggi.BR
import me.jy.danggi.R
import me.jy.danggi.common.rx.RxBus
import me.jy.danggi.data.DataHelper
import me.jy.danggi.data.Video
import me.jy.danggi.databinding.ActivityVideoBinding
import me.jy.danggi.text.TextActivity
import me.jy.danggi.video.adapter.VideoAdapter

class VideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoBinding

    private val realm = Realm.getDefaultInstance()
    private var adapter: VideoAdapter? = null

    private val rxBus = RxBus.getInstance()
    private var disposables = CompositeDisposable()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_video)
        binding.setVariable(BR.activity, this)

        initToolbar()
        initRecyclerView()
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbarVideo?.toolbarList)
        supportActionBar?.let {
            title = getString(R.string.title_video)
        }
    }

    private fun initRecyclerView() {
        adapter = VideoAdapter(
                realm.where(Video::class.java).sort("writeDate", Sort.DESCENDING).findAll(), true).apply {

            disposables.add(clickSubject.subscribe { data ->
                goToWriteVideo(data)
            })
            disposables.add(longClickSubject.subscribe { data ->
                createDialog(data).show()
            })
        }

        binding.recyclerVideo.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@VideoActivity)
            adapter = this@VideoActivity.adapter
        }
    }

    private fun goToWriteVideo(item: Video) {
        rxBus.takeBus(item)

        Intent(this, WriteVideoActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(this)
        }
    }

    private fun createDialog(item: Video): Dialog { //다이얼로그 생성
        val builder = AlertDialog.Builder(this)

        return builder.setTitle(getString(R.string.ask_choose))
                .setItems(R.array.menus) { _, which ->
                    when (which) {
                        0 -> DataHelper.deleteVideo(realm, item.id)
                        1 -> shareItem(item)
                    }
                }.create()
    }


    private fun shareItem(item: Video) {
        Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, Uri.parse(item.uri))
            type = "video/*"
            startActivity(Intent.createChooser(this, getString(R.string.menu_share)))
        }
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
                Intent(this, TextActivity::class.java).apply {
                    startActivity(this)
                }
                return true
            }
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()

        realm.close()
        adapter = null
        disposables.dispose()
    }
}
