package butterfly.requests.properties

import com.google.protobuf.ByteString

case class FetchProperties(nVal: Int = 3,
                           allowMult: Boolean = true,
                           basicQuorum: Boolean = false,
                           deletedVclock: Boolean = false,
                           head: Boolean = false,
                           ifModified: ByteString = ByteString.EMPTY,
                           notFoundOk: Boolean = false,
                           pr: Int = 1)
