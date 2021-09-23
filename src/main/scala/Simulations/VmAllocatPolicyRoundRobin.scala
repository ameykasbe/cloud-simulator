package Simulations

import HelperUtils.{CreateLogger, DatacenterUtils, ObtainConfigReference}
import Simulations.BasicCloudSimPlusExample.config
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

class VmAllocatPolicyRoundRobin(schedulerModel: String, vmAllocation: VmAllocationPolicy)  {
    // Create a cloudsim object for simulation. Also creates the Cloud Information Service (CIS) entity.

    val config = ConfigFactory.load(schedulerModel)

  def start() = {
      // Create a cloudsim object for simulation. Also creates the Cloud Information Service (CIS) entity.
      val cloudsim = new CloudSim
      println(s"VmAllocatPolicyRoundRobin - $schedulerModel")
      val datacenterutil = new DatacenterUtils(schedulerModel: String, vmAllocation = vmAllocation: VmAllocationPolicy)
      val datacenter = datacenterutil.createDatacenter(cloudsim)

      val broker = new DatacenterBrokerSimple(cloudsim)

      val vmList = datacenterutil.createVms()

      val cloudletList = datacenterutil.createCloudlets()

      broker.submitVmList(vmList.asJava)
      broker.submitCloudletList(cloudletList.asJava)

      cloudsim.start()

      val finishedCloudlet = broker.getCloudletFinishedList()
      CloudletsTableBuilder(finishedCloudlet).build()
  }

}

