package Simulations

import HelperUtils.{CreateLogger, DatacenterUtils, GetCloudletConfig, GetDatacenterConfig, GetHostConfig, GetVmConfig}
import com.typesafe.config.ConfigFactory
import org.cloudbus.cloudsim.allocationpolicies.{VmAllocationPolicy, VmAllocationPolicyRoundRobin, VmAllocationPolicySimple}
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

/** A class to simulate VM Allocation policy Round Robin.
 *
 *  @param schedulerModel a string that accepts the scheduling policy. E.g. TimeShared, SpaceShared.
 *  @param vmAllocation the vm allocation policy. E.g. Round Robin.
 */
class VmAllocatPolicyRoundRobin(schedulerModel: String, vmAllocation: VmAllocationPolicy)  {

    // Creating a logger instance to log events
    val logger = CreateLogger(classOf[SchedulingSimulations])


  def start() = {
      // Create a cloudsim instance for simulation. Also creates the Cloud Information Service (CIS) entity internally.
      val cloudsim = new CloudSim

      // Create a utility instance to create cloud entities.
      val datacenterutil = new DatacenterUtils(schedulerModel, vmAllocation = vmAllocation: VmAllocationPolicy)

      // Create a datacenter instance
      val datacenter = datacenterutil.createDatacenter(cloudsim)

      // Create a broker instance that submits VM requests and cloudlets from customer to cloud service provider
      val broker = new DatacenterBrokerSimple(cloudsim)

      // Create a list of VMs
      logger.info("Creating VMs")
      val vmList = datacenterutil.createVms()

      // Create a list of cloudlets
      logger.info("Creating cloudlets")
      val cloudletList = datacenterutil.createCloudlets()

      // Submit VMs and cloudlets to broker
      logger.info("Submitting VMs and cloudlets to broker")
      broker.submitVmList(vmList.asJava)
      broker.submitCloudletList(cloudletList.asJava)

      // Start the simulation
      cloudsim.start()

      // Build the simulation table
      val finishedCloudlet = broker.getCloudletFinishedList()
      CloudletsTableBuilder(finishedCloudlet).build()
      logger.info(s"Finished execution of $schedulerModel VM ALlocation Policy.  \n\n\n")
  }

}

