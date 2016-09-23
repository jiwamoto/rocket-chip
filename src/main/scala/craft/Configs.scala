package craft

import rocketchip._
import cde.{Parameters, Config, CDEMatchError}
import uncore.tilelink._
import uncore.coherence.{MICoherence, NullRepresentation}
import uncore.agents.CacheBlockBytes
import junctions.MIFDataBeats

class WithCraft extends Config(
  (pname, site, here) => pname match {
    case TLKey("XBar") => TileLinkParameters(
      coherencePolicy = new MICoherence(new NullRepresentation(1)),
      nManagers = site(InPorts),
      nCachingClients = 0,
      nCachelessClients = site(OutPorts),
      maxClientXacts = 4,
      maxClientsPerPort = site(InPorts),
      maxManagerXacts = 1,
      dataBeats = site(MIFDataBeats),
      dataBits = site(CacheBlockBytes) * 8)
    case TLId => "XBar"
    case InPorts => 2
    case OutPorts => 2
    case _ => throw new CDEMatchError
  })

class CraftConfig extends Config(new WithCraft ++ new BaseConfig)
