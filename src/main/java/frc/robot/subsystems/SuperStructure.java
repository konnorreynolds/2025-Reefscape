package frc.robot.subsystems;

import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;

import java.util.Set;


public class SuperStructure extends SubsystemBase {

    private final ArmSubsystem arm;
    private final ElevatorSubsystem elevator;
    private final IntakeShooterSubsystem intakeShooter;
    private final SwerveSubsystem drivebase;
    private final PoseSelector poseSelector;

    private boolean isOperatorManualBoolean = true;
    private final Trigger isOperatorManual = new Trigger(() -> isOperatorManualBoolean);

    /**
     * Initialize the robot control {@link SuperStructure}
     */
    public SuperStructure(
            ArmSubsystem arm,
            ElevatorSubsystem elevator,
            IntakeShooterSubsystem intakeShooter,
            SwerveSubsystem drivebase,
            PoseSelector poseSelector) {

        this.arm = arm;
        this.elevator = elevator;
        this.intakeShooter = intakeShooter;
        this.drivebase = drivebase;
        this.poseSelector = poseSelector;

        updateAutoStow();

        SmartDashboard.putData("Side View", Constants.sideRobotView);
    }

    public void updateAutoStow() {
        arm.setArmStow(isOperatorManualBoolean);
        elevator.setElevatorStow(isOperatorManualBoolean);
    }

    private Command driveToReef() {
        return Commands.defer(() -> drivebase.driveToPose(
                poseSelector::flippedReefPose,
                .8), Set.of(drivebase));
    }

    private Trigger atReef() {
        return new Trigger(() ->
                drivebase.poseIsNear(
                        poseSelector.flippedReefPose(),
                        drivebase.getPose(),
                        Constants.DrivebaseConstants.kAtReefTolerance
                ));
    }

    private Command driveToStation() {
        return Commands.defer(() -> drivebase.driveToPose(
                poseSelector::flippedStationPose,
                1), Set.of(drivebase));
    }

    public Command getCoral() {
        return
                driveToStation()
                        .alongWith(structureToStation())
                        .alongWith(intakeShooter.intake())
                        .until(intakeShooter.getCoralTrigger())
                        .withTimeout(3)
                        .andThen(drivebase.driveBackwards()
                                .alongWith(forceStow())
                                .withTimeout(.5));
    }

    public Command scoreL1() {
        return
                driveToReef()
                        .alongWith(structureToL1())
                        .until(atReef()
                                .and(structuresAtL1()))
                        .andThen(
                                drivebase.stopDrive()
                                        .withTimeout(.1))
                        .andThen(intakeShooter.shoot()
                                .alongWith(structureToL1())
                                .withTimeout(2)
                                .until(intakeShooter.getCoralTrigger().negate())
                        ).andThen(
                                drivebase.driveBackwards()
                                        .alongWith(forceStow())
                                        .withTimeout(0.5)
                        );
    }
    public Command scoreL2() {
        return
                driveToReef()
                        .alongWith(structureToL2())
                        .until(atReef()
                                .and(structuresAtL2()))
                        .andThen(
                                drivebase.stopDrive()
                                        .withTimeout(.1))
                        .andThen(intakeShooter.shoot()
                                .alongWith(structureToL2())
                                .withTimeout(2)
                                .until(intakeShooter.getCoralTrigger().negate())
                        ).andThen(
                                drivebase.driveBackwards()
                                        .alongWith(forceStow())
                                        .withTimeout(0.5)
                        );
    }
    public Command scoreL3() {
        return
                driveToReef()
                        .alongWith(structureToL3())
                        .until(atReef()
                                .and(structuresAtL3()))
                        .andThen(
                                drivebase.stopDrive()
                                        .withTimeout(.1))
                        .andThen(intakeShooter.shoot()
                                .alongWith(structureToL3())
                                .withTimeout(2)
                                .until(intakeShooter.getCoralTrigger().negate())
                        ).andThen(
                                drivebase.driveBackwards()
                                        .alongWith(forceStow())
                                        .withTimeout(0.5)
                        );
    }
    public Command scoreL4() {
        return
                driveToReef()
                        .alongWith(structureToL4())
                        .until(atReef()
                                .and(structuresAtL4()))
                        .andThen(
                                drivebase.stopDrive()
                                        .withTimeout(.1))
                        .andThen(intakeShooter.shoot()
                                .alongWith(structureToL4())
                                .withTimeout(2)
                                .until(intakeShooter.getCoralTrigger().negate())
                        ).andThen(
                                drivebase.driveBackwards()
                                        .alongWith(forceStow())
                                        .withTimeout(0.5)
                        );
    }


    public Command structureToL1() {
        return
                arm.armToL1()
                        .alongWith(elevator.elevatorToL1());
    }

    public Command structureToL2() {
        return
                arm.armToL2()
                        .alongWith(elevator.elevatorToL2());
    }

    public Command structureToL3() {
        return
                arm.armToL3()
                        .alongWith(elevator.elevatorToL3());
    }

    public Command structureToL4() {
        return
                arm.armToL4()
                        .alongWith(elevator.elevatorToL4());
    }

    public Command structureToStation() {
        return
                arm.armToStation()
                        .alongWith(elevator.elevatorToStation());
    }

    public Command structureToDealgaeHigh() {
        return
                arm.armToDealgaeHigh()
                        .alongWith(elevator.elevatorToDealgaeHigh());
    }

    public Command structureToDealgaeLow() {
        return
                arm.armToDealgaeLow()
                        .alongWith(elevator.elevatorToDealgaeLow());
    }

    public Command forceStow() {
        return
                arm.armToStow()
                        .alongWith(elevator.elevatorToStow());
    }

    public Command toggleOperatorControls() {
        return runOnce(() -> {
            isOperatorManualBoolean = !isOperatorManualBoolean;
        });
    }

    public Command enablePID() {
        return runOnce(() -> {
            isOperatorManualBoolean = false;
        });
    }

    public Command disablePID() {
        return runOnce(() -> {
            isOperatorManualBoolean = true;
        });
    }

    public Command updateStowCommand() {
        return runOnce(this::updateAutoStow);
    }

    public Trigger isOperatorManual() {
        return isOperatorManual;
    }

    private Trigger atStation() {
        return new Trigger(() ->
                drivebase.poseIsNear(
                        poseSelector.flippedStationPose(),
                        drivebase.getPose(),
                        Constants.DrivebaseConstants.kAtStationTolerance
                ));
    }

    public Trigger structuresAtL1() {
        return
                elevator.atHeight(
                                Units.inchesToMeters(Constants.ElevatorConstants.kL1Setpoint),
                                Units.inchesToMeters(Constants.ElevatorConstants.kAutoScoreToleranceInches))
                        .and(arm.atAngle(
                                Constants.ArmConstants.kL1Setpoint,
                                Constants.ArmConstants.kAutoScoreToleranceDegrees
                        ));
    }
    public Trigger structuresAtL2() {
        return
                elevator.atHeight(
                                Units.inchesToMeters(Constants.ElevatorConstants.kL2Setpoint),
                                Units.inchesToMeters(Constants.ElevatorConstants.kAutoScoreToleranceInches))
                        .and(arm.atAngle(
                                Constants.ArmConstants.kL2Setpoint,
                                Constants.ArmConstants.kAutoScoreToleranceDegrees
                        ));
    }
    public Trigger structuresAtL3() {
        return
                elevator.atHeight(
                                Units.inchesToMeters(Constants.ElevatorConstants.kL3Setpoint),
                                Units.inchesToMeters(Constants.ElevatorConstants.kAutoScoreToleranceInches))
                        .and(arm.atAngle(
                                Constants.ArmConstants.kL3Setpoint,
                                Constants.ArmConstants.kAutoScoreToleranceDegrees
                        ));
    }
    public Trigger structuresAtL4() {
        return
                elevator.atHeight(
                                Units.inchesToMeters(Constants.ElevatorConstants.kL4Setpoint),
                                Units.inchesToMeters(Constants.ElevatorConstants.kAutoScoreToleranceInches))
                        .and(arm.atAngle(
                                Constants.ArmConstants.kL4Setpoint,
                                Constants.ArmConstants.kAutoScoreToleranceDegrees
                        ));
    }


    /**
     * A command to stop all manipulator motors.
     *
     * @return a command to stop all manipulator motors.
     */
    public Command stopAllManipulators() {
        return run(() -> {
            intakeShooter.stopIntakeShooter();
            elevator.stopElevator();
            arm.stopArm();
        });
    }
}
