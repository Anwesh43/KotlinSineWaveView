package ui.anwesome.com.kotlinsinewaveview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import ui.anwesome.com.sinewavevie.SineWaveView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = SineWaveView.create(this)
        view.addOnSineWaveListener { ox,dx ->
            Toast.makeText(this,"moved from $ox to $dx",Toast.LENGTH_SHORT).show()
        }
        fullScreen()
    }
}
fun AppCompatActivity.fullScreen() {
    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    supportActionBar?.hide()
}