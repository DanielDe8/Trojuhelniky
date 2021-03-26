import kotlin.random.Random

/** Trida Evoluce(populace) s typovym argumentem J: Trida ktera vylepsuje obrazek */
class Evoluce<J : Jedinec<J>>(val populace: Array<Jedinec<J>>) {
    /** Spusti vylepsovani a jakmile je hotove, vrati vylepsenou promnenou populace */
    fun spust(presnost: Double): Array<Jedinec<J>> {
        var ix = 0    //Nastaveni promnene ix (index) na 0
        for (i in 0 until 10000) {    //forcyklus opakujici-se desettisic-krat
            populace[ix] = (0..20).map { populace[ix].new() }.minBy { it.kvalita }!! //Nevim
            var timeout = 0     //Nastaveni promnene timeout na 0
            var skore =
                populace[ix].kvalita //Nastaveni promnene skore na skore prvku z pole populace na indexu ix (index)
            while (timeout++ < 100) { //Opakuj dokud je timeout mensi nez 100
                val novy_jedinec =
                    populace[ix].mutace() //Vytvoreni noveho jedince (obsahuje mutaci prvku z pole populace na indexu)

                if (populace[ix].kvalita + 0 > novy_jedinec.kvalita) { //Pokud je skore noveho jedince lepsi nez skore prvku z pole populace na indexu ix tak:
                    populace[0].zobraz() //Vykresli prvek z pole populace na indexu 0
                    if (skore > novy_jedinec.kvalita) {  //Pokud je skore (definovano na radku 456) horsi nez skore noveho jedince tak:
                        skore = novy_jedinec.kvalita //Nastaveni skore (definovano na radku 456) na skore noveho jedince
                        timeout = 0 //Nastaveni promnene timeout na 0
                    }
                    populace[ix] = novy_jedinec //Nastaveni Stareho jedince na toho noveho
                }
//                Thread.sleep(20)
            }
            if (populace[ix].kvalita < 0) //Pokud je skore prvku z pole populace na indexu ix
                populace[ix].prijmout() //Nevim
            println("${populace[ix].kvalita}")  //Vypsani skore prvku z pole populace na indexu ix
//            Thread.sleep(2000)
        }
        return populace  //Vraceni pole populace
    }
}

/** Jedinec: Obsahuje nektere metody a atributy ruznych tvaru */
interface Jedinec<Jedinec> {
    /** Pocet atributu. */
    val size: Int
    /** Kvalita jedince */
    val kvalita: Double

    operator
    /** Vrati atribut jedince. */
    fun get(key: Int): Int

    /** Vytvori noveho jedince z pole atributu. */
    fun new(values: IntArray): Jedinec

    /** Zobrazi jedince. */
    fun zobraz()

    /** Prijme jedince. */
    fun prijmout()

    /**
     * Zmeni jeden z atributu danou funkci.
     *
     * @param key klic atributu, ktery bude zmenen
     * @param fn funkce ktera zmeni dany atribut
     */
    fun new(key: Int, fn: (Int) -> Int): Jedinec {
        val values = IntArray(size) { get(it) }
        values[key] = fn(values[key])
        return new(values)
    }

    /** Vrati uplne nahodneho jedince */
    fun new(): Jedinec {
        return new(IntArray(size) { Random.nextInt() })
    }

    /** Malicko zmeni jeden nahodny atribut. */
    fun mutace(): Jedinec {
        return new(Random.nextInt(size)) { it + Random.nextInt(-10, 10) }
    }

    /** Vyhodi TODO chybu */
    fun kombinace(otec: Jedinec): Jedinec {
        TODO("Krizeni neni naimplementovano.")
    }
}