// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.hardware.TalonFX;  

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.motorcontrol.Talon;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.wpilibj.Joystick;


  

/**
 * The methods in this class are called automatically corresponding to each mode, as described in
 * the TimedRobot documentation. If you change the name of this class or the package after creating
 * this project, you must also update the Main.java file in the project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

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
  public static final double MOTOR_FEED_DEFAULT_SPEED = 0.2;
  public static final double MOTOR_COLUMN_DEFAULT_SPEED = 0.2;
  public static final double MOTOR_SHOOTER_DEFAULT_SPEED_LOW = 0.2;
  public static final double MOTOR_SHOOTER_DEFAULT_SPEED_MEDIUM = 0.4;
  public static final double MOTOR_SHOOTER_DEFAULT_SPEED_HIGH = 0.7;
  public static final double MOTOR_INTAKE_PIVOT_DOWN_POSITION = 0;//set to value
  public static final double MOTOR_INTAKE_PIVOT_UP_POSITION = 0.2;//set to value


  public static final double CALIBRATION_INCREMENT = 0.01;
  public double Calibration = 0;
  public double ShooterSpeed = 0;
  


  TalonFX MotorFeed = new TalonFX(MOTOR_FEEDER_ID);  
  TalonFX MotorColumn = new TalonFX(MOTOR_COLUMN_ID); 
  TalonFX MotorIntakePivot = new TalonFX(MOTOR_INTAKE_PIVOT_ID); 
  TalonFX MotorIntakeSpin = new TalonFX(MOTOR_INTAKE_COLLECT_ID); 
  TalonFX MotorShooterLeft = new TalonFX(MOTOR_SHOOTER_FLYWHEEL_LEFT_ID);  
  TalonFX MotorShooterRight = new TalonFX(MOTOR_SHOOTER_FLYWHEEL_RIGHT_ID);  


  private final XboxController mControllerShooter = new XboxController(0);
  private final XboxController mControllerDriver = new XboxController(1);
  
  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  public Robot() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {}

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
    //shooterMotor.set(.5); //Agnitti added 3. .5 is 50% power
  }
  boolean isIntakeMotorOn = false;
  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    if (mControllerShooter.getLeftBumperButton()) {
      Calibration-=CALIBRATION_INCREMENT;
      System.out.println("-Calibration: " + Calibration);
    }
    if (mControllerShooter.getRightBumperButton()) {
      Calibration+=CALIBRATION_INCREMENT;
      System.out.println("+Calibration: " + Calibration);
    }
    
    if (mControllerShooter.getLeftTriggerAxis()>0.5) {//set pivot position to DOWN position
      //Get Pivot to DOWN position
     
      MotorIntakePivot.setPosition(MOTOR_INTAKE_PIVOT_DOWN_POSITION+Calibration);
      StatusSignal<Angle> positionSignal = MotorIntakePivot.getPosition();
      Angle currentRotations = positionSignal.refresh().getValue();
      System.out.println("DOWN Motor Position: " + currentRotations + " rotations");
    }

    if (mControllerShooter.getRightTriggerAxis()>0.5) {//set pivot position to UP position
      //Get Pivot to UP position
      MotorIntakePivot.setPosition(MOTOR_INTAKE_PIVOT_UP_POSITION+Calibration);
      StatusSignal<Angle> positionSignal = MotorIntakePivot.getPosition();
      Angle currentRotations = positionSignal.refresh().getValue();
      System.out.println("UP Motor Position: " + currentRotations + " rotations");
    }

    
    
     
  
    /*
    if (mControllerShooter.getXButton()) {// it works
      MotorColumn.set(MOTOR_COLUMN_DEFAULT_SPEED+Calibration);
      System.out.println("MotorColumn: "+ MOTOR_COLUMN_DEFAULT_SPEED + Calibration);
    } 
    else {
      MotorColumn.set(0);
    }
      */
    if (mControllerShooter.getRawButtonReleased(7)){
      isIntakeMotorOn = !isIntakeMotorOn;
      System.out.println("Intake Motor Toggled: " + isIntakeMotorOn);
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
    if (mControllerShooter.getPOV()==DPAD_UP) { 
      System.out.println("DPAD UP, turn on Column Motor");
      MotorColumn.set(MOTOR_COLUMN_DEFAULT_SPEED+Calibration);
    }

    if (mControllerShooter.getAButton()) {// set shooter speed to 0
      ShooterSpeed=0;
      MotorIntakeSpin.set(ShooterSpeed);
      System.out.println("MotorIntakeSpin: "+ ShooterSpeed);
    }
   
    if (mControllerShooter.getXButton()) {// set Shooter Speed to low
      ShooterSpeed=MOTOR_SHOOTER_DEFAULT_SPEED_LOW+Calibration;
      MotorShooterLeft.set(ShooterSpeed);
      MotorShooterRight.set(ShooterSpeed);
      System.out.println("MotorIntakeSpin: "+ ShooterSpeed);

    }
     if (mControllerShooter.getBButton()) {// set Shooter Speed to Medium
      ShooterSpeed=MOTOR_SHOOTER_DEFAULT_SPEED_MEDIUM+Calibration;
      MotorShooterLeft.set(ShooterSpeed);
      MotorShooterRight.set(ShooterSpeed);
      System.out.println("MotorIntakeSpin: "+ ShooterSpeed);
    }
     if (mControllerShooter.getYButton()) {// set Shooter Speed to High
      ShooterSpeed=MOTOR_SHOOTER_DEFAULT_SPEED_HIGH+Calibration;
      MotorShooterLeft.set(ShooterSpeed);
      MotorShooterRight.set(ShooterSpeed);
      System.out.println("MotorIntakeSpin: "+ ShooterSpeed);
    } 

    }
  
  


  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {}

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {}  

}