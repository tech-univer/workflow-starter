package mcb.demo.coinflip.command

class RequestCoinFlip(val id: String,
                      val denomination: Int,
                      val currency: String,
                      val numberOfFlips: Int)
