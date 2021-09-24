package HelperUtils

import Simulations.BasicCloudSimPlusExample.config
import Simulations.SchedulingSimulations
import com.typesafe.config.{Config, ConfigFactory}
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.cloudlets.CloudletSimple
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.{Datacenter, DatacenterSimple}
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
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull

import scala.collection.JavaConverters.*

class DatacenterUtils (schedulerModel:String, vmScheduler: VmScheduler = new VmSchedulerSpaceShared(), cloudletScheduler: CloudletScheduler = new CloudletSchedulerTimeShared(), vmAllocation: VmAllocationPolicy = new VmAllocationPolicySimple()) {

//  If scheduling policy is not provided then the default ones are assumed

// Creating a logger instance to log events
  val logger = CreateLogger(classOf[DatacenterUtils])

  // Configuration
  logger.info(s"Parsing configurations from $schedulerModel.conf")
  // Configuration of a datacenter
  val datacenterConfig = new GetDatacenterConfig(schedulerModel)

  // Configuration of hosts in a datacenter
  val hostConfig = new GetHostConfig(schedulerModel)

  // Configuration of VMs to be created
  val vmConfig = new GetVmConfig(schedulerModel)

  // Configuration of cloudlets to be assigned to VMs to be created
  val cloudletConfig = new GetCloudletConfig(schedulerModel)

  logger.info(s"Configuration parsing completed from $schedulerModel.conf.")

  val numberOfHosts = datacenterConfig.numberOfHosts
  val numOfVms = datacenterConfig.numOfVms
  val numofCloudlets = datacenterConfig.numOfCloudlets

  logger.info("Datacenter configuration are as:")
  logger.info(s"Number of hosts: $numberOfHosts")
  logger.info(s"Number of VMs: $numOfVms")
  logger.info(s"Number of cloudlets: $numofCloudlets")


  /**
   * Creates a datacenter with configured hosts, VM Allocation Policy, architecture, OS, Cost.
   */
  def createDatacenter(cloudsim: CloudSim) : Datacenter = {
    val hostList = createHost(datacenterConfig.numberOfHosts)
    val dc = new DatacenterSimple(cloudsim, hostList.asJava, vmAllocation)
    dc.getCharacteristics().setArchitecture(datacenterConfig.arch).setOs(datacenterConfig.os).setCostPerBw(datacenterConfig.costPerBw).setCostPerStorage(datacenterConfig.costPerStorage).setCostPerMem(datacenterConfig.costPerMem)
    return dc

  }

  /**
   * Creates a list of hosts with configured pe, VM Scheduling policy, RAM, Bandwidth and storage.
   */
  def createHost(numberOfHosts: Int) = {
    val peList : List[Pe] = 1.to(hostConfig.numberOfPes).map(x=>new PeSimple(hostConfig.mipsCapacity)).toList
    1.to(numberOfHosts).map(x=>new HostSimple(hostConfig.ram, hostConfig.bw, hostConfig.storage, peList.asJava, true).setVmScheduler(vmScheduler.getClass().getDeclaredConstructor().newInstance())).toList
  }


  /**
   * Creates a list of VMs with configured MIPS Capacity, PEs, RAM, Bandwidth requested.
   */
  def createVms() = {
    val numOfVms = datacenterConfig.numOfVms
    1.to(numOfVms).map(x =>
      new VmSimple(vmConfig.mipsCapacity, vmConfig.numOfPes, cloudletScheduler.getClass().getDeclaredConstructor().newInstance()).setRam(vmConfig.ram).setBw(vmConfig.bw).setSize(vmConfig.size)
    ).toList
  }

  /**
   * Creates a list of cloudlets with with configured Utilization model, length, PEs requested.
   */
  def createCloudlets() = {
    val numOfCloudlets = datacenterConfig.numOfCloudlets
//    val utilizationModel = new UtilizationModelFull()
    val utilizationModel = cloudletConfig.utilizationModel
    1.to(numOfCloudlets).map(x=>new CloudletSimple(cloudletConfig.length, cloudletConfig.pesNumber, utilizationModel).setSizes(cloudletConfig.size)).toList
  }
}

