import HelperUtils.{CreateLogger, ObtainConfigReference}
import Simulations.{SchedulingSimulations, VmAllocatPolicyRoundRobin}
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
import org.cloudbus.cloudsim.schedulers.cloudlet.{CloudletSchedulerSpaceShared, CloudletSchedulerTimeShared}
import org.cloudbus.cloudsim.schedulers.vm.{VmSchedulerSpaceShared, VmSchedulerTimeShared}
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyRoundRobin

object Simulation:
  val logger = CreateLogger(classOf[Simulation])

  @main def runSimulation =
    logger.info("Starting VM and Cloudlet Simulation cloud models...")
    logger.info("Space Shared VM and Cloudlet Scheduling simulation cloud model...")
    val spaceShared = new SchedulingSimulations("SpaceShared", new VmSchedulerSpaceShared(), new CloudletSchedulerSpaceShared())
    spaceShared.start()
    logger.info("Time Shared VM and Cloudlet Scheduling simulation cloud model...")
    val timeShared = new SchedulingSimulations("TimeShared", new VmSchedulerTimeShared(), new CloudletSchedulerTimeShared())
    timeShared.start()

    logger.info("Starting simulation for VM Allocation Policy Round Robin...")
    val roundRobin = new VmAllocatPolicyRoundRobin("RoundRobin", vmAllocation = new VmAllocationPolicyRoundRobin())
    roundRobin.start()

    logger.info("Finished cloud simulation...")


class Simulation