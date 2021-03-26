import java.awt.Color
import java.awt.Graphics2D
import java.awt.Image
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import kotlin.math.pow

fun BufferedImage.draw(fn: Graphics2D.() -> Unit): BufferedImage {
    val g = createGraphics()
    g.fn()
    g.dispose()
    return this
}

fun BufferedImage.obarvi(barva: Color): BufferedImage {
    val img  = this.clone()
    val data = img.raster.dataBuffer
    if (data is DataBufferInt) for (i in 0 until data.size) {
        val rgb = Color(barva.red, barva.green, barva.blue, (data.color(i).alpha*(barva.alpha / 255.0)).toInt()).rgb
        data.setElem(i, rgb)
    }
    return img
}

/** Vytvori kopii obrazku. */
fun BufferedImage.clone(): BufferedImage {
    val img = this
    return BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB).draw {
        drawImage(img, 0, 0, null)
    }
}

/** Zmeni velikost obrazku. */
fun BufferedImage.resize(newW: Int, newH: Int): BufferedImage {
    val tmp = this.getScaledInstance(newW, newH, Image.SCALE_SMOOTH)
    return BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB).draw {
        drawImage(tmp, 0, 0, null)
    }
}

/** Vezme pixel (rgb hodnota) z obrazku a vrati jako [Color] */
fun DataBufferInt.color(index: Int): Color {
    return Color(getElem(index), true)
}

/** Color Funkce podobnost(ostatni): Vrati rozdil mezi dvoma barvama (na kterou se tahle funkce aplikuje a na tu v argumetech)
 *
 * plati: barva.podobnost(barva) == 0
 *
 * */
fun Color.podobnost(other: Color): Double {
    return intArrayOf(red - other.red, green - other.green, blue - other.blue).velikost()
}

/** Vzdálenost bodu od počátku (ve 2D bod (0,0)).
 *  Podrobnější informace zde: <A href="https://www.matematika.cz/euklidovy-vety">https://www.matematika.cz/euklidovy-vety</A>
 * */
fun IntArray.velikost(): Double {
    return map{ it.toDouble().pow(2) }.sum().pow(.5)
}