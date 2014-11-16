package butterfly

case class FetchProperties(allowMult: Boolean = true,
                           deletedVclock: Boolean = false,
                           notfoundOk: Boolean = true,
                           nVal: Int = 3,
                           p: Int,
                           pr: Int,
                           r: Int,
                           sloppyQuorum: Boolean = false,
                           timeout: Int = 0)
