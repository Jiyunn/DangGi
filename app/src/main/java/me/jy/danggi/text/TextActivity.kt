package me.jy.danggi.text

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import io.reactivex.disposables.CompositeDisposable
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where
import me.jy.danggi.R
import me.jy.danggi.data.DataHelper
import me.jy.danggi.data.Memo
import me.jy.danggi.databinding.ActivityTextBinding
import me.jy.danggi.text.adapter.TextAdapter
import me.jy.danggi.video.VideoActivity

class TextActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTextBinding

    private val realm = Realm.getDefaultInstance()
    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_text)
        binding.activity = this

        initToolbar()
        initRecyclerView()
    }


    private fun initToolbar() {
        setSupportActionBar(binding.toolbarText?.toolbarList)
        supportActionBar?.let {
            title = getString(R.string.title_text)
        }
    }


    private fun initRecyclerView() {
        binding.recyclerText.run {
            setHasFixedSize(true)

            layoutManager = LinearLayoutManager(this@TextActivity)
            adapter = TextAdapter(realm.where<Memo>()
                    .sort(Memo::writeDate.name, Sort.DESCENDING).findAll(), true).apply {

                disposables.addAll(clickSubject.subscribe {
                    goToWriteToEdit(it)
                }, longClickSubject.subscribe {
                    createDialog(it).show()
                })
            }
        }
    }

    private fun createDialog(item: Memo) =
            AlertDialog.Builder(this)
                    .setTitle(getString(R.string.ask_choose))
                    .setItems(R.array.menus, ({ _, which ->
                        when (which) {
                            0 -> DataHelper.deleteMemo(realm, item.id)
                            1 -> shareItem(item)
                        }
                    })).create()


    /**
     * Share selected item
     */
    private fun shareItem(item: Memo) {
        Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, item.content)
            type = "text/plain"

            if (resolveActivity(packageManager) != null) {
                startActivity(Intent.createChooser(this, getString(R.string.menu_share)))
            }
        }
    }

    /**
     * Go to writeTextActivity
     */
    private fun goToWriteToEdit(item: Memo) {
        Intent(this, WriteTextActivity::class.java).run {
            putExtra("itemId", item.id)

            startActivity(this)
        }
    }

    fun onFABClick() { //플로팅버튼클릭
        startActivity(Intent(this, WriteTextActivity::class.java))
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.menu_video -> {
                Intent(this, VideoActivity::class.java).run {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(this)
                }
                 true
            }
            else -> false
        }


    override fun onDestroy() {
        super.onDestroy()

        binding.recyclerText.adapter = null
        realm.close()
        disposables.dispose()
    }
}