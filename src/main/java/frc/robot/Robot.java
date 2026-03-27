// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix6.HootAutoReplay;

import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.hardware.TalonFX;  
import com.ctre.phoenix6.configs.TalonFXConfiguration; //motion magic
import com.ctre.phoenix6.configs.FeedbackConfigs;
import com.ctre.phoenix6.configs.MotionMagicConfigs; //motion magic
import com.ctre.phoenix6.controls.MotionMagicVoltage; //motion magic
import com.ctre.phoenix6.controls.MotionMagicDutyCycle; //motion magic
import com.ctre.phoenix6.configs.Slot0Configs;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.motorcontrol.Talon;
import edu.wpi.first.units.measure.Angle;
import java.time.Instant;
//import edu.wpi.first.wpilibj.Joystick; //Not used. Using Xbox instead


public class Robot extends TimedRobot {
    private Command m_autonomousCommand;

    private final RobotContainer m_robotContainer;

      //constants
  public static final int MOTOR_FRONT_LEFT_STEER_ID = 1;
  public static final int MOTOR_FRONT_LEFT_DRIVE_ID = 2;
  public static final int MOTOR_FRONT_RIGHT_STEER_ID = 3;
  public static final int MOTOR_FRONT_RIGHT_DRIVE_ID = 4;
  public static final int MOTOR_BACK_LEFT_STEER_ID = 5;
  public static final int MOTOR_BACK_LEFT_DRIVE_ID = 6;
  public static final int MOTOR_BACK_RIGHT_STEER_ID = 7;
  public static final int MOTOR_BACK_RIGHT_DRIVE_ID = 8;
  public static final int ENCODER_FRONT_LEFT_ID = 9;
  public static final int ENCODER_FRONT_RIGHT_ID = 10;
  public static final int ENCODER_BACK_LEFT_ID = 11;
  public static final int ENCODER_BACK_RIGHT_ID = 12;
  public static final int PIDGEON = 13;
  public static final int MOTOR_INTAKE_PIVOT_ID = 14;
  public static final int MOTOR_INTAKE_COLLECT_ID = 15;
  public static final int MOTOR_SHOOTER_FLYWHEEL_LEFT_ID = 16;
  public static final int MOTOR_SHOOTER_FLYWHEEL_RIGHT_ID = 17;
  public static final int MOTOR_COLUMN_ID = 18;
  public static final int MOTOR_FEEDER_ID = 19;

  public static final int DPAD_UP = 0;
  public static final int DPAD_DOWN = 180;
  public static final int DPAD_RIGHT = 90;
  public static final int DPAD_LEFT = 270;

  public static final double MOTOR_INTAKE_PIVOT_DEFAULT_SPEED = 0.2;
  public static final double MOTOR_INTAKE_SPIN_DEFAULT_SPEED = 0.2;
  public static final double MOTOR_FEED_DEFAULT_SPEED = 0.50;
  public static final double MOTOR_COLUMN_DEFAULT_SPEED = -0.50;
  public static final double MOTOR_SHOOTER_DEFAULT_SPEED_LOW = -0.75; 
  public static final double MOTOR_SHOOTER_DEFAULT_SPEED_MEDIUM = -0.85; 
  public static final double MOTOR_SHOOTER_DEFAULT_SPEED_HIGH = -1;
  public static final double MOTOR_INTAKE_PIVOT_DOWN_POSITION = -0.1; // 0.05 rotations, multiplied by 25 to convert to real rotation of motor
  public static final double MOTOR_INTAKE_PIVOT_UP_POSITION = 5; // 0.05 rotations, multiplied by 25 to convert to real rotation of motor
  public static final double MOTOR_INTAKE_PIVOT_SPEED_HACK = 0.1;

  public static final boolean IS_CALIBRATING = false; // Set to TRUE if calibrating constants.
  

  //=======================================================================================================
  //The HACK code should be removed if the Pivot SetPosition code works.
  public static final boolean IS_USING_PIVOT_HACK = false; // Set to TRUE if using Pivot Motor Hack.
  // End of Pivot Hack section==============================================================================
 
  public static final double CALIBRATION_INCREMENT = 0.01;
  public double Calibration = 0;
  public double ShooterSpeed = 0;
  public double IntakeSpeed = 0; 
  public double FeedSpeed = 0;
  public double ColumnSpeed = 0;

  TalonFX MotorFeed = new TalonFX(MOTOR_FEEDER_ID);  
  TalonFX MotorColumn = new TalonFX(MOTOR_COLUMN_ID); 
  TalonFX MotorIntakePivot = new TalonFX(MOTOR_INTAKE_PIVOT_ID); 
  TalonFX MotorIntakeSpin = new TalonFX(MOTOR_INTAKE_COLLECT_ID); 
  TalonFX MotorShooterLeft = new TalonFX(MOTOR_SHOOTER_FLYWHEEL_LEFT_ID);  
  TalonFX MotorShooterRight = new TalonFX(MOTOR_SHOOTER_FLYWHEEL_RIGHT_ID); 
  // Drive motors (created here so autonomous can drive)
  TalonFX DriveFrontLeft = new TalonFX(MOTOR_FRONT_LEFT_DRIVE_ID);
  TalonFX DriveFrontRight = new TalonFX(MOTOR_FRONT_RIGHT_DRIVE_ID);
  TalonFX DriveBackLeft = new TalonFX(MOTOR_BACK_LEFT_DRIVE_ID);
  TalonFX DriveBackRight = new TalonFX(MOTOR_BACK_RIGHT_DRIVE_ID);
  
  // MOTION MAGIC BEGINS--------------------------------------------
  TalonFXConfiguration talonFXConfigs = new TalonFXConfiguration()
                                            .withMotionMagic(new MotionMagicConfigs()
                                              .withMotionMagicCruiseVelocity(Units.degreesToRotations(45))
                                              .withMotionMagicAcceleration(Units.degreesToRotations(45)))
                                            .withSlot0(new Slot0Configs()
                                              .withKP(10)
                                              .withKI(0)
                                              .withKD(0.1)
                                              .withKS(0.1))
                                            .withFeedback(new FeedbackConfigs()
                                              .withSensorToMechanismRatio(15));

  //MotorIntakePivot.getConfigurator().apply(talonFXConfigs);
  
// MOTION MAGIC ENDS--------------------------------------------


  private final XboxController mControllerShooter = new XboxController(1);
    /* log and replay timestamp and joystick data */
    private final HootAutoReplay m_timeAndJoystickReplay = new HootAutoReplay()
        .withTimestampReplay()
        .withJoystickReplay();

    public Robot() {
        m_robotContainer = new RobotContainer();

        TalonFXConfiguration config = new TalonFXConfiguration(); 
    // PID (We might need to tune these values later, but for now we keep kP to 3 and leave kI and kD at 0)
    config.Slot0.kP = 3; //How strong the motor will try to reach the target position. Lower values will make motor less aggressive.
    config.Slot0.kI = 0; //If the motor stops short of the target position, kI will use a small amount of power to slowly move it the rest of the way.
    config.Slot0.kD = 0; //Slows the motor down as it gets closer to the target position. Higher values will make the motor slow down more as it approaches the target position.

    // Motion Magic settings 
    config.MotionMagic.MotionMagicCruiseVelocity = 5; //Maximum speed the motor can move at when trying to reach the target position.
    config.MotionMagic.MotionMagicAcceleration = 10; //Acceleration of the motor when trying to reach the target position.

    MotorIntakePivot.getConfigurator().apply(config); 
    }

    @Override
    public void robotPeriodic() {
        m_timeAndJoystickReplay.update();
        CommandScheduler.getInstance().run(); 
    }

    @Override
    public void disabledInit() {}

    @Override
    public void disabledPeriodic() {}

    @Override
    public void disabledExit() {}

    @Override
    public void autonomousInit() { //Autonomous code
    // We'll run a small time-based autonomous here: drive ~2m backward, then shoot.
      /*m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    final double distanceMeters = 2.0; // desired backward travel
    final double percentOutput = -0.5; // negative => backward
    final double assumedMaxSpeedMps = 3.0; // estimate of robot speed at 100% output (adjust if needed)
    final double actualSpeedMps = assumedMaxSpeedMps * Math.abs(percentOutput);
    final long driveTimeMs = (long) (1000.0 * (distanceMeters / Math.max(actualSpeedMps, 0.01)));

    // Shooting parameters
    final double shooterOutput = MOTOR_SHOOTER_DEFAULT_SPEED_MEDIUM; // use medium speed
    final double feedOutput = MOTOR_FEED_DEFAULT_SPEED;
    final double columnOutput = MOTOR_COLUMN_DEFAULT_SPEED;
    final long shootTimeMs = 3000; // shoot for 3s

    Thread autoThread = new Thread(() -> {
      try {
        System.out.println("Autonomous: driving backward for ~" + driveTimeMs + " ms");
        DriveFrontLeft.set(percentOutput);
        DriveFrontRight.set(percentOutput);
        DriveBackLeft.set(percentOutput);
        DriveBackRight.set(percentOutput);
        Thread.sleep(driveTimeMs);

        DriveFrontLeft.set(0);
        DriveFrontRight.set(0);
        DriveBackLeft.set(0);
        DriveBackRight.set(0);

        Thread.sleep(100);

        System.out.println("Autonomous: starting shooter and feed");
        MotorShooterLeft.set(shooterOutput);
        MotorShooterRight.set(shooterOutput);
        MotorFeed.set(feedOutput);
        MotorColumn.set(columnOutput);

        Thread.sleep(shootTimeMs);

        MotorShooterLeft.set(0);
        MotorShooterRight.set(0);
        MotorFeed.set(0);
        MotorColumn.set(0);

        System.out.println("Autonomous: complete");
      } catch (InterruptedException ex) {
        DriveFrontLeft.set(0);
        DriveFrontRight.set(0);
        DriveBackLeft.set(0);
        DriveBackRight.set(0);
        MotorShooterLeft.set(0);
        MotorShooterRight.set(0);
        MotorFeed.set(0);
        MotorColumn.set(0);
        Thread.currentThread().interrupt();
      }
    });
    autoThread.setDaemon(true);
    autoThread.setName("AutonomousThread");
    autoThread.start(); */
    }

    @Override
    public void autonomousPeriodic() {}

    @Override
    public void autonomousExit() {}

    @Override
    public void teleopInit() {
        if (m_autonomousCommand != null) {
            CommandScheduler.getInstance().cancel(m_autonomousCommand);
        }
        
    }
  boolean isIntakeMotorOn = false;
    @Override
    public void teleopPeriodic() {
    
    if(IS_CALIBRATING) { //If the calibration flag isn't set to TRUE, disable calibration
      if (mControllerShooter.getLeftBumperButton()) {
      Calibration-=CALIBRATION_INCREMENT;
      System.out.println("-Calibration: " + Calibration);
      }
      if (mControllerShooter.getRightBumperButton()) {
        Calibration+=CALIBRATION_INCREMENT;
        System.out.println("+Calibration: " + Calibration);
      }
    }
      
    if (isIntakeMotorOn) { 
      MotorIntakeSpin.set(MOTOR_INTAKE_SPIN_DEFAULT_SPEED+Calibration);
    }
    else {
      MotorIntakeSpin.set(0);
    }
    
    if (mControllerShooter.getPOV()==DPAD_UP) { 
      System.out.println("DPADUP, turn on FEED Motor");
      MotorFeed.set(MOTOR_FEED_DEFAULT_SPEED+Calibration);
    }
    else {
      MotorFeed.set(0);
    }
    
    if (mControllerShooter.getPOV()==DPAD_DOWN) { 
      System.out.println("DPAD DOWN, turn off FEED Motor");
      MotorFeed.set(0);
    }
    if (mControllerShooter.getPOV()==DPAD_UP) { 
      System.out.println("DPAD UP, turn on FEED Motor");
      MotorFeed.set(MOTOR_FEED_DEFAULT_SPEED+Calibration);
    }

    if (mControllerShooter.getPOV()==DPAD_LEFT) { 
      System.out.println("DPAD LEFT, turn off COLUMN Motor");
      MotorColumn.set(0);
    }
    if (mControllerShooter.getPOV()==DPAD_RIGHT) { 
      System.out.println("DPAD UP, turn on Column Motor");
      MotorColumn.set(MOTOR_COLUMN_DEFAULT_SPEED+Calibration);
    }

    if (mControllerShooter.getAButton()) {// set shooter speed to 0
      ShooterSpeed=0;
      MotorIntakeSpin.set(ShooterSpeed);
      System.out.println("MotorIntakeSpin: "+ ShooterSpeed);
      MotorShooterLeft.set(ShooterSpeed);
      MotorShooterRight.set(ShooterSpeed);
      MotorColumn.set(ShooterSpeed);
      //MotorFeed.set(FeedSpeed);
    }

   
    if (mControllerShooter.getXButton()) {// set Shooter Speed to low
      
      ShooterSpeed=MOTOR_SHOOTER_DEFAULT_SPEED_LOW-Calibration; //Calibration is negative because we set the shooter speed for low as negative
      MotorShooterLeft.set(ShooterSpeed);
      MotorShooterRight.set(ShooterSpeed);
      System.out.println("MotorIntakeSpin: "+ ShooterSpeed);
      
      MotorColumn.set(MOTOR_COLUMN_DEFAULT_SPEED-Calibration);
      MotorFeed.set(MOTOR_FEED_DEFAULT_SPEED+Calibration); //Changed MotorFeed.set to MOTOR_FEED_DEFAULT_SPEED
      //MotorFeed.set(FeedSpeed);
    } 


     if (mControllerShooter.getBButton()) {// set Shooter Speed to Medium
      ShooterSpeed=MOTOR_SHOOTER_DEFAULT_SPEED_MEDIUM-Calibration;
      MotorShooterLeft.set(ShooterSpeed);
      MotorShooterRight.set(ShooterSpeed);
      System.out.println("MotorIntakeSpin: "+ ShooterSpeed);

      MotorColumn.set(MOTOR_COLUMN_DEFAULT_SPEED-Calibration);
      MotorFeed.set(MOTOR_FEED_DEFAULT_SPEED+Calibration); //Changed MotorFeed.set to MOTOR_FEED_DEFAULT_SPEED
      //MotorFeed.set(FeedSpeed);
    }
     if (mControllerShooter.getYButton()) {// set Shooter Speed to High
      ShooterSpeed=MOTOR_SHOOTER_DEFAULT_SPEED_HIGH-Calibration;
      MotorShooterLeft.set(ShooterSpeed);
      MotorShooterRight.set(ShooterSpeed);
      MotorFeed.set(FeedSpeed);
      System.out.println("MotorIntakeSpin: "+ ShooterSpeed);

      MotorColumn.set(MOTOR_COLUMN_DEFAULT_SPEED-Calibration);
      MotorFeed.set(MOTOR_FEED_DEFAULT_SPEED+Calibration); //Changed MotorFeed.set to MOTOR_FEED_DEFAULT_SPEED
    }

    // DOWN 
if (mControllerShooter.getLeftTriggerAxis() > 0.1) 
  { //Set pivot arm to DOWN
  if(IS_USING_PIVOT_HACK)//This code is only run if the pivot motor hack is being used
    MotorIntakePivot.set(-(MOTOR_INTAKE_PIVOT_SPEED_HACK+Calibration)); //Changed MotorFeed.set into MotorIntakePivot.set
  else
    MotorIntakePivot.setControl(new MotionMagicDutyCycle(MOTOR_INTAKE_PIVOT_DOWN_POSITION + Calibration));
  }
// UP 
if (mControllerShooter.getRightTriggerAxis() > 0.1) 
  { //Set pivot arm to UP
  if(IS_USING_PIVOT_HACK) //This code is only run if the pivot motor hack is being used
    MotorIntakePivot.set(MOTOR_INTAKE_PIVOT_SPEED_HACK+Calibration); //Changed MotorFeed.set into MotorIntakePivot.set
  else // This code is run if not hacking the pivot motor
    MotorIntakePivot.setControl(new MotionMagicDutyCycle(MOTOR_INTAKE_PIVOT_UP_POSITION + Calibration) ); 
  }
}

    @Override
    public void teleopExit() {}

    @Override
    public void testInit() {
        CommandScheduler.getInstance().cancelAll();
    }

    @Override
    public void testPeriodic() {}

    @Override
    public void testExit() {}

    @Override
    public void simulationPeriodic() {}
}
