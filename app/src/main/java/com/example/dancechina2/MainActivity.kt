package com.example.dancechina2

import android.Manifest
import android.content.pm.FeatureInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker
import kotlinx.android.synthetic.main.activity_main.*
import tv.danmaku.ijk.media.ijkplayerview.widget.media.AndroidMediaController
import tv.danmaku.ijk.media.ijkplayerview.widget.media.IRenderView
import tv.danmaku.ijk.media.ijkplayerview.widget.media.IjkVideoView
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.net.URI

class MainActivity : AppCompatActivity() {


    companion object{
        val TAG = "MainActivity";
        /**
         * 中国舞二级A组
         */
        public var GROUP_A="a"

        /**
         * 中国舞二级B组
         */
        public var GROUP_B="b"
    }
    private var mMediaController: AndroidMediaController? = null

    private var mCurrentPosition = 1;

    /**
     * a:中国舞二级A组
     * b:中国舞二级B组
     */
    private var mGroup = GROUP_A
    /**
     * 权限申请 sd读、写、相机权限集合
     */
    private val requestPermission = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )
    private val mRequestPermissionCode = 10086 //request camera permission .


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main)

        init();
    }

    /**
     * 加载raw视频.
     */
    private fun init() {
        // init player
        IjkMediaPlayer.loadLibrariesOnce(null)

        radio_button_a.setOnCheckedChangeListener { buttonView, isChecked ->
            run {

                //rest  video position.
                if (isChecked) {
                    mGroup = GROUP_A
                    mCurrentPosition =1;
                }
                if(ijk_video_view.isPlaying){
                    stopPlay()
                }
            }
        }

        radio_button_b.setOnCheckedChangeListener { buttonView, isChecked ->
            run {
                //rest  video position.
                if (isChecked) {
                    mGroup = GROUP_B
                    mCurrentPosition =1;
                }
                if(ijk_video_view.isPlaying){
                    stopPlay()
                }
            }
        }


        //播放/暂停.
        video_play_btn.setOnClickListener {
            if(!ijk_video_view.isPlaying){
                startPlay()
            }else{
                stopPlay()
            }
        }


        //上一首
        last_video_btn.setOnClickListener {

            playLast()
        }

        //下一首
        next_video_btn.setOnClickListener {
            playNext()
        }

    }


    /**
     * //update the current video title .
     */
    private fun updateTitle(){

        var titles:Array<String>?

        if(mGroup.equals(GROUP_A)){
            titles = resources.getStringArray(R.array.group_a)
        }else{
            titles = resources.getStringArray(R.array.group_b)
        }

        var title = titles[mCurrentPosition-1]
        tv_title.text = title;
    }

    /**
     * 停止播放.
     */
    private fun stopPlay() {
        //使用新的播放器. IjkPlayer.
        ijk_video_view.pause()
        ijk_video_view.stopPlayback()
        ijk_video_view.release(true)
        ijk_video_view.stopBackgroundPlay()
        IjkMediaPlayer.native_profileEnd()
    }

    /**
     * 上一首
     */
    private fun playLast() {

        if(mCurrentPosition<=1){
            Toast.makeText(this,"当前已经是第一个视频",Toast.LENGTH_SHORT).show()
            return
        }else{
            mCurrentPosition--
        }

        if(ijk_video_view.isPlaying){
            stopPlay()
        }


        startPlay()
    }

    /**
     * 下一首.
     */
    private fun playNext() {
       if(mCurrentPosition >=8){
            Toast.makeText(this,"当前已经是最后一个视频",Toast.LENGTH_SHORT).show()
            return
        }else{
            mCurrentPosition++
        }

        if(ijk_video_view.isPlaying){
            stopPlay()
        }

        startPlay()
    }

    private fun startPlay(){
        Log.i(TAG,"startPlay()")
        IjkMediaPlayer.native_profileBegin(IjkMediaPlayer.IJK_LIB_NAME_FFMPEG)

        var videoPath =  "android.resource://${packageName}/raw/${mGroup}0${mCurrentPosition}";
//        var videoPath = "${Environment.getExternalStorageDirectory()}/chinadance/${mGroup}0${mCurrentPosition}.mp4";
        Log.i(TAG,"paly video : ${videoPath}")
        Log.i(TAG," anoth storage getExternalFilesDir:${getExternalFilesDir(Environment.DIRECTORY_MOVIES)}")
        ijk_video_view.setAspectRatio(IRenderView.AR_ASPECT_FILL_PARENT)
        ijk_video_view.setVideoPath(videoPath, IjkVideoView.IJK_TYPE_FILE_PLAY)
        ijk_video_view.setRender(IjkVideoView.RENDER_SURFACE_VIEW)
        /*ijk_video_view.setOnPreparedListener {
            it.start()
        }*/
        ijk_video_view.start()
        //update the current video title .
        updateTitle()
    }


    override fun onResume() {
        super.onResume()
        // Example of a call to a native method
        // Example of a call to a native method
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (PermissionChecker.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PermissionChecker.PERMISSION_GRANTED
            ) {
                requestPermissions(requestPermission, mRequestPermissionCode)
            } else {
//                startPlay()
            }
        } else {
//            startPlay()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == mRequestPermissionCode) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                startPlay()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(ijk_video_view.isPlaying){
            stopPlay()
        }
    }
}