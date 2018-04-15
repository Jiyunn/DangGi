package me.jy.danggi.activities

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.TextureView
import android.view.View
import android.widget.Toast
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MediaSourceEventListener
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.EventLogger
import com.google.android.exoplayer2.util.Util
import com.tedpark.tedpermission.rx2.TedRx2Permission
import io.realm.Realm
import me.jy.danggi.BR
import me.jy.danggi.MyApplication
import me.jy.danggi.R
import me.jy.danggi.database.DataHelper
import me.jy.danggi.databinding.ActivityWriteVideoBinding

class WriteVideoActivity : AppCompatActivity(), PlaybackPreparer, PlayerControlView.VisibilityListener {

    private lateinit var binding: ActivityWriteVideoBinding
    private lateinit var trackSelector: DefaultTrackSelector
    private lateinit var mainHandler: Handler
    private lateinit var eventLogger: EventLogger
    private lateinit var mediaDataSourceFactory: DataSource.Factory

    private val bandwidthMeter: DefaultBandwidthMeter = DefaultBandwidthMeter()
    private val realm: Realm = Realm.getDefaultInstance()
    private var uri: Uri? = null
    private var player: SimpleExoPlayer? = null

    private var oldId: Int = -1
    private val video: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_write_video)
        binding.setVariable(BR.activity, this)


        mediaDataSourceFactory = buildDataSourceFactory(true)
        initToolbar()

        intent.extras?.let {
            //수정하는 경우 컨텐츠 정보들 가져옴
            oldId = intent.getIntExtra("itemId", -1)
            val video = DataHelper.findVideoById(realm, oldId)
            uri = Uri.parse(video?.uri)
            binding.editVideoMemo.setText(video?.content)
            initPlayer()
        }
    }

    /**
     * 플레이어 설정
     */
    private fun initPlayer() {
        player.let {
            mainHandler = Handler()
            val videoTrackSelectionFactory: TrackSelection.Factory = AdaptiveTrackSelection.Factory(bandwidthMeter)
            trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
            eventLogger = EventLogger(trackSelector)

            player = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
            binding.playView.requestFocus()
            binding.playView.setControllerVisibilityListener(this)
            binding.playView.player = player

            player?.addListener(PlayerEventListener())
            player?.addListener(eventLogger)
            player?.playWhenReady = true
            player?.addVideoDebugListener(eventLogger)
            player?.addAudioDebugListener(eventLogger)

            mediaDataSourceFactory = DefaultDataSourceFactory(this,
                    Util.getUserAgent(this, "MyApplication"), bandwidthMeter)
        }

        val videoSource: MediaSource = ExtractorMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri)

        buildMediaSource(uri, "", mainHandler, eventLogger)

        player?.prepare(videoSource)
        binding.playView.setPlaybackPreparer(this)
    }

    /**
     * DataSource Build, Using Application.class's methods
     */
    private fun buildDataSourceFactory(useBandWidthMeter: Boolean): DataSource.Factory {
        return (application as MyApplication).buildDataSourceFactory(if (useBandWidthMeter) bandwidthMeter else null)
    }

    /**
     * 타입 찾아서 미디어 소스 생성
     */
    private fun buildMediaSource(uri: Uri?, overrideExtension: String,
                                 handler: Handler?, listener: MediaSourceEventListener?): MediaSource {

        @C.ContentType val type = if (TextUtils.isEmpty(overrideExtension))
            Util.inferContentType(uri)
        else
            Util.inferContentType(".$overrideExtension")

        when (type) {
            C.TYPE_DASH -> return DashMediaSource.Factory(
                    DefaultDashChunkSource.Factory(mediaDataSourceFactory),
                    buildDataSourceFactory(false))
                    .createMediaSource(uri, handler, listener)

            C.TYPE_SS -> return SsMediaSource.Factory(
                    DefaultSsChunkSource.Factory(mediaDataSourceFactory),
                    buildDataSourceFactory(false))
                    .createMediaSource(uri, handler, listener)

            C.TYPE_HLS -> return HlsMediaSource.Factory(mediaDataSourceFactory)
                    .createMediaSource(uri, handler, listener)

            C.TYPE_OTHER -> return ExtractorMediaSource.Factory(mediaDataSourceFactory)
                    .createMediaSource(uri, handler, listener)
            else -> {
                throw IllegalStateException("Unsupported type: $type")
            }
        }
    }

    /**
     * 메모 저장
     */
    private fun saveMemo() {
        val content: String = binding.editVideoMemo.text.toString()

        val textureView: TextureView? = binding.playView.videoSurfaceView as? TextureView
        val bitmap: Bitmap? = textureView?.bitmap //썸네일

        if (oldId != -1) { //수정하는 경우
            DataHelper.updateVideoAsync(realm, oldId, bitmap, uri, content)
            Toast.makeText(this, getString(R.string.edit_complete), Toast.LENGTH_SHORT).show()
        } else {
            DataHelper.addVideoAsync(Realm.getDefaultInstance(), bitmap, uri, content)
            Toast.makeText(this, getString(R.string.save_complete), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == video && resultCode == Activity.RESULT_OK) {
            uri = data!!.data
            initPlayer()
        }
    }


    fun onVideoSelectClick(v: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //마시멜로우 이상만 권한 검사
            getPermissionGrant()
        }
    }

    /**
     * 엑세스 권한 요청
     */
    private fun getPermissionGrant() {
        TedRx2Permission.with(this)
                .setRationaleTitle(R.string.rationale_title)
                .setRationaleMessage(R.string.rationale_message)
                .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request()
                .subscribe { tedPermissionResult ->
                    if (tedPermissionResult.isGranted) {
                        selectVideo()
                    } else {
                        Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
                    }
                }
    }


    private fun selectVideo() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "video/*"
        startActivityForResult(intent, video)
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbarWriteVideo)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_write, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_check -> {
                uri?.let {
                    saveMemo()
                    onBackPressed()
                }
            }
            android.R.id.home -> onBackPressed()
        }
        return false
    }

    //preparePlayback 구현
    override fun preparePlayback() {
        initPlayer()
    }

    //visibility listener 구현
    override fun onVisibilityChange(visibility: Int) {

    }


    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()

    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }

    inner class PlayerEventListener : Player.DefaultEventListener() {

        override fun onPlayerError(e: ExoPlaybackException?) {
            e?.printStackTrace()
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            if (playbackState == Player.STATE_READY)
                binding.playView.hideController()
        }
    }
}
