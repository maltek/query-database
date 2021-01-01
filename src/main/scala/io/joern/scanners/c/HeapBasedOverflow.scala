package io.joern.scanners.c

import io.joern.scanners._
import io.shiftleft.dataflowengineoss.queryengine.EngineContext
import io.shiftleft.semanticcpg.language._
import io.shiftleft.dataflowengineoss.language._

object HeapBasedOverflow extends QueryBundle {

  /**
    * Find calls to malloc where the first argument contains an arithmetic expression,
    * the allocated buffer flows into memcpy as the first argument, and the third
    * argument of that memcpy is unequal to the first argument of malloc. This is
    * an adaption of the old-joern query first shown at 31C3 that found a
    * buffer overflow in VLC's MP4 demuxer (CVE-2014-9626).
    * */
  @q
  def mallocMemcpyIntOverflow()(implicit context: EngineContext): Query = Query(
    name = "malloc-memcpy-int-overflow",
    author = Crew.fabs,
    title = "Dangerous copy-operation into heap-allocated buffer",
    description = "-",
    score = 4, { cpg =>
      val src = cpg.call("malloc").where(_.argument(1).arithmetics)
      cpg
        .call("memcpy")
        .filter { call =>
          call
            .argument(1)
            .reachableBy(src)
            .not(_.argument(1).codeExact(call.argument(3).code))
            .hasNext
        }
    }
  )

}
