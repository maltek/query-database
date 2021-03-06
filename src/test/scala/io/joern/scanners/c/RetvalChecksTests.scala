package io.joern.scanners.c

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.console.scan._
import io.shiftleft.semanticcpg.language._

class RetvalChecksTests extends Suite {

  override val code =
    """
      |
      |void unchecked_read() {
      |  read(fd, buf, sizeof(buf));
      |}
      |
      |void checked_after_assignment() {
      | int nbytes = read(fd, buf, sizeof(buf));
      | if( nbytes != sizeof(buf)) {
      |
      | }
      |}
      |
      |void checks_something_else() {
      |
      | int nbytes = read(fd, buf, sizeof(buf));
      | if( foo != sizeof(buf)) {
      |
      | }
      |}
      |
      |void immediately_checked() {
      | if ( (read(fd, buf, sizeof(buf))) != sizeof(buf)) {
      |
      | }
      |}
      |
      |
      |""".stripMargin

  "should find unchecked read and not flag others" in {
    val results =
      RetvalChecks.uncheckedReadRecvMalloc()(cpg).flatMap(_.evidence).collect {
        case call: nodes.Call => call.method.name
      }
    results.toSet shouldBe Set("unchecked_read", "checks_something_else")
  }

}
