package Simulations

import HelperUtils.{CreateLogger, DatacenterUtils, GetCloudletConfig, GetDatacenterConfig, GetHostConfig, GetVmConfig}
import com.typesafe.config.ConfigFactory
import org.cloudbus.cloudsim.allocationpolicies.{VmAllocationPolicy, VmAllocationPolicyRoundRobin, VmAllocationPolicySimple}
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.cloudlets.{CloudletSimple, Cloudlet}
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
import org.cloudbus.cloudsim.network.topologies.BriteNetworkTopology
import java.util

import scala.collection.JavaConverters.*

/** A class to simulate cloud models Saas, Paas and Iaas in three different datacenters in a BRITE networks topology and execute cloudlets in them.
 */
class CloudModelsSimulation()  {
    // Creating a logger instance to log events
    val logger = CreateLogger(classOf[CloudModelsSimulation])

  def start() = {
      logger.info(s"Starting execution of cloud models simulation.")

      // Create a cloudsim instance for simulation. Also creates the Cloud Information Service (CIS) entity internally.
      val cloudsim = new CloudSim

      // Create utility instances for Saas, Paas and Iass to create cloud entities.
      val iaasDatacenterutil = new DatacenterUtils("Iaas")
      val paasDatacenterutil = new DatacenterUtils("Paas")
      val saasDatacenterutil = new DatacenterUtils("Saas")

      // Create three datacenters each for the three cloud models Saas, Paas and Iaas.
      val iaasDatacenter = iaasDatacenterutil.createDatacenter(cloudsim)
      val paasDatacenter = paasDatacenterutil.createDatacenter(cloudsim)
      val saasDatacenter = saasDatacenterutil.createDatacenter(cloudsim)

      // Create a broker instance that submits VM requests and cloudlets from customer to cloud service provider
      val broker = new DatacenterBrokerSimple(cloudsim)

      // Connect the three datacenters and broker in a BRITE Network topology using the topology.brite file.
      val networkTopology = new BriteNetworkTopology("topology.brite")
      cloudsim.setNetworkTopology(networkTopology)

      networkTopology.mapNode(iaasDatacenter, 0)
      networkTopology.mapNode(paasDatacenter, 1)
      networkTopology.mapNode(saasDatacenter, 2)
      networkTopology.mapNode(broker, 3)

      // Create a list of VMs for all datacenters
      logger.info("Creating VMs")
      val iaasVmList = iaasDatacenterutil.createVms()
      val paasVmList = paasDatacenterutil.createVms()
      val saasVmList = saasDatacenterutil.createVms()
      val allVmList = iaasVmList ::: paasVmList ::: saasVmList

      // Create a list of cloudlets for all datacenters
      logger.info("Creating cloudlets")
      val iaasCloudletList = iaasDatacenterutil.createCloudlets()
      val paasCloudletList = paasDatacenterutil.createCloudlets()
      val saasCloudletList = saasDatacenterutil.createCloudlets()
      val allCloudletList = iaasCloudletList ::: paasCloudletList ::: saasCloudletList

      // Submit VMs and cloudlets to broker
      logger.info("Submitting VMs and cloudlets to broker")
      broker.submitVmList(allVmList.asJava)
      broker.submitCloudletList(allCloudletList.asJava)

      // Start the simulation
      cloudsim.start()

      val finishedCloudlet : util.List[Cloudlet] = broker.getCloudletFinishedList()
      CloudletsTableBuilder(finishedCloudlet).build()

      val scalaCloudletList : List[Cloudlet] =  finishedCloudlet.asScala.toList.sorted
      scalaCloudletList.map(cloudlet => {
          val cloudletId = cloudlet.getId
          val cost = cloudlet.getTotalCost()
          val dc = cloudlet.getLastTriedDatacenter()
          logger.info(s"Cost of cloudlet: $cloudletId on datacenter $dc is $cost")
        }
      )
      logger.info(s"Finished execution of cloud models simulation.")
  }

}

