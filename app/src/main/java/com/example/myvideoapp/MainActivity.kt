package com.example.myvideoapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog.show
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.system.Os.close
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.myvideoapp.Framents.FoldersFragment
import com.example.myvideoapp.Framents.VideosFragment
import com.example.myvideoapp.Model.Video
import com.example.myvideoapp.databinding.ActivityMainBinding
import java.io.File
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle:ActionBarDrawerToggle

    companion object{
        lateinit var videoList: ArrayList<Video>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setTheme(R.style.CoolPinkNav)
        setContentView(binding.root)
//        if (requestRuntimePermission()){
//            videoList=getAllVideos()
//            setFragment(VideosFragment())
//        }
        toggle= ActionBarDrawerToggle(this,binding.root,R.string.open,R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        binding.bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.videoView -> setFragment(VideosFragment())
                R.id.folderView -> setFragment(FoldersFragment())
            }
            return@setOnItemSelectedListener true
        }
        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.feedback -> Toast.makeText(this,"Feedback clicked",Toast.LENGTH_SHORT).show()
                R.id.about->Toast.makeText(this,"About clicked",Toast.LENGTH_SHORT).show()
                R.id.themes->Toast.makeText(this,"Themes clicked",Toast.LENGTH_SHORT).show()
                R.id.sortorder->Toast.makeText(this,"Sort Order clicked",Toast.LENGTH_SHORT).show()
                R.id.exit-> exitProcess(1)
            }
            return@setNavigationItemSelectedListener true
        }

    }

    private fun setFragment(fragment: Fragment){
        val transaction=supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentFL,fragment)
        transaction.disallowAddToBackStack()
        transaction.commit()
    }

    private fun requestRuntimePermission():Boolean  {
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),102)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show()
        } else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),102)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("Range")
    private fun getAllVideos():ArrayList<Video>{
        val tempList=ArrayList<Video>()
        val projection= arrayOf(MediaStore.Video.Media.TITLE,MediaStore.Video.Media.SIZE,MediaStore.Video.Media._ID,
        MediaStore.Video.Media.BUCKET_DISPLAY_NAME,MediaStore.Video.Media.DATA,MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.DURATION)
        val cursor=this.contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,projection, null, null,MediaStore.Video.Media.DATE_ADDED+" DESC")
        if (cursor!=null){
            if (cursor.moveToNext()){
                do {
                    val titlec = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE))
                    val idc = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE))
                    val folderc = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE))
                    val sizec = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE))
                    val durationc = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE)).toLong()
                    val pathc = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE))

                    try{
                        val file= File(pathc)
                        val artURI= Uri.fromFile(file)
                        val video=Video(title=titlec,id=idc, folderName =folderc,duration=durationc, size=sizec,
                        path=pathc, artUri=artURI)
                        if (file.exists()) tempList.add(video)
                    }catch (e:Exception){}
                }while (cursor.moveToNext())
                cursor.close()
            }
        }
        return tempList
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }

}