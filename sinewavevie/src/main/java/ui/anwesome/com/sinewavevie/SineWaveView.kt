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
    val renderer = Renderer(this)
    override fun onDraw(canvas:Canvas) {
        renderer.render(canvas,paint)
    }
    override fun onTouchEvent(event:MotionEvent):Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }
    data class SineWave(var x:Float,var y:Float,var a_y:Float,var a_x:Float) {
        var points:ConcurrentLinkedQueue<PointF> = ConcurrentLinkedQueue()
        val sineWaveState:SineWaveState = SineWaveState()
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
        private fun updateXY(scale_x:Float,scale_y:Float) {
            points.add(PointF(scale_x*a_x,scale_y*a_y))
        }
        private fun removePoints(stopcb:()->Unit) {
            if(points.size > 0) {
                points.removeFirst()
            }
            else {
                stopcb()
                x += a_x
            }
        }
        fun update(stopcb: () -> Unit) {
            sineWaveState.update({scale_x,scale_y ->
                updateXY(scale_x,scale_y)
            },{stopcb2 ->
                removePoints(stopcb2)
            },stopcb)
        }
        fun startUpdating(startcb:()->Unit) {
            sineWaveState.startUpdating(startcb)
        }
    }
    data class SineWaveState(var deg:Float = 0f,var dir:Float = 0f) {
        fun update(updateXY:(Float,Float)->Unit,removePoints:(()->Unit)->Unit,stopcb:()->Unit) {
            if(deg < 360) {
                deg += dir
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
    data class Animator(var view:SineWaveView,var animated:Boolean = false) {
        fun animate(updatecb:()->Unit) {
            if(animated) {
                updatecb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                }
                catch(ex:Exception) {

                }
            }
        }
        fun start() {
            if(!animated) {
                animated = true
                view.postInvalidate()
            }
        }
        fun stop() {
            if(animated) {
                animated = false
            }
        }
    }
    data class Renderer(var view:SineWaveView,var time:Int = 0) {
        val animator = Animator(view)
        var sineWave:SineWave?=null
        fun render(canvas:Canvas,paint:Paint) {
            if(time == 0) {
                val w = canvas.width.toFloat()
                val h = canvas.height.toFloat()
                sineWave = SineWave(0f,h/2,w/4,h/4)
            }
            sineWave?.draw(canvas,paint)
            time++
            animator.animate {
                sineWave?.update {
                    animator.stop()
                }
            }
        }
        fun handleTap() {
            sineWave?.startUpdating {
                animator.start()
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