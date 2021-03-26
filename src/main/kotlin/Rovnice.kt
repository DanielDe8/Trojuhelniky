import kotlin.math.abs
import kotlin.random.Random

typealias X = Double  // Aliasy typu z vyres rovnici
typealias Chyba = Double // Y

/** Vrati polovinu dvou cisel. */
fun rozpulCislo(cislo1: Double, cislo2: Double): Double {
    return (abs(cislo1) + cislo2) / 2
}


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

/** Nepouzivana funkce vyresRovnici() */
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


class Cislo(val dna: Double = Random.nextDouble(-1000.0, 1000.0)) : Jedinec<Cislo> {

    override val size = 1
    override val kvalita = 0.0

    override fun mutace(): Cislo {
        return Cislo(Random.nextDouble(dna - 3.0, dna + 3.0))
    }

    override fun toString(): String {
        return "$dna"
    }

    override fun zobraz() {
        println(dna)
    }

    override fun get(key: Int): Int {
        TODO("Not yet implemented")
    }

    override fun new(values: IntArray): Cislo {
        TODO("Not yet implemented")
    }

    override fun prijmout() {
        TODO("Not yet implemented")
    }
}
