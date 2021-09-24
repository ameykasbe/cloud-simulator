package Simulations

import HelperUtils.{CreateLogger, DatacenterUtils, GetCloudletConfig, GetDatacenterConfig, GetHostConfig, GetVmConfig, ObtainConfigReference}
import Simulations.BasicCloudSimPlusExample.config
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


class CloudModelsSimulation()  {
    // Create a cloudsim object for simulation. Also creates the Cloud Information Service (CIS) entity.

  def start() = {
      // Create a cloudsim object for simulation. Also creates the Cloud Information Service (CIS) entity.
      val cloudsim = new CloudSim

      val iaasDatacenterutil = new DatacenterUtils("Iaas")
      val paasDatacenterutil = new DatacenterUtils("Paas")
      val saasDatacenterutil = new DatacenterUtils("Saas")

      val iaasDatacenter = iaasDatacenterutil.createDatacenter(cloudsim)
      val paasDatacenter = paasDatacenterutil.createDatacenter(cloudsim)
      val saasDatacenter = saasDatacenterutil.createDatacenter(cloudsim)

      val broker = new DatacenterBrokerSimple(cloudsim)

      val networkTopology = new BriteNetworkTopology("topology.brite")
      cloudsim.setNetworkTopology(networkTopology)

      networkTopology.mapNode(iaasDatacenter, 0)
      networkTopology.mapNode(paasDatacenter, 1)
      networkTopology.mapNode(saasDatacenter, 2)
      networkTopology.mapNode(broker, 3)

      val iaasVmList = iaasDatacenterutil.createVms()
      val paasVmList = paasDatacenterutil.createVms()
      val saasVmList = saasDatacenterutil.createVms()

      val allVmList = iaasVmList ::: paasVmList ::: saasVmList

      val iaasCloudletList = iaasDatacenterutil.createCloudlets()
      val paasCloudletList = paasDatacenterutil.createCloudlets()
      val saasCloudletList = saasDatacenterutil.createCloudlets()

      val allCloudletList = iaasCloudletList ::: paasCloudletList ::: saasCloudletList

      broker.submitVmList(allVmList.asJava)
      broker.submitCloudletList(allCloudletList.asJava)

      cloudsim.start()

      val finishedCloudlet : util.List[Cloudlet] = broker.getCloudletFinishedList()
      CloudletsTableBuilder(finishedCloudlet).build()

      val scalaCloudletList : List[Cloudlet] =  finishedCloudlet.asScala.toList.sorted
      scalaCloudletList.map(cloudlet => {
          val cloudletId = cloudlet.getId
          val cost = cloudlet.getTotalCost()
          val dc = cloudlet.getLastTriedDatacenter()
          println(s"Cost of cloudlet: $cloudletId on datacenter $dc is $cost")
      }
      )



  }

}

