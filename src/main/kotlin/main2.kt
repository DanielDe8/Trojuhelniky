import java.awt.*                    //Importy
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import java.io.File
import java.lang.Math.pow
import javax.imageio.ImageIO
import javax.swing.Box
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel
import kotlin.math.*
import kotlin.random.Random

val SIZE  = arrayOf(400, 200) //Velikost okna
val cellSize = 10
val TABLE = arrayOf((SIZE[0] / cellSize), (SIZE[1] / cellSize))
val pocetTvaru = (TABLE[0] * TABLE[1])

typealias X = Double  // Aliasy typu z vyres rovnici
typealias XMin = Double
typealias XMax = Double
typealias Chyba = Double // Y


/**
 * Funkce vytvarejici nahodnr cislo
 */
fun nahodneCislo(x: X): X {
    return Random.nextDouble(x - 3.0, x + 3.0)
}

/**
 * Funkce vracejici polovinu dvou cisel
 */
fun rozpulCislo(cislo1: Double, cislo2: Double): Double {
    return (abs(cislo1) + cislo2) / 2
}

/**
 * Vytvori nahodny obrazku.
 *
 * Vytvori 100x ctvercu s nahodnou velikosti a barvou a ulozi do BufferedImage.
 */
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

/**
 * Zmeni velikost obrazku.
 */
fun BufferedImage.resize(newW: Int, newH: Int): BufferedImage { //Zacatek funkce
    val tmp = this.getScaledInstance(newW, newH, Image.SCALE_SMOOTH) // Vytvoreni zmneneneho obrazku
    val dimg = BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB) //Vytvoreni BufferedImage
    dimg.createGraphics().apply { //Vytvoreni grafiky
        drawImage(tmp, 0, 0, null) //Vykresleni zmneneneho obrazku do BufferedImage
        dispose() //Zavreni grafiky
    }//Konec grafiky
    return dimg //Vraceni BufferedImage
}//Konec funkce

fun BufferedImage.nejcetnejsiBarva(): Color {
    val data = raster.dataBuffer as DataBufferInt
    val colors = Array(256*256*256) {0}

    for (i in 0..data.data.size-1) {
        val color = data.color(i)
        colors[color.red*256*256 +color.green*256 +color.blue] += 1
    }
    val rgb = colors.withIndex().maxBy {it.value}!!.index

    return Color(rgb)
}

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
    val original = ImageIO.read(File("src/main/kotlin/morro.jpg")).resize(SIZE[0], SIZE[1])//Nacteni a zmneneni velikosti obrazku ktery malujeme
    val prumernaBarva = original.nejcetnejsiBarva()

    val prazdnyObrazek = BufferedImage(SIZE[0], SIZE[1], BufferedImage.TYPE_INT_RGB) //Vytvoreni makety BufferedImage
    prazdnyObrazek.createGraphics().apply {
        color = prumernaBarva
        fillRect(0, 0, SIZE[0], SIZE[1])
    }
    val imitace = Tvar(0, 0, 0, 0, Color.RED, 0, Platno(platno, original, prazdnyObrazek)) //Nevim

    platno_original.icon = ImageIcon(original)  //Pridani obrazku ktery malujeme do jeho platna

    val evoluce = Evoluce(Array(1) { imitace })  //Vytvoreni evoluce
    val populace = evoluce.spust(1e-4) //Vytvoreni populace
    println("Reseni 0 = x*x + 1e-4 - 3: ${populace[0].skore}")    //Vypsani rovnice (uz nefunguje)
}//Konec hlavni funkce

/*
          y
          |    /
chyba |  |   /
       |  |  /
       |  | /
       |  |/_____________
          ^   ^          x
    hledame   nahodne reseni
*/

/**
 * Nepouzivana funkce vyresRovnici()
 */
fun vyresRovnici(chyba: (X) -> Chyba, presnost: Double, reseni: (X) -> X): ArrayList<X> { //Zacatek funkce
    var xy = arrayListOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0) //Vytvoreni pole xy
    var ix = 0 //Index na poli xy
    while (abs(chyba(xy[ix])) > presnost) { //Opakuj dokud vysledek neni dokonaly
        val nove_x = reseni(xy[ix]) //Vytvoreni noveho x

        if (abs(chyba(xy[ix])) > abs(chyba(nove_x))) { //Pokud noove x bude "lepsi", nez to stare tak
//            println(x)
            xy[ix] = nove_x //Stare x = Nove x
            ix = Random.nextInt(0, xy.size) //Index na poli xy = nahodny
        }//Konec ifu
    }//Konec while cyklu
    return xy //Vraceni pole xy
}//Konec funkce

typealias Skore = Double //Vytvoreni typu skore ktere je zastupce typy double


/**
 * Jedinec: Obsahuje nektere metody a atributy ruznych tvaru
 */
interface Jedinec<Jedinec> { //Zacatek jedince
    val size: Int     //Vytvoreni promnene size
    val skore: Double //Vytvoreni promnene skore, ktera obsahuje zlepseni/zhorseni Jedince

    operator //Uz jsem zapomnel

    /**
     * Funkce get(klic): Zatim ni nedela
     */
    fun get(key: Int): Int

    /**
     * Funkce new(parametry Jedince): Zatim nic nedela
     */
    fun new(values: IntArray): Jedinec

    /**
     * Funkce vykresli(): Zatim nic nedela
     */
    fun vykresli()

    /**
     * Funkce prijmout(): Zatim nic nedela
     */
    fun prijmout()

    /**
     * Funkce new(klic, fn = Lambda): Vrati trochu zmneneneho jedince (ma 1 parametr(ktery urcuje klic) trochu jiny (jak jiny urcuje lambda funkce fn)<br>
     * @param fn funkce ktera zmeni dany atribut
     */
    fun new(key: Int, fn: (Int) -> Int): Jedinec {
        val values = IntArray(size) { get(it) }
        values[key] = fn(values[key])
        return new(values)
    }

    /**
     * Funkce new(): Vrati uplne nahodneho jedince
     */
    fun new(): Jedinec {
        return new(IntArray(size) { Random.nextInt() })
    }

    /**
     * Vrati nahodne upraveneho jedince (pomoci funkce new (pro upravu))
     *
     * Zmeni nahodny atribut o -10 az +10.
     */
    fun mutace(): Jedinec {
        return new(Random.nextInt(size)) { it + Random.nextInt(-10, 10) }
    }

    /**
     * Funkce mutace(otec): Vyhodi TODO chybu
     */
    fun krizeni(otec: Jedinec): Jedinec {
        TODO("Krizeni neni naimplementovano.")
    }
}//Konec jedince

//class Cislo(val dna: Double = Random.nextDouble(-1000.0, 1000.0)) : Jedinec<Cislo> {
//    override fun krizeni(otec: Cislo): Cislo {
//        return Cislo((abs(this.dna) + otec.dna) / 2)
//    }
//
//    override fun skore(): Double {
//        return 0.0
//    }
//
//    override fun mutace(): Cislo {
//        return Cislo(Random.nextDouble(dna - 3.0, dna + 3.0))
//    }
//
//    override fun toString(): String {
//        return "$dna"
//    }
//
//    override fun vykresli() {
//        println(dna)
//    }
//
//    override fun novy(): Cislo {
//        return Cislo()
//    }
//}

/**
 * BufferedImage Funkce clone():  Naklonuje obrazek (BufferedImage)
 */
fun BufferedImage.clone(): BufferedImage {
    return BufferedImage(colorModel, copyData(null), isAlphaPremultiplied, null)
}

/**
 * Funkce xy2x(y, x, maximalniX): Vezme x a y tabulky a prevede je na jedno cislo (tabulku rozlozi na jeden radek a vezme index)
 */
fun yx2x(y: Int, x: Int, maxX: Int): Int {
    return y*maxX + x
}

fun BufferedImage.obarvi(barva: Color): BufferedImage {
    val img  = this.clone()
    val data = img.raster.dataBuffer
    if (data is DataBufferInt) for (i in 0 until data.size) {
        val rgb = Color(barva.red, barva.green, barva.blue, data.color(i).alpha).rgb
        data.setElem(i, rgb)
    }
    return img
}

val stetec = ImageIO.read(File("src/main/kotlin/stetec1.png")).resize(cellSize, cellSize)

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
class Tvar(var x: Int, var y: Int, val width: Int, val height: Int, val barva: Color, var rotace: Int, val platno: Platno) : Jedinec<Tvar> {

    val obrazek = platno.imitace.clone() //Vytvoreni prazdneho obrazku

    init { //Konstruktor, ktery se spusti na zacatku (pri definovani promnene s touto tridou)
        vykresli(obrazek) //Vykresleni toho prazdneho obrazku
    }//Konec konstruktoru (initu)

    /**
     * Funkce vykresli(obrazek): Vykresli obrazek(ctverec)
     */
    fun vykresli(obrazek: BufferedImage) {
        obrazek.createGraphics().apply { //Vytvoreni grafiky a aplikovani:
            color = barva
            fillRect(x, y, this@Tvar.width, this@Tvar.height).apply { //Vytvoreni ctverce podle atributu (jedine dva nepouzivane atributy jsou platno a barva) a aplikovani:
                color = barva //Nastaveni barvy na barvu v atribbutech
                rotate(Math.toRadians(rotace+0.0), width / 2.0, height / 2.0)
//                fill(this) //Vybarveni this (ctverce)
            }//Konec aplikovani na ctverec
            // border
//            Rectangle(x, y, width-1, height-1).apply {
//                color = Color.BLACK
//                stroke = BasicStroke(2f )
//                setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE)
//                draw(this)
//            }

//            val i = BufferedImage(stetec.width, stetec.height, BufferedImage.TYPE_INT_ARGB)
//            i.createGraphics().apply {
//                rotate(Math.toRadians(x+y+0.0), i.width / 2.0, i.height / 2.0)
//                drawImage(stetec.obarvi(barva), 0,0, null)
//            }
//            drawImage(i, x, y, null)


            dispose()
        }//Konec aplikovani na grafyku
    }//Konec funkce

    override val size: Int = 8 //Prepsani promnene size na 7 (z jedince)
    override val skore: Double = x.let { //Prepsani promnene skore, pouzivame lambda funkci let (promnenou skore to prepise na to, co vrati tato lambda funkce)
        var soucet = 0.0 //Definovani promnene soucet na 0.0
        var pocetPx = 0  //Definovani promnene pocetPx (pocetPixelu) na 0
        val d0 = obrazek.raster.dataBuffer as DataBufferInt
        val d1 = platno.original.raster.dataBuffer as DataBufferInt
        val d2 = platno.imitace.raster.dataBuffer as DataBufferInt
        for (y in max(0,y).until(min(y+height, obrazek.height))) {
            for (x in max(0, x).until(min(x + width, obrazek.width))) { //Nevim
                val i = yx2x(y, x, obrazek.width)  //Vytvoreni promnene x ktera je xy2x (jiz definovana funkce) s argumenty x, y a sirka "prazdneho obrazku"
                // spocte chybu tvaru vuci originalu a chybu imitace vuci originalu
                // kdyz je chyba tvaru stejna jako chyba imitace, pak imitaci nezlepsime a skore by melo zustat nizke
                val chybaTvaru = d1.color(i).podobnost(d0.color(i))
                val chybaImitace = d1.color(i).podobnost(d2.color(i))  //Nevim
                soucet += chybaTvaru - chybaImitace
                pocetPx += 1  //Pricitani 1 k promnene pocetPx (pocetPixelu)
            }
        }

        soucet //Vraceni promnene soucet
    }

    /**
     * Prepsani Funkce get(klic): pokud je klic:
     * 0. vrati pozici x
     * 1. vrati pozici y
     * 2. vrati sirku
     * 3. vrati vysku
     * 4. vrati cervenou barvu
     * 5. vrati zelenou barvu
     * 6. vrati modrou barvu
     * 7. cokoliv jineho vrati rotaci
     */
    override fun get(key: Int): Int {
        return when (key) {
            0 -> x
            1 -> y
            2 -> width
            3 -> height
            4 -> barva.red
            5 -> barva.green
            6 -> barva.blue
            else -> rotace
        }
    }

    /**
     * Prepsani Funkce new(parametry Jedince): Vrati noveho jedince, s danymi parametry (v poli values)
     */
    override fun new(values: IntArray): Tvar {
        for (i in values.indices) values[i] = abs(values[i]) //Forcyklus, ktery udela vsechny prvky v poli values absolutni

        val barva = Color(values[4]%256,values[5]%256,values[6]%256) //Vytvoreni promnene barva a nastaveni ji na barvu z pole values
        val (w, h) = Pair(obrazek.width, obrazek.height) //Nevim
        return Tvar(x, y, values[2]%cellSize, values[2]%cellSize, Color.WHITE, rotace, platno) //Vraceni Ctverce/kruhu s danymi parametry + s nove vytvorenymi promnenimy
    }

    /**
     * Prepsani Funkce vykresli(): Vykresli jedince na platno
     */
    override fun vykresli() {
        platno.platno.icon = ImageIcon(obrazek)
    }

    /**
     * Prepsani Funkce prijmout(): Zmneni parametr parametru platno na "prazdny obrazek"
     */
    override fun prijmout() {
        platno.imitace = obrazek
        x += cellSize
        if (x == TABLE[0] * cellSize) {
            x =  0
            y += cellSize
        }
    }
}

/**
 * Vezme pixel (rgb hodnota) z obrazku a vrati jako [Color]
 */
fun DataBufferInt.color(index: Int): Color {
    return Color(getElem(index), true)
}

/** barva.podobnost(barva) == 0 */

/**
 * Color Funkce podobnost(ostatni): Vrati rozdil mezi dvoma barvama (na kterou se tahle funkce aplikuje a na tu v argumetech)
 */
fun Color.podobnost(other: Color): Double {
    return intArrayOf(red - other.red, green - other.green, blue - other.blue).velikost()
}

/** Vzdálenost bodu od počátku (ve 2D bod (0,0)).
 *  Podrobnější informace zde <A href="https://www.matematika.cz/euklidovy-vety">https://www.matematika.cz/euklidovy-vety</A>
 * */
fun IntArray.velikost(): Double {
    return map { it.toDouble().pow(2) }.sum().pow(.5)
}


/**
 * Trida Evoluce(populace) s typovym argumentem J: Trida kteravylepsuje obrazek
 */
class Evoluce<J: Jedinec<J>>(val populace: Array<Jedinec<J>>) {
    /**
     * Funkce spust(nepouzivana presnost): Spusti vylepsovani a jakmile je hotove, vrati vylepsenou promnenou populace
     */
    fun spust(presnost: Double): Array<Jedinec<J>> {
        var ix = 0    //Nastaveni promnene ix (index) na 0
        for (i in 0 until pocetTvaru) {    //forcyklus opakujici-se desettisic-krat
            populace[ix] = (0..20).map { populace[ix].new() }.minBy { it.skore }!! //Nevim
            var timeout = 0     //Nastaveni promnene timeout na 0
            var skore = populace[ix].skore //Nastaveni promnene skore na skore prvku z pole populace na indexu ix (index)
            while (timeout++ < 100) { //Opakuj dokud je timeout mensi nez 100
                val novy_jedinec = populace[ix].mutace() //Vytvoreni noveho jedince (obsahuje mutaci prvku z pole populace na indexu)

                if (populace[ix].skore+0 > novy_jedinec.skore) { //Pokud je skore noveho jedince lepsi nez skore prvku z pole populace na indexu ix tak:
                    populace[0].vykresli() //Vykresli prvek z pole populace na indexu 0
                    if (skore > novy_jedinec.skore) {  //Pokud je skore (definovano na radku 456) horsi nez skore noveho jedince tak:
                        skore = novy_jedinec.skore //Nastaveni skore (definovano na radku 456) na skore noveho jedince
                        timeout = 0 //Nastaveni promnene timeout na 0
                    }
                    populace[ix] = novy_jedinec //Nastaveni Stareho jedince na toho noveho
                }
//                Thread.sleep(20)
            }
//            if (populace[ix].skore) //Pokud je skore prvku z pole populace na indexu ix
            populace[ix].prijmout() //Nevim
            println("${populace[ix].skore}")  //Vypsani skore prvku z pole populace na indexu ix
//            Thread.sleep(2000)
        }
        return populace  //Vraceni pole populace
    }
}

//1..velikost {
//  Kreslit cary
//  Druhy stetec
//  Mutovat rotaci (zmnenit size (Jedinec), zmneneit get (Jedinec), zmnenit aribut tvaru a zmnenit new ())
//}