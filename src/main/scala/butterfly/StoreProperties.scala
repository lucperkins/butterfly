package butterfly

case class StoreProperties(asIs: Boolean = false,
                           dw: Int = 2,
                           ifNoneMatch: Boolean = false,
                           ifNotModified: Boolean = false,
                           nVal: Int = 3,
                           pw: Int = 2,
                           returnBody: Boolean = true,
                           returnHead: Boolean = false,
                           sloppyQuorum: Boolean = false,
                           timeout: Int = 0,
                           w: Int = 2)
