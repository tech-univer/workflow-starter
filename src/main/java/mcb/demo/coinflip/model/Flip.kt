package mcb.demo.coinflip.model

class Flip(val requestId: String, val id: String, val outcome: Outcome, val currency: String, val denomination: Int) {
    override fun toString(): String {
        return """Flip(requestId='$requestId',
            |id='$id',
            |outcome=$outcome,
            |currency='$currency',
            |denomination=$denomination)""".trimMargin()
    }
}

