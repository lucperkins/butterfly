package butterfly.core

import akka.io.LengthFieldFrame

trait RiakStages {
   val stages = new RiakPipeline >> new LengthFieldFrame(1024 * 1024 * 200, lengthIncludesHeader = false)
 }
