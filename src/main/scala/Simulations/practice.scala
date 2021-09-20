package Simulations

import HelperUtils.{CreateLogger, ObtainConfigReference}
import Simulations.BasicCloudSimPlusExample.config
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.cloudlets.CloudletSimple
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.DatacenterSimple
import org.cloudbus.cloudsim.hosts.HostSimple
import org.cloudbus.cloudsim.resources.{Pe, PeSimple}
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic
import org.cloudbus.cloudsim.vms.VmSimple
import org.cloudsimplus.builders.tables.CloudletsTableBuilder

import collection.JavaConverters.*

import com.typesafe.config._

class Practice

object Practice:
  val config = ObtainConfigReference("cloudSimulator") match {
    case Some(value) => value
    case None => throw new RuntimeException("Cannot obtain a reference to the config data.")
  }

  val logger = CreateLogger(classOf[BasicCloudSimPlusExample])

  def Start() = {
    // Create a cloudsim object for simulation. Also creates the Cloud Information Service (CIS) entity.
    val props = ConfigFactory.load("TimeShared")
    val ram = props.getInt("datacenter.host.ram")
    println(s"Properties =  $props")
    println(s"Ram =  $ram")



    val cloudsim = new CloudSim

    val (datacenter, peList) = createDatacenter(cloudsim)

    val broker = new DatacenterBrokerSimple(cloudsim)

    val vmList = createVms(peList)

    val cloudletList = createCloudlets()

    broker.submitVmList(vmList.asJava)
    broker.submitCloudletList(cloudletList.asJava)

    cloudsim.start()

    val finishedCloudlet = broker.getCloudletFinishedList()
    CloudletsTableBuilder(finishedCloudlet).build()
  }

  def createDatacenter(cloudsim: CloudSim) = {
    val (hostList, peList) = createHost()
    val datacenter = new DatacenterSimple(cloudsim, hostList.asJava)
    (datacenter, peList)
  }

  def createHost() = {
    val peList : List[Pe] = createPe()
    val host = new HostSimple(10000, 100000, 100000, peList.asJava)
    val hostList = List(host)
    (hostList, peList)
  }


  def createPe() = {
    val peList : List[Pe] = List(new PeSimple(1000))
    peList
  }


  def createVms(peList: List[Pe]) = {
    val vmList = List(new VmSimple(1000, peList.length).setRam(1000).setBw(1000).setSize(1000))
    vmList
  }

  def createCloudlets() = {
    val utilizationModel = new UtilizationModelDynamic(0.5)
    val cloudlet = new CloudletSimple(10000, 2, utilizationModel)
    List(cloudlet)
  }
