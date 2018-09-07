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
import android.view.Menu
import android.widget.Toast
import me.jy.danggi.R
import android.view.MenuItem


class WriteTextActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWriteTextBinding
    private val realm = Realm.getDefaultInstance()

    private var oldId: Int = 0
    private var oldContent: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_write_text)
        binding.activity = this

        initToolbar()

        intent?.let {
            oldId = it.getIntExtra("itemId", -1)

            if (oldId != -1) {
                oldContent = DataHelper.findMemoById(realm, oldId).content
                binding.editMemo.setText(oldContent)
            }
        }
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbarWrite?.toolbarWrite)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * 변경된 데이터를 위젯에게 브로드캐스트보냄.
     */
    private fun sendBroadcastToWidget() {
        DataHelper.findWidgetByMemoId(realm, oldId)?.let {
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

            if (content.isNotEmpty() || content == oldContent) {
                //아무것도 입력하지 않고 확인메뉴를 누른경우.
                onBackPressed()
            }
            if (oldId != -1) { //수정모드
                DataHelper.updateMemoAsync(realm, oldId, content)
                Toast.makeText(this, getString(R.string.edit_complete), Toast.LENGTH_SHORT).show()
                sendBroadcastToWidget() //브로드캐스트 전송
            } else { //등록모드
                DataHelper.addMemoAsync(realm, content)
                Toast.makeText(this, getString(R.string.save_complete), Toast.LENGTH_SHORT).show()
            }
        }

        override fun onCreateOptionsMenu(menu: Menu): Boolean {
            val inflater = menuInflater
            inflater.inflate(R.menu.menu_write, menu)
            return true
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean =
                when (item.itemId) {
                    R.id.menu_check -> {
                        saveMemo()
                        onBackPressed()
                        true
                    }
                    android.R.id.home -> {
                        onBackPressed()
                        true
                    }
                    else -> false
                }


        override fun onDestroy() {
            super.onDestroy()

            realm.close()
        }

    }