import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import java.io.File
import javax.imageio.ImageIO
import javax.swing.Box
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel
import kotlin.math.abs
import kotlin.math.min
import kotlin.random.Random

val SIZE = arrayOf(400, 200)

/** Vytvori 100x ctvercu s nahodnou velikosti a barvou a ulozi do BufferedImage. */
fun nahodnyObrazek(): BufferedImage {
    val obrazek = BufferedImage(SIZE[0], SIZE[1], BufferedImage.TYPE_INT_RGB) //Vytvoreni obrazku s velikosti okna a jakymykoli barvy
    val grafika = obrazek.graphics  //Grafika obrazku
    // vykresli 100x ctverec s nahodnou barvou
    for (i in 0..100) {     //Forcyklus opakujici se 100-krat
        grafika.color = Color(Random.nextInt())     //Nahodna barva veci v grafice
        grafika.fillRect(Random.nextInt(SIZE[0]), Random.nextInt(SIZE[1]), Random.nextInt(10, SIZE[0]), Random.nextInt(10,SIZE[1]))  //Vykresleni ctverce
    } //Konec forcyklu
    return obrazek  //Vraceni obrazku
}//Konec funkce

fun main() { //Hlavni funkce
    // vytvoreni okna
    val platno = JLabel() //Vytvoreni platna na kresleni
    val platno_original = JLabel() //Vytvoreni platna na obrazek
    val box = Box.createVerticalBox().apply {//Spojeni obou platen do jednoho boxu
        add(platno) //Pridani prvniho platno
        add(platno_original) //Pridani druheho platna
    }//Konec boxu

    JFrame().apply { //Vytvoreni okna
        setSize(SIZE[0], 2*SIZE[1]) //Nastaveni velikosti
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE //Kdyz se zavre okno, zavre se aplikace
        isVisible = true //Nastaveni viditelnosti na true
        contentPane.add(box) //Pridani boxu (obou platen)
    }//Konec platna

    // obrazek ktery malujeme
    val original = ImageIO.read(File("images/tygr.jpg")).resize(SIZE[0], SIZE[1])//Nacteni a zmneneni velikosti obrazku ktery malujeme
    val prazdnyObrazek = BufferedImage(SIZE[0], SIZE[1], BufferedImage.TYPE_INT_RGB) //Vytvoreni makety BufferedImage
    prazdnyObrazek.draw {
        color = Color.WHITE
        fillRect(0, 0, SIZE[0], SIZE[1])
    }
    val imitace = Tvar(0, 0, 1, 1, Color.RED, Platno(platno, original, prazdnyObrazek)) //Nevim

    platno_original.icon = ImageIcon(original)  //Pridani obrazku ktery malujeme do jeho platna

    val evoluce = Evoluce(Array(1) { imitace })  //Vytvoreni evoluce
    val populace = evoluce.spust(1e-4) //Vytvoreni populace
    println("Reseni 0 = x*x + 1e-4 - 3: ${populace[0].kvalita}")    //Vypsani rovnice (uz nefunguje)
}


/**
 * Funkce xy2x(y, x, maximalniX): Vezme x a y tabulky a prevede je na jedno cislo (tabulku rozlozi na jeden radek a vezme index)
 */
fun yx2x(y: Int, x: Int, maxX: Int): Int {
    return y*maxX + x
}

val stetec = ImageIO.read(File("images/stetec1.png")).resize(30, 30)

/**
 * Nevim k cemu je platno
 */
class Platno(val platno: JLabel, val original: BufferedImage, var imitace: BufferedImage)

/**
 * Trida Ctverec/Kruh(x, y, sirka, vyska, barva, platno) dedi Jedince s typovym argumentem Kruh: Ctverec ktery kreslime s atributy pro:
 * pozici x,
 * pozici y,
 * sirku,
 * vysku,
 * barvu (r g b) a
 * platno, na ktere se ma vykreslit
 */
class Tvar(var x: Int, var y: Int, val w: Int, val h: Int, val barva: Color, val platno: Platno) : Jedinec<Tvar> {

    val obrazek = platno.imitace.getSubimage(x, y, min(w, platno.imitace.width-x), min(h, platno.imitace.height-y)).clone()


    init { //Konstruktor, ktery se spusti na zacatku (pri definovani promnene s touto tridou)
        vykresli(obrazek) //Vykresleni toho prazdneho obrazku
    }


    override val size: Int = 7 //Prepsani promnene size na 7 (z jedince)

    override val kvalita = let {
        var soucet = 0.0
        val d0 = obrazek.raster.dataBuffer as DataBufferInt
        val d1 = platno.original.raster.dataBuffer as DataBufferInt
        val d2 = platno.imitace.raster.dataBuffer as DataBufferInt
        for (x in 0 until obrazek.width) for (y in 0 until obrazek.height) {
            val i0 = yx2x(y, x, obrazek.width)
            val i1 = yx2x(y+this.y, x+this.x, platno.imitace.width)
            // spocte chybu tvaru vuci originalu a chybu imitace vuci originalu
            // kdyz je chyba tvaru stejna jako chyba imitace, pak imitaci nezlepsime a skore by melo zustat nizke
            val chybaTvaru = d1.color(i1).podobnost(d0.color(i0))
            val chybaImitace = d1.color(i1).podobnost(d2.color(i1))
            soucet += chybaTvaru - chybaImitace
        }
        soucet
    }

    /**
     * Pokud je klic:
     * 0. vrati pozici x
     * 1. vrati pozici y
     * 2. vrati sirku
     * 3. vrati vysku
     * 4. vrati cervenou barvu
     * 5. vrati zelenou barvu
     * 6. vrati modrou barvu
     */
    override fun get(key: Int): Int {
        return intArrayOf(x, y, w, h, barva.red, barva.green, barva.blue)[key]
    }

    /** Vrati noveho jedince, s danymi parametry (v poli values) */
    override fun new(values: IntArray): Tvar {
        for (i in values.indices) values[i] = abs(values[i]) //Forcyklus, ktery udela vsechny prvky v poli values absolutni

        val barva = Color(values[4]%256,values[5]%256,values[6]%256) //Vytvoreni promnene barva a nastaveni ji na barvu z pole values
        val (w, h) = Pair(platno.imitace.width, platno.imitace.height)
        return Tvar(values[0]%(w-1), values[1]%(h-1), 30, 30, barva, platno) //Vraceni Ctverce/kruhu s danymi parametry + s nove vytvorenymi promnenimy
    }

    /** Vykresli jedince na platno */
    override fun zobraz() {
        val icon = platno.imitace.clone().draw {
            drawImage(obrazek, x, y, null)
        }
        platno.platno.icon = ImageIcon(icon)
    }

    /** Zmneni parametr parametru platno na "prazdny obrazek" */
    override fun prijmout() {
        platno.imitace.draw {
            drawImage(obrazek, x, y, null)
        }
    }

    /** Vykresli obrazek(ctverec) */
    fun vykresli(obrazek: BufferedImage) {
        obrazek.draw { //Vytvoreni grafiky a aplikovani:
            color = barva
            fillRect(0, 0, w, h)
            // border
//            Rectangle(x, y, width-1, height-1).apply {
//                color = Color.BLACK
//                stroke = BasicStroke(2f )
//                setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE)
//                draw(this)
//            }

            // stetec
//            val i = BufferedImage(stetec.width, stetec.height, BufferedImage.TYPE_INT_ARGB)
//            i.createGraphics().apply {
//                rotate(Math.toRadians(x+y+0.0), i.width / 2.0, i.height / 2.0)
//                drawImage(stetec.obarvi(barva), 0,0, null)
//            }
//            drawImage(i, x, y, null)
        }
    }
}