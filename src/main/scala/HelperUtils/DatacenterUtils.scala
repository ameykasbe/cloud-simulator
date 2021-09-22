package HelperUtils

import Simulations.BasicCloudSimPlusExample.config
import com.typesafe.config.{Config, ConfigFactory}
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.cloudlets.CloudletSimple
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.DatacenterSimple
import org.cloudbus.cloudsim.hosts.HostSimple
import org.cloudbus.cloudsim.resources.{Pe, PeSimple}
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic
import org.cloudbus.cloudsim.vms.VmSimple
import org.cloudsimplus.builders.tables.CloudletsTableBuilder
import org.cloudbus.cloudsim.schedulers.cloudlet.{CloudletSchedulerAbstract, CloudletSchedulerSpaceShared, CloudletSchedulerTimeShared}
import org.cloudbus.cloudsim.schedulers.vm
import org.cloudbus.cloudsim.schedulers.vm.{VmScheduler, VmSchedulerAbstract, VmSchedulerSpaceShared, VmSchedulerTimeShared}
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple

import scala.collection.JavaConverters.*

class DatacenterUtils (schedulerModel: String, vmScheduler: VmScheduler, cloudletScheduler: CloudletScheduler) {
  // Create a cloudsim object for simulation. Also creates the Cloud Information Service (CIS) entity.
  val config = ConfigFactory.load(schedulerModel)

  def createDatacenter(cloudsim: CloudSim) = {
    val numberOfHosts = config.getInt("datacenter.numOfHosts")
    val hostList = createHost(numberOfHosts)
    new DatacenterSimple(cloudsim, hostList.asJava, new VmAllocationPolicySimple())
  }

  def createHost(numberOfHosts: Int) = {
    val peList : List[Pe] = createPe()
    val ram = config.getInt("host.ram")
    val bw = config.getInt("host.bw")
    val storage = config.getInt("host.storage")
    1.to(numberOfHosts).map(x=>new HostSimple(ram, bw, storage, peList.asJava, true).setVmScheduler(vmScheduler)).toList

  }

  def createPe() = {
    val mipsCapacity = config.getInt("pe.mipsCapacity")
    val numberOfPes = config.getInt("host.numberOfPe")
    1.to(numberOfPes).map(x=>new PeSimple(mipsCapacity)).toList

  }

  def createVms() = {
    val mipsCapacity = config.getInt("vm.mipsCapacity")
    val ram = config.getInt("vm.ram")
    val bw = config.getInt("vm.bw")
    val size = config.getInt("vm.size")
    val numberOfPes = config.getInt("host.numberOfPe")
    List(new VmSimple(mipsCapacity, numberOfPes, cloudletScheduler).setRam(ram).setBw(bw).setSize(size))
  }

  def createCloudlets() = {
    val numOfCloudlets = config.getInt("datacenter.numofCloudlets")
    val length = config.getInt("cloudlet.length")
    val pesNumber = config.getInt("cloudlet.pesNumber")
    val utilizationModel = new UtilizationModelDynamic(config.getDouble("cloudlet.utilizationModel"))
    1.to(numOfCloudlets).map(x=>new CloudletSimple(length, pesNumber, utilizationModel)).toList
  }

}

