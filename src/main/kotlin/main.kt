import org.w3c.dom.*
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Date
import kotlin.math.*

/**
 * 使用Kotlin语言开发的Web时钟
 * Kotlin-JS [https://kotlinlang.org/docs/reference/js-project-setup.html]
 * Api参考 WebApi-Canvas [https://developer.mozilla.org/zh-CN/docs/Web/API/Canvas_API]
 * 视频参考 [https://www.bilibili.com/video/BV1WW411z7PA?p=632]
 * module参考 [https://github.com/mdn/js-examples/blob/master/modules/basic-modules/modules/canvas.js]
 */
fun main(args: Array<String>) {
    Clock().apply {
        if (!isSupport()) {
            window.alert("浏览器不支持Canvas绘制")
            return@apply
        }
        draw()
        window.setInterval({ draw() }, 1000)
    }
}

class Clock {
    private val PI2 = 2 * PI

    private val windowWidth = window.innerWidth
    private val windowHeight = window.innerHeight

    private val radius = (min(windowWidth, windowHeight) - 100) / 2.0

    private val canvas: HTMLCanvasElement
    private val canvasContext: CanvasRenderingContext2D?

    init {
        document.createElement("canvas").apply {
            this as HTMLCanvasElement

            style.display = "block"

            width = windowWidth
            height = windowHeight

            document.body!!.appendChild(this)

            val tmpContext = getContext("2d")
            canvasContext = if (tmpContext == null) null else tmpContext as CanvasRenderingContext2D
            canvas = this


            // https://stackoverflow.com/questions/18017260/how-to-add-hyperlink-to-image-in-canvas-element
            canvas.addEventListener(
                    "click",
                    { _ -> window.open("https://github.com/hellofun-github/Kotlin-JS-Clock") },
                    false
            )
            canvas.addEventListener(
                    "mousemove",
                    { _ -> document.body!!.style.cursor = "pointer" },
                    false
            )
        }
    }

    /**
     * 是否支持绘制
     */
    fun isSupport(): Boolean = canvasContext != null

    fun draw() {
        canvasContext?.apply {
            clearAll()
            drawBackground()
            drawBrand()
            drawDot()
            drawHourText()
            Date().apply {
                drawHourLine(getHours(), getMinutes())
                drawMinuteLine(getMinutes())
                drawSecondLine(getSeconds())
            }
            drawCenter()
        }
    }

    /**
     * 擦除所有
     */
    private fun clearAll() {
        canvasContext?.apply {
            save()
            translate(0.0, 0.0)
            clearRect(0.0, 0.0, windowWidth.toDouble(), windowHeight.toDouble())
            restore()
        }
    }

    /**
     * 绘制 秒针
     */
    private fun drawSecondLine(second: Int) {
        canvasContext?.apply {
            save()
            // 变换原点
            translate(windowWidth / 2.0, windowHeight / 2.0)
            beginPath()
            rotate(PI2 / 60 * second)
            lineCap = CanvasLineCap.ROUND
            lineWidth = 3.0
            fillStyle = "#f00"

            moveTo(0.0, 30.0)
            lineTo(-5.0, -0.0)
            lineTo(0.0, -radius + 50)
            lineTo(5.0, 0.0)
            lineTo(0.0, 30.0)

            fill()
            restore()
        }
    }


    /**
     * 绘制 分针
     */
    private fun drawMinuteLine(minute: Int) {
        canvasContext?.apply {
            save()
            // 变换原点
            translate(windowWidth / 2.0, windowHeight / 2.0)
            beginPath()

            rotate(PI2 / 60 * minute)
            lineCap = CanvasLineCap.ROUND
            lineWidth = 8.0
            moveTo(0.0, 15.0)
            lineTo(0.0, -radius / 4 * 3)
            stroke()
            restore()
        }
    }

    /**
     * 绘制 时针
     */
    private fun drawHourLine(hour: Int, minute: Int) {
        canvasContext?.apply {
            save()
            // 变换原点
            translate(windowWidth / 2.0, windowHeight / 2.0)
            beginPath()
            rotate(PI2 / 12 * hour + PI2 / 12 / 60 * minute)
            lineCap = CanvasLineCap.ROUND
            lineWidth = 10.0
            moveTo(0.0, 15.0)
            lineTo(0.0, -radius / 2)
            stroke()
            restore()
        }
    }

    /**
     * 绘制小时的文字
     */
    private fun drawHourText() {
        canvasContext?.apply {
            listOf("3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "1", "2").forEachIndexed { index, s ->

                save()
                // 变换原点
                translate(windowWidth / 2.0, windowHeight / 2.0)

                beginPath()

                val rad = PI2 / 12 * index

                font = "35px Arial"
                textAlign = CanvasTextAlign.CENTER
                textBaseline = CanvasTextBaseline.MIDDLE

                fillStyle = if (index % 3 == 0) "#000" else "#ccc"
                fillText(s, cos(rad) * (radius - 50), sin(rad) * (radius - 50))

                restore()
            }
        }
    }

    /**
     * 绘制60个点
     */
    private fun drawDot() {
        canvasContext?.apply {
            (0..59).forEach {
                save()
                // 变换原点
                translate(windowWidth / 2.0, windowHeight / 2.0)
                beginPath()
                val rad = PI2 / 60 * it
                val x = cos(rad) * (radius - 16)
                val y = sin(rad) * (radius - 16)

                arc(x, y, 4.0, 0.0, PI2)
                fillStyle = if (it % 5 == 0) "#000" else "#ccc"
                fill()
                restore()
            }
        }
    }

    /**
     * 绘制圆心
     */
    private fun drawCenter() {
        canvasContext?.apply {
            save()

            // 变换原点
            translate(windowWidth / 2.0, windowHeight / 2.0)
            beginPath()
            arc(0.0, 0.0, 6.0, 0.0, PI2)
            fillStyle = "#666"
            fill()

            restore()
        }
    }

    /**
     * 绘制品牌
     */
    private fun drawBrand() {

        canvasContext?.apply {
            save()

            translate(windowWidth / 2.0, windowHeight / 2.0)

            textBaseline = CanvasTextBaseline.MIDDLE
            textAlign = CanvasTextAlign.CENTER

            font = "${radius / 10}px Arial"
            fillText("Simple-Clock", 0.0, -(radius / 2))

            font = "${radius / 15}px Arial"
            fillText("Time is life...", 0.0, -(radius / 3))

            restore()
        }
    }


    /**
     * 绘制表盘（背景）
     */
    private fun drawBackground() {
        canvasContext?.apply {
            save()
            // 变换原点
            translate(windowWidth / 2.0, windowHeight / 2.0)
            // 绘制路径
            beginPath()
            lineWidth = 10.0
            arc(0.0, 0.0, radius, 0.0, PI2)
            stroke()

            restore()
        }
    }
}