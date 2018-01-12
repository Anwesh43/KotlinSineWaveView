package ui.anwesome.com.sinewavevie

/**
 * Created by anweshmishra on 13/01/18.
 */
import android.view.*
import android.content.*
import android.graphics.*
import java.util.concurrent.ConcurrentLinkedQueue

class SineWaveView(ctx:Context):View(ctx) {
    val paint:Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    override fun onDraw(canvas:Canvas) {

    }
    override fun onTouchEvent(event:MotionEvent):Boolean {
        return true
    }
    data class SineWave(var x:Float,var y:Float,var a_y:Float,var a_x:Float) {
        var points:ConcurrentLinkedQueue<PointF> = ConcurrentLinkedQueue()
        fun draw(canvas:Canvas,paint:Paint) {
            canvas.save()
            canvas.translate(x,y)
            val path = Path()
            var i = 0
            points.forEach {
                if(i == 0) {
                    path.moveTo(it.x,it.y)
                }
                else {
                    path.lineTo(it.x,it.y)
                }
                i++
            }
            canvas.drawPath(path,paint)
            canvas.restore()
        }
        fun updateXY(scale_x:Float,scale_y:Float) {
            points.add(PointF(scale_x*a_x,scale_y*a_y))
        }
        fun removePoints(stopcb:()->Unit) {
            if(points.size > 0) {
                points.removeFirst()
                stopcb()
            }
        }
    }
    data class SineWaveState(var deg:Float = 0f,var dir:Float = 0f) {
        fun update(updateXY:(Float,Float)->Unit,removePoints:(()->Unit)->Unit,stopcb:()->Unit) {
            if(deg < 360) {
                deg += 1f
                updateXY(deg/360f,Math.sin(deg*Math.PI/180).toFloat())
            }
            else {
                removePoints {
                    deg = 0f
                    stopcb()
                }
            }
        }
        fun startUpdating(startcb:()->Unit) {
            if(dir == 0f) {
                dir = 1f
                startcb()
            }
        }
    }
}
fun ConcurrentLinkedQueue<PointF>.removeFirst() {
    var i = 0
    forEach {
        if(i == 0) {
            remove(it)
            return
        }
        i++
    }

}