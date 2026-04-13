// Copyright (c) FIRST and other WPILib contributors. 
// Open Source Software; you can modify and/or share it under the terms of 
// the WPILib BSD license file in the root directory of this project. 

 
package frc.robot; 
 

import static edu.wpi.first.units.Units.*; 
 

import java.util.List; 
 

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType; 
import com.ctre.phoenix6.hardware.TalonFX; 
import com.ctre.phoenix6.swerve.SwerveRequest; 


import edu.wpi.first.math.geometry.Pose2d; 
import edu.wpi.first.math.geometry.Rotation2d; 
import edu.wpi.first.wpilibj2.command.Command; 
import edu.wpi.first.wpilibj2.command.Commands; 
import edu.wpi.first.wpilibj2.command.InstantCommand; 
import edu.wpi.first.wpilibj2.command.RunCommand; 
import edu.wpi.first.wpilibj2.command.button.CommandXboxController; 
import edu.wpi.first.wpilibj2.command.button.RobotModeTriggers; 
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction; 
import edu.wpi.first.math.trajectory.TrajectoryConfig; 
import edu.wpi.first.math.trajectory.Trajectory; 
import edu.wpi.first.math.trajectory.TrajectoryGenerator; 

 
import frc.robot.generated.TunerConstants; 
import frc.robot.subsystems.CommandSwerveDrivetrain; 


public class RobotContainer { 

    public static final boolean IS_WHEELS_ENABLED = true; // Set to TRUE to enable wheels. 
    private double MaxSpeed = 1.0 * TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed 
    private double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond); // 3/4 of a rotation per second max angular velocity 
 

    /* Setting up bindings for necessary control of the swerve drive platform */ 
    private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric() 
            .withDeadband(MaxSpeed * 0.1).withRotationalDeadband(MaxAngularRate * 0.1) // Add a 10% deadband 
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage); // Use open-loop control for drive motors 
    private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake(); 
    private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt(); 
 

    private final Telemetry logger = new Telemetry(MaxSpeed); 
 

    private final CommandXboxController joystick = new CommandXboxController(0); 
 

    public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain(); 
 

    // Old RobotContainer constructor without gamepiece handling motors. 

 
    // public RobotContainer() { 
    //     if(IS_WHEELS_ENABLED) 
    //     configureBindings(); 
    // } 

 
    // RobotContainer constructor with gamepiece handling motors passed in from Robot.java. 
    public RobotContainer( 
        // Gamepiece handling motors 
        TalonFX motorFeed, TalonFX motorColumn, TalonFX motorIntakeCollect, TalonFX motorIntakePivot, TalonFX motorShooterLeft, TalonFX motorShooterRight 
    ) { 
        // Initialize the motors for gamepiece handling 
        MotorFeed = motorFeed; 
        MotorColumn = motorColumn; 
        MotorIntakeCollect = motorIntakeCollect; 
        MotorIntakePivot = motorIntakePivot; 
        MotorShooterLeft = motorShooterLeft; 
        MotorShooterRight = motorShooterRight; 
 

        if(IS_WHEELS_ENABLED) 
            configureBindings(); 
    } 


    // Gamepiece handling motors passed from Robot.java 
    private final TalonFX MotorFeed, MotorColumn, MotorIntakeCollect, MotorIntakePivot, MotorShooterLeft, MotorShooterRight; 


    private void configureBindings() { 
        // Note that X is defined as forward according to WPILib convention, 
        // and Y is defined as to the left according to WPILib convention. 
        drivetrain.setDefaultCommand( 
            // Drivetrain will execute this command periodically 
            drivetrain.applyRequest(() -> 
                drive.withVelocityX(-joystick.getLeftY() * MaxSpeed) // Drive forward with negative Y (forward) 
                    .withVelocityY(-joystick.getLeftX() * MaxSpeed) // Drive left with negative X (left) 
                    .withRotationalRate(-joystick.getRightX() * MaxAngularRate) // Drive counterclockwise with negative X (left) 
            ) 
        ); 

        // Idle while the robot is disabled. This ensures the configured 
        // neutral mode is applied to the drive motors while disabled. 
        final var idle = new SwerveRequest.Idle(); 
        RobotModeTriggers.disabled().whileTrue( 
            drivetrain.applyRequest(() -> idle).ignoringDisable(true) 
        ); 

        joystick.a().whileTrue(drivetrain.applyRequest(() -> brake)); 
        joystick.b().whileTrue(drivetrain.applyRequest(() -> 
            point.withModuleDirection(new Rotation2d(-joystick.getLeftY(), -joystick.getLeftX())) 
        )); 

        // Run SysId routines when holding back/start and X/Y. 
        // Note that each routine should be run exactly once in a single log. 
        joystick.back().and(joystick.y()).whileTrue(drivetrain.sysIdDynamic(Direction.kForward)); 
        joystick.back().and(joystick.x()).whileTrue(drivetrain.sysIdDynamic(Direction.kReverse)); 
        joystick.start().and(joystick.y()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kForward)); 
        joystick.start().and(joystick.x()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kReverse)); 

        // Reset the field-centric heading on left bumper press. 
        joystick.start().onTrue(drivetrain.runOnce(drivetrain::seedFieldCentric)); 

        drivetrain.registerTelemetry(logger::telemeterize); 
    } 

    /** Get the Telemetry instance for logging motor and drivetrain data to SmartDashboard. */
    public Telemetry getTelemetry() {
        return logger;
    }

    public Command getAutonomousCommand() { 

        // Initial trajectory code. Commented out until a command can be built from it. 
        // TrajectoryConfig config = new TrajectoryConfig(2, 1) 
        //         .setKinematics(drivetrain.getKinematics()); 

        // Trajectory trajectory = TrajectoryGenerator.generateTrajectory( 
        //         List.of(new Pose2d(0, 0, new Rotation2d(0)), new Pose2d(-1, 0, new Rotation2d(0))), 
        //         config); 

        // Drive backwards auto. 
        final var idle = new SwerveRequest.Idle(); 

        Command backUpCommand = Commands.sequence( 
                drivetrain.runOnce(() -> drivetrain.seedFieldCentric(Rotation2d.kZero)), // Reseting your field orientation 

                drivetrain.applyRequest(() -> drive.withVelocityX(-0.5).withVelocityY(0).withRotationalRate(0)) // Create your drive command 
                        .withTimeout(.5), // End my drive command after 3 seconds 
                // Briefly set the drivetrain to idle so this step finishes and the sequence can continue
                drivetrain.applyRequest(() -> idle).withTimeout(2.5), // Stop my drivetrain from moving after the previous command ends.

                /*new InstantCommand(() -> {
                    MotorIntakePivot.set(0.5); // Move the intake pivot down to the ground 
                }),
                Commands.waitSeconds(0.2), // Wait for the intake pivot to move down*/

                new InstantCommand(() -> { 
                    MotorShooterLeft.set(-0.80); 
                    MotorShooterRight.set(-0.80); 
                }),
                Commands.waitSeconds(1.0),

                //Feeder and Column for 16 seconds, rest of auto
                new InstantCommand(() -> {
                    MotorFeed.set(0.5); 
                    MotorColumn.set(-0.5);
                }),
                Commands.waitSeconds(5),

                // Stop everything
                new InstantCommand(() -> {
                MotorFeed.set(0);
                MotorColumn.set(0);
                MotorShooterLeft.set(0);
                MotorShooterRight.set(0);
                })

                /*new RunCommand(() -> { 
                    MotorShooterLeft.set(-0.85); 
                    MotorShooterRight.set(-0.85); 
                }).withTimeout(1.0), // Run the shooter for 1 second initially and do not stop 

                new RunCommand(() -> { 
                    MotorFeed.set(0.5); 
                    MotorColumn.set(-0.5); 
                }).withTimeout(16), // Run the Feed/Column for 16 seconds / rest of auto 

                new InstantCommand(() -> { 
                    MotorFeed.set(0); 
                    MotorColumn.set(0); 
                    MotorShooterLeft.set(0); 
                    MotorShooterRight.set(0); 
                })*/  // Stops all gamepiece handling motors 
        ); 
        return backUpCommand; 
    } 
} 
