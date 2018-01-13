package ui.anwesome.com.kotlinsinewaveview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ui.anwesome.com.sinewavevie.SineWaveView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SineWaveView.create(this)
    }
}
