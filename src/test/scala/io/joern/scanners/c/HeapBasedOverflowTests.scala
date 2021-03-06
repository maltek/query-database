package io.joern.scanners.c

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.console.scan._

class HeapBasedOverflowTests extends Suite {

  override val code: String =
    """
      int vulnerable(size_t len, char *src) {
        char *dst = malloc(len + 8);
        memcpy(dst, src, len + 7);
      }

      int non_vulnerable(size_t len, char *src) {
       char *dst = malloc(len + 8);
       memcpy(dst, src,len + 8);
      }

      int non_vulnerable2(size_t len, char *src) {
       char *dst = malloc( some_size );
       assert(dst);
       memcpy(dst, src, some_size );
      }
    """

  "find calls to malloc/memcpy system with different expressions in arguments" in {
    val x = HeapBasedOverflow.mallocMemcpyIntOverflow()
    x(cpg).map(_.evidence) match {
      case List(List(expr: nodes.Expression)) =>
        expr.code shouldBe "memcpy(dst, src, len + 7)"
      case _ => fail
    }
  }

}
