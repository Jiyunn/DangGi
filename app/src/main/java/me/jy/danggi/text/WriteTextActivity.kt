package me.jy.danggi.text

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.realm.Realm
import me.jy.danggi.data.DataHelper
import me.jy.danggi.databinding.ActivityWriteTextBinding
import android.appwidget.AppWidgetManager
import me.jy.danggi.common.provider.NormalWidget
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.widget.Toast
import me.jy.danggi.R
import android.view.MenuItem
import io.reactivex.disposables.CompositeDisposable


import me.jy.danggi.common.rx.RxBus
import me.jy.danggi.data.Memo


class WriteTextActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWriteTextBinding

    private val realm = Realm.getDefaultInstance()

    private var editedMemo: Memo? = null
    private val rxBus = RxBus.getInstance()
    private val disposables = CompositeDisposable()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_write_text)
        binding.activity = this

        initToolbar()

        checkEditModeOrNot()
    }

    /**
     * check RxBus.
     */
    private fun checkEditModeOrNot() {
        disposables.add(rxBus.toObservable()
                .doOnError { e -> e.printStackTrace() }
                .filter { data -> data is Memo }
                .map { data -> data as Memo }
                .subscribe {
                    editedMemo = it

                    binding.editMemo.setText(it.content)
                })
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbarWrite?.toolbarWrite)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * 변경된 데이터를 위젯에게 브로드캐스트보냄.
     */
    private fun sendBroadcastToWidget(id: String) {
        DataHelper.findWidgetByMemoId(realm, id)?.let {
            val widgetIds = arrayListOf<Int>()

            for (w in it) {
                widgetIds.add(w.widgetId)
            }

            Intent(this, NormalWidget::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

                putIntegerArrayListExtra("widgetIds", widgetIds)
                sendBroadcast(this)
            }
        }
    }


    /**
     * 입력한 메모를 update / insert 할 것인지 정하는 메소드.
     */
    private fun saveMemo() {
        val content = binding.editMemo.text.toString() //사용자가 입력한 문자

        if (editedMemo == null) { //등록모드
            DataHelper.addMemoAsync(realm, content)
            Toast.makeText(this, getString(R.string.save_complete), Toast.LENGTH_SHORT).show()
        }

        //수정모드
        editedMemo?.let {
            if (content == it.content) { //
                onBackPressed()
            }

            DataHelper.updateMemoAsync(realm, it.id, content)
            Toast.makeText(this, getString(R.string.edit_complete), Toast.LENGTH_SHORT).show()
            sendBroadcastToWidget(it.id) //브로드캐스트 전송
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_write, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
            when (item.itemId) {
                R.id.menu_check -> {
                    saveMemo()

                    Intent(this, TextActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(this)
                        finish()

                    }
                    true
                }
                android.R.id.home -> {
                    Intent(this, TextActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(this)
                        finish()
                    }
                    true
                }
                else -> false
            }


    override fun onDestroy() {
        super.onDestroy()

        realm.close()

        rxBus.shutdownBus()
        disposables.dispose()
    }
}