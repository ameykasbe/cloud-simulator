package Simulations

import HelperUtils.{CreateLogger, DatacenterUtils, GetCloudletConfig, GetDatacenterConfig, GetHostConfig, GetVmConfig}
import com.typesafe.config.ConfigFactory
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletSimple}
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

/** A class to simulate Time shared and Space shared VM and Cloudlet scheduling policies.
 *
 *  @param schedulerModel a string that accepts the scheduling policy. E.g. TimeShared, SpaceShared.
 *  @param vmScheduler the vm scheduling policy
 *  @param cloudletScheduler the cloudlet scheduling policy
 */
class SchedulingSimulations(schedulerModel: String, vmScheduler: VmScheduler, cloudletScheduler: CloudletScheduler)  {

    // Creating a logger instance to log events
    val logger = CreateLogger(classOf[SchedulingSimulations])

    def start() = {
      // Create a cloudsim instance for simulation. Also creates the Cloud Information Service (CIS) entity internally.
      val cloudsim = new CloudSim

      // Create a utility instance to create cloud entities.
      val datacenterutil = new DatacenterUtils(schedulerModel, vmScheduler: VmScheduler, cloudletScheduler: CloudletScheduler)

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

      val scalaCloudletList : List[Cloudlet] =  finishedCloudlet.asScala.toList
      scalaCloudletList.map(cloudlet => {
        val cloudletId = cloudlet.getId
        val cost = cloudlet.getTotalCost()
        val dc = cloudlet.getLastTriedDatacenter()
        logger.info(s"Cost of cloudlet: $cloudletId on datacenter $dc is $cost")
      }
      )
      logger.info(s"Finished execution of cloud models simulation.")

      logger.info(s"Finished execution of $schedulerModel VM and cloudlet scheduling policy. \n\n\n")
  }

}

