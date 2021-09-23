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
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull


import scala.collection.JavaConverters.*

class DatacenterUtils (schedulerModel: String, vmScheduler: VmScheduler = new VmSchedulerSpaceShared(), cloudletScheduler: CloudletScheduler = new CloudletSchedulerTimeShared(), vmAllocation: VmAllocationPolicy = new VmAllocationPolicySimple()) {
//  If scheduling policy is not provided then the default ones are assumed
  val config = ConfigFactory.load(schedulerModel)

  val datacenterConfig = new GetDatacenterConfig(schedulerModel)
  val hostConfig = new GetHostConfig(schedulerModel)
  val vmConfig = new GetVmConfig(schedulerModel)
  val cloudletConfig = new GetCloudletConfig(schedulerModel)

  def createDatacenter(cloudsim: CloudSim) = {
    val hostList = createHost(datacenterConfig.numberOfHosts)
    new DatacenterSimple(cloudsim, hostList.asJava, vmAllocation).getCharacteristics().setArchitecture(datacenterConfig.arch).setOs(datacenterConfig.os).setCostPerBw(datacenterConfig.costPerBw).setCostPerStorage(datacenterConfig.costPerStorage).setCostPerMem(datacenterConfig.costPerMem)
  }

  def createHost(numberOfHosts: Int) = {
    val peList : List[Pe] = 1.to(hostConfig.numberOfPes).map(x=>new PeSimple(hostConfig.mipsCapacity)).toList
    1.to(numberOfHosts).map(x=>new HostSimple(hostConfig.ram, hostConfig.bw, hostConfig.storage, peList.asJava, true).setVmScheduler(vmScheduler.getClass().getDeclaredConstructor().newInstance())).toList
  }



  def createVms() = {
    val numOfVms = datacenterConfig.numOfVms
    1.to(numOfVms).map(x =>
      new VmSimple(vmConfig.mipsCapacity, vmConfig.numOfPes, cloudletScheduler.getClass().getDeclaredConstructor().newInstance()).setRam(vmConfig.ram).setBw(vmConfig.bw).setSize(vmConfig.size)
    ).toList
  }


  def createCloudlets() = {
    val numOfCloudlets = datacenterConfig.numOfCloudlets
//    val utilizationModel = new UtilizationModelFull()
    val utilizationModel = cloudletConfig.utilizationModel
    1.to(numOfCloudlets).map(x=>new CloudletSimple(cloudletConfig.length, cloudletConfig.pesNumber, utilizationModel).setSizes(cloudletConfig.size)).toList
  }
}

