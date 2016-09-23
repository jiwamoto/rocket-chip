package craft

import Chisel._
import cde.Parameters
import junctions._
import rocketchip._
import uncore.tilelink.{TLKey, TLId}

class TestHarness(topParams: Parameters) extends Module {
  implicit val p = topParams

  val io = new Bundle {
    val success = Bool(OUTPUT)
  }

  val tlConfig = p(TLKey(p(TLId)))
  println(s"TL DataBits: ${tlConfig.dataBitsPerBeat}")
  println(s"TL DataBeats: ${tlConfig.dataBeats}")

  val dut = Module(new CraftXBar(p))

  val inPorts = p(InPorts)
  val outPorts = p(OutPorts)
  val memSize = p(ExtMemSize)

  val finished = Wire(Vec(inPorts, Bool()))

  for (i <- 0 until inPorts) {
    val start = i * (memSize / inPorts)
    val driver = Module(new NastiDriver(p(MIFDataBits), p(MIFDataBeats), 4, start))
    dut.io.in(i) <> driver.io.nasti
    finished(i) := driver.io.finished
    driver.io.start := Bool(true)
  }

  for (i <- 0 until outPorts) {
    val mem = Module(new SimAXIMem(memSize / outPorts))
    mem.io.axi <> dut.io.out(i)
  }

  io.success := finished.reduce(_ || _)
}
