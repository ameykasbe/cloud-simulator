package Simulations
import HelperUtils.{DatacenterUtils, GetCloudletConfig, GetDatacenterConfig, GetHostConfig, GetVmConfig}
import com.typesafe.config.ConfigFactory
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.schedulers.cloudlet.{CloudletScheduler, CloudletSchedulerTimeShared}
import org.cloudbus.cloudsim.schedulers.vm.{VmScheduler, VmSchedulerTimeShared}
import org.cloudsimplus.builders.tables.CloudletsTableBuilder
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.funspec.AnyFunSpec

import scala.collection.JavaConverters.*

class CloudSimulatorTestSuite extends AnyFunSpec {
  val schedulerModel: String = "TimeShared"
  val vmScheduler = new VmSchedulerTimeShared()
  val cloudletScheduler = CloudletSchedulerTimeShared()

  val config = ConfigFactory.load(schedulerModel)

  describe("Config parameters"){
    it("should get the number of hosts") {
      assert(config.getInt("datacenter.numOfHosts") == 1)
    }
    it("should get the host ram") {
      assert(config.getInt("host.ram") == 10000)
    }
  }

  val cloudsim = new CloudSim

  describe("Cloud Sim Instance") {
    it("should not be null") {
      assert(cloudsim != null)
    }
  }


  val datacenterutil = new DatacenterUtils(schedulerModel, vmScheduler: VmScheduler, cloudletScheduler: CloudletScheduler)
  val datacenter = datacenterutil.createDatacenter(cloudsim)


 describe("Datacenter Instance") {
    it("should not be null") {
      assert(datacenter != null)
    }
  }

  val broker = new DatacenterBrokerSimple(cloudsim)


  describe("Broker Instance") {
    it("should not be null") {
      assert(broker != null)
    }
  }

  val vmList = datacenterutil.createVms()

  describe("VM List") {
    it("should not be null") {
      assert(vmList != null)
    }
    it("should be not be empty") {
      assert(vmList.length > 0)
    }
  }

  val cloudletList = datacenterutil.createCloudlets()


  describe("Cloudlet list") {
    it("should not be null") {
      assert(cloudletList != null)
    }
    it("should be not be empty") {
      assert(cloudletList.length > 0)
    }
  }

  broker.submitVmList(vmList.asJava)
  broker.submitCloudletList(cloudletList.asJava)

  cloudsim.start()
  describe("Simulation") {
    it("should not be running") {
      assert(cloudsim.isRunning() == false)
    }
  }
}
