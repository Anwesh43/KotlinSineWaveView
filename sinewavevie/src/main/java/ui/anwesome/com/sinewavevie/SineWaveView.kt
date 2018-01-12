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
        fun updateXY(x:Float,y:Float) {
            points.add(PointF(x,y))
        }
        fun removePoints(stopcb:()->Unit) {
            if(points.size > 0) {
                points.removeFirst()
                stopcb()
            }
        }
    }
}
fun ConcurrentLinkedQueue<PointF>.removeFirst() {
    var i = 0
    forEach {
        if(i == 0) {
            remove(it)
        }
        i++
    }

}