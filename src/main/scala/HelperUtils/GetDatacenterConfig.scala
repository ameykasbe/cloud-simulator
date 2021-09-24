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

/**
 * A utility class to parse the configurations of datacenter
 */
class GetDatacenterConfig (schedulerModel: String) {
  val config = ConfigFactory.load(schedulerModel: String)
  val numberOfHosts = config.getInt("datacenter.numOfHosts")
  val numOfCloudlets = config.getInt("datacenter.numofCloudlets")
  val numOfVms = config.getInt("datacenter.numOfVms")
  val arch = config.getString("datacenter.arch")
  val os = config.getString("datacenter.os")
  val vmm = config.getString("datacenter.vmm")
  val costPerSec = config.getDouble("datacenter.costPerSec")
  val costPerBw = config.getDouble("datacenter.costPerBw")
  val costPerMem = config.getDouble("datacenter.costPerMem")
  val costPerStorage = config.getDouble("datacenter.costPerStorage")

}

