package ui.anwesome.com.sinewavevie

/**
 * Created by anweshmishra on 13/01/18.
 */
import android.app.Activity
import android.view.*
import android.content.*
import android.graphics.*
import java.util.concurrent.ConcurrentLinkedQueue

class SineWaveView(ctx:Context):View(ctx) {
    val paint:Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val renderer = Renderer(this)
    var onSineWaveListener:OnSineWaveListener?=null
    override fun onDraw(canvas:Canvas) {
        canvas.drawColor(Color.parseColor("#212121"))
        renderer.render(canvas,paint)
    }
    fun addOnSineWaveListener(listener: (Float,Float)->Unit) {
        onSineWaveListener = OnSineWaveListener(listener)
    }
    override fun onTouchEvent(event:MotionEvent):Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }
    data class SineWave(var x:Float,var y:Float,var a_y:Float,var a_x:Float,var scale:Float = 0f,var orig:Int = 0) {
        var points:ConcurrentLinkedQueue<PointF> = ConcurrentLinkedQueue()
        val sineWaveState:SineWaveState = SineWaveState()
        fun draw(canvas:Canvas,paint:Paint,drawcb:(Float)->Unit) {
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
            drawcb(scale)
        }
        private fun updateXY(scale_x:Float,scale_y:Float) {
            points.add(PointF(scale_x*a_x,scale_y*a_y))
            scale = Math.sin((sineWaveState.deg/4)*Math.PI/180).toFloat()
            orig = points.size
        }
        private fun removePoints(stopcb:(Float,Float)->Unit) {
            if(points.size > 0) {
                points.removeFirst()
                scale = points.size.toFloat()/orig
            }
            else {
                stopcb(x,x+a_x)
                x += a_x
            }
        }
        fun update(updatecb:(Float)->Unit,stopcb: (Float,Float) -> Unit) {
            sineWaveState.update({scale_x,scale_y ->
                updateXY(scale_x,scale_y)
                var x_offset = 0f
                if(points.size > 0) {
                    x_offset = points.getLast()?.x?:0f
                }
                updatecb(x+x_offset)
            },{stopcb2 ->
                removePoints(stopcb2)
            },stopcb)
        }
        fun startUpdating(startcb:()->Unit) {
            sineWaveState.startUpdating(startcb)
        }
    }
    data class SineWaveState(var deg:Float = 0f,var dir:Float = 0f) {
        fun update(updateXY:(Float,Float)->Unit,removePoints:((Float,Float)->Unit)->Unit,stopcb:(Float,Float)->Unit) {
            if(deg < 360) {
                deg += dir*10f
                updateXY(deg/360f,Math.sin(deg*Math.PI/180).toFloat())
            }
            else {
                removePoints {ox,dx ->
                    deg = 0f
                    dir = 0f
                    stopcb(ox,dx)
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
        var screen:Screen?=null
        fun render(canvas:Canvas,paint:Paint) {
            val w = canvas.width.toFloat()
            val h = canvas.height.toFloat()
            if(time == 0) {
                sineWave = SineWave(0f,h/2,w/4,h/4)
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = Math.min(w,h)/45
                paint.strokeCap = Paint.Cap.ROUND
                paint.color = Color.parseColor("#0097A7")
                screen = Screen(w)
            }
            screen?.draw(canvas,paint,{
                sineWave?.draw(canvas,paint,{it ->
                    canvas.drawDoubleSideProgressLine(-(screen?.x?:0f) +w/2,h-h/10,9*w/10,it,paint)
                })
            })
            time++
            animator.animate {
                sineWave?.update({px ->
                    screen?.update(px)
                },{ox,dx ->
                    animator.stop()
                    view.onSineWaveListener?.listener?.invoke(ox,dx)
                })
            }
        }
        fun handleTap() {
            sineWave?.startUpdating {
                animator.start()
            }
        }
    }
    companion object {
        fun create(activity:Activity):SineWaveView {
            val view = SineWaveView(activity)
            activity.setContentView(view)
            return view
        }
    }
    data class Screen(var w:Float,var x:Float = 0f) {
        fun draw(canvas:Canvas,paint:Paint,drawcb:()->Unit) {
            canvas.save()
            canvas.translate(x,0f)
            drawcb()
            canvas.restore()
        }
        fun update(px:Float) {
            if(px+x > w) {
                x -= w
            }
        }
    }
    data class OnSineWaveListener(var listener:(Float,Float)->Unit)
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

fun ConcurrentLinkedQueue<PointF>.getLast():PointF? {
    var i = 0
    forEach {
        if(i == size-1) {
            return it
        }
        i++
    }
    return null
}
fun Canvas.drawDoubleSideProgressLine(x:Float,y:Float,size:Float,scale:Float,paint:Paint) {
    save()
    translate(x,y)
    drawLine(-(size/2-size/5)*scale,0f,(size/2-size/5)*scale,0f,paint)
    for(i in 0..1) {
        var x_arc = (size/2-size/10)
        var dir = 1-2*i
        drawArc(RectF(dir*x_arc-size/10,-size/10,dir*x_arc+size/10,size/10),0f,360f*scale,false,paint)
    }
    restore()
}