//import java.awt.Color
//import java.awt.Graphics2D
//import java.awt.image.BufferedImage
//import java.awt.image.DataBufferInt
//import javax.swing.Box
//import javax.swing.ImageIcon
//import javax.swing.JFrame
//import javax.swing.JLabel
//import kotlin.math.*
//import kotlin.random.Random
//
//
//typealias X = Double
//typealias XMin = Double
//typealias XMax = Double
//typealias Chyba = Double // Y
//
//fun nahodneCislo(x: X): X {
//    return Random.nextDouble(x - 3.0, x + 3.0)
//}
//
//fun rozpulCislo(cislo1: Double, cislo2: Double): Double {
//    return (abs(cislo1) + cislo2) / 2
//}
//
//fun main(args: Array<String>) {
//    // vytvoreni okna
//    val platno = JLabel()
//    val platno_original = JLabel()
//    val box = Box.createHorizontalBox().apply {
//        setSize(1000, 500)
//        add(platno)
//        add(platno_original)
//    }
//    JFrame().apply {
//        setSize(1000, 500)
//        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
//        isVisible = true
//        contentPane.add(box)
//    }
//    // obrazek ktery malujeme
//    val maketa = BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB)
//    val original = Obrazek(maketa, platno_original)
//
//    original.vykresli()
//
//    val evoluce = Evoluce(Array(1) { Obrazek(original.obrazek, platno) })
//    val populace = evoluce.spust(1e-4)
//    println("Reseni 0 = x*x + 1e-4 - 3: ${populace[0]}")
//}
//
///*
//          y
//          |    /
// chyba |  |   /
//       |  |  /
//       |  | /
//       |  |/_____________
//          ^   ^          x
//    hledame   nahodne reseni
// */
//fun vyresRovnici(chyba: (X) -> Chyba, presnost: Double, reseni: (X) -> X): ArrayList<X> {
//    var xy = arrayListOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
//    var ix = 0
//    while (abs(chyba(xy[ix])) > presnost) {
//        val nove_x = reseni(xy[ix])
//
//        if (abs(chyba(xy[ix])) > abs(chyba(nove_x))) {
////            println(x)
//            xy[ix] = nove_x
//            ix = Random.nextInt(0, xy.size)
//        }
//    }
//    return xy
//}
//
//typealias Skore = Double
//
//interface Jedinec<Jedinec> {
//    fun novy(): Jedinec
//    fun skore(): Double
//    fun mutace(): Jedinec
//    fun krizeni(otec: Jedinec): Jedinec
//    fun vykresli()
//    val x: Array<Int>
//    val y: Array<Int>
//    val
//}
//
//class Cislo(val dna: Double = Random.nextDouble(-1000.0, 1000.0)) : Jedinec<Cislo> {
//    override fun krizeni(otec: Cislo): Cislo {
//        return Cislo((abs(this.dna) + otec.dna) / 2)
//    }
//    override fun skore(): Double {
//        return 0.0
//    }
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
//
//    override val x: Array<Int> = arrayOf(0)
//    override val y: Array<Int> = arrayOf(0)
//}
//
//typealias Pozice = Int
//
//class Trojuhelnik(val x: Array<Int>, val y: Array<Int>, val barva: Color) {
//
//    constructor(maxX: Int, maxY: Int) : this (
//        x = arrayOf(Random.nextInt(0, maxX), Random.nextInt(0, maxX), Random.nextInt(0, maxX)),
//        y = arrayOf(Random.nextInt(0, maxY), Random.nextInt(0, maxY), Random.nextInt(0, maxY)),
//        barva = Color(Random.nextInt(0, 255), Random.nextInt(0, 255), Random.nextInt(0, 255)),
//    )
//
//    fun vykresli(platno: Graphics2D) {
//        platno.color = barva
//        platno.fillPolygon(x.toIntArray(), y.toIntArray(), 3)
//    }
//
//    fun mutace(): Trojuhelnik {
//        val x = x.clone()
//        val y = y.clone()
//        var b = barva
//        val znamenko = 2*Random.nextInt(0,2)-1
//        when (Random.nextInt(0, 3)){
//            0 -> x[Random.nextInt(0, x.size)] += znamenko * Random.nextInt(1, 3)
//            1 -> y[Random.nextInt(0, y.size)] += znamenko * Random.nextInt(1, 3)
//            else -> b = barva.mutace()
//        }
//        return Trojuhelnik(x, y, b)
//    }
//}
//
//class ARGB(val a: Int, val r: Int, val g: Int, val b: Int) {
//    constructor(argb: Int) : this (
//        argb shr 24 and 0xff,
//        argb shr 16 and 0xff,
//        argb shr 8 and 0xff,
//        argb and 0xff,
//    )
//
//    fun distance(other: ARGB): Double {
//        return ((0.0+r-other.r).pow(2) + (0.0+g-other.g).pow(2) + (0.0+b-other.b).pow(2)).pow(0.5)
//    }
//}
//
//fun Color.mutace(): Color {
//    return Color(Random.nextInt(0, 255), Random.nextInt(0, 255), Random.nextInt(0, 255))
//}
//
//class Obrazek(val original: BufferedImage, val platno: JLabel, val dna: Array<Trojuhelnik>) : Jedinec<Obrazek> {
//    val obrazek = BufferedImage(original.width, original.height, BufferedImage.TYPE_INT_ARGB)
//    init {
//        vykresliDNA()
//    }
//
//    fun vykresliDNA() {
//        val grafika = obrazek.createGraphics()
//        for (i in dna) {
//            i.vykresli(grafika)
//        }
//    }
//
//    constructor(original: BufferedImage, platno: JLabel): this(
//        original,
//        platno,
//        dna = Array(1) { Trojuhelnik(original.width, original.height) }
//    )
//
//
//    override fun skore(): Double {
//        var soucet = 0.0
//        val pocetPx = (obrazek.width * obrazek.height).toDouble()
//        for (i in 0 until obrazek.width * obrazek.height) {
//                val d0 = obrazek.raster.dataBuffer
//                val d1 = original.raster.dataBuffer
//                if (d0 is DataBufferInt) if (d1 is DataBufferInt) {
//                    soucet += ARGB(d0.getElem(i)).distance(ARGB(d1.getElem(i))) / pocetPx
//                }
//        }
//        return soucet
//    }
//
//    override fun mutace(): Obrazek {
//        val dna = this.dna.clone()
//
//
//        val i = Random.nextInt(dna.size)
//
//        dna[i] = dna[i].mutace()
//
//
//        return Obrazek(this.original, this.platno, dna)
//    }
//
//    override fun krizeni(otec: Obrazek): Obrazek {
//        for (i in 0..5) {
//            dna[Random.nextInt(0, dna.size)] = otec.dna[Random.nextInt(0, otec.dna.size)]
//        }
//
//        return Obrazek(this.original, this.platno, this.dna)
//    }
//
//    override fun vykresli() {
//        platno.icon = ImageIcon(obrazek)
//    }
//
//    override fun novy(): Obrazek {
//        return Obrazek(original, platno)
//    }
//
//    override val x: Array<Int> = arrayOf(0)
//    override val y: Array<Int> = arrayOf(0)
//}
//
//class Evoluce<J: Jedinec<J>>(val populace: Array<Jedinec<J>>) {
//    fun spust(presnost: Double): Array<Jedinec<J>> {
//        var ix = 0
//        var y = 0.0
//
//        while (abs(populace[0].skore()) > presnost) {
//            val ctverec: Array<Array<Int>> = arrayOf(
//                //x
//                arrayOf(
//                    populace[ix].dna[0].x[0],
//                    populace[ix].x[1],
//                    populace[ix].x[0],
//                    populace[ix].x[1]
//                ),
//                //y
//                arrayOf(
//                    populace[ix].x[0],
//                    populace[ix].x[0],
//                    populace[ix].x[2],
//                    populace[ix].x[2]
//                )
//            )
//            val pocetPx = ((ctverec[0][2] - ctverec[0][0]) * (ctverec[1][2] - ctverec[1][0])).toDouble()
//            for (i in 0 until (ctverec[0][2] - ctverec[0][0]) * (ctverec[1][2] - ctverec[1][0])) {
//                y += ARGB(i).distance(ARGB(i)) / pocetPx
//            }
//
//            val novy_jedinec: Jedinec<J> = populace[ix].mutace()
//
//            populace[0].vykresli()
//
//            println("${populace[ix].skore()}, ${novy_jedinec.skore()}")
//            if (1.00000*abs(populace[ix].skore()) > abs(novy_jedinec.skore())) {
//                populace[ix] = novy_jedinec
//                ix = Random.nextInt(0, populace.size)
//            }
//
//            if (y >= 5.0) {
//                populace[ix].novy()
//            }
//        }
//        return populace
//    }
//}
//
////1.* vyresit mizejici trojuhleniky
////2. mutace barvy - posunout r g b o +- 10
////3. zhodnotit posun trojuhelniku
////4. promyslet rychlejsi mutaci
//
//
////0=x*x+1-3
//
//
 /**
 *    val co_se_stalo = if (x > byvale_x) {
 *        "x+"
 *    } else if (x < byvale_x) {
 *        "x-"
 *    } else if (y > byvale_y) {
 *        "y+"
 *  } else if (y < byvale_y) {
 *      "y-"
 *  }
 *
 *  if (byvala_chyba > aktualni_chyba) {
 *      return "dokonale"
 *  } else {
 *      if (co_se_stalo = x+) {
 *          return "moc jsi zvecil x."
 *          zmensiX()
 *      } else if (co_se_stalo = x-) {
 *          return "moc jsi zmensil x."
 *          zvetsX()
 *      } else if (co_se_stalo = y+) {
 *          return "moc jsi zvecil y."
 *          zmensiY()
 *      } else if (co_se_stalo = y-) {
 *          return "moc jsi zmensil y."
 *          zvetsY()
 *      }
 *  }
 **/