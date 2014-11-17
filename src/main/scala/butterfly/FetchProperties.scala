package butterfly

case class FetchProperties(basicQuorum: Boolean = false,
                           deletedVclock: Boolean = false,
                           head: Boolean = false,
                           notfoundOk: Boolean = true,
                           nVal: Int = 3,
                           pr: Int = 0,
                           r: Int = 2,
                           sloppyQuorum: Boolean = false,
                           timeout: Int = 0)