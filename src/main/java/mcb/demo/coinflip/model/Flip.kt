package mcb.demo.coinflip.model

class Flip(val requestId: String, val id: String, val outcome: Outcome, val currency: String, val denomination: Int) {
    override fun toString(): String {
        return """Flip(requestId='$requestId',
            |id='$id',
            |outcome=$outcome,
            |currency='$currency',
            |denomination=$denomination)""".trimMargin()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Flip

        if (requestId != other.requestId) return false
        if (id != other.id) return false
        if (outcome != other.outcome) return false
        if (currency != other.currency) return false
        if (denomination != other.denomination) return false

        return true
    }

    override fun hashCode(): Int {
        var result = requestId.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + outcome.hashCode()
        result = 31 * result + currency.hashCode()
        result = 31 * result + denomination
        return result
    }
}

