package cn.jzl.lko.math

import cn.jzl.lko.geom.RectCorners
import cn.jzl.lko.geom.Rectangle
import cn.jzl.lko.geom.RoundRectangle
import cn.jzl.lko.geom.vector.path.buildVectorPath
import cn.jzl.lko.geom.vector.path.roundRectangle
import kotlin.test.Test

class IntRectTest {

    @Test
    fun test() {
        val path = buildVectorPath {
//            ellipse(50f, 50f, 10f, 20f)
//            ellipse(100f, 100f, 40f, 20f)
//            rectangle(Rectangle(0f, 0f, 30f, 30f))

            roundRectangle(
                RoundRectangle(
                    Rectangle(0f, 0f, 100f, 100f),
                    RectCorners(50f, 50f, 50f, 50f)
                )
            )

        }
        val html = """
            <!DOCTYPE html>
            <html>
            <body>
            <svg xmlns="https://www.w3.org/2000/svg" version="1.1">
            	<path d = "${path.toSvgString()}" />
            </svg> 
            </body>
            </html>
        """.trimIndent()

        println(html)
    }
}