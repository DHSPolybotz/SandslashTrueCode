// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix6.HootAutoReplay;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.hardware.TalonFX;  

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.motorcontrol.Talon;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.wpilibj.Joystick;


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
  public static final double MOTOR_INTAKE_PIVOT_UP_POSITION = 0.1; // 0.05 rotations, multiplied by 25 to convert to real rotation of motor

  public static final double CALIBRATION_INCREMENT = 0.01;
  public double Calibration = 0;
  public double ShooterSpeed = 0;
  public double IntakeSpeed = 0; 


  TalonFX MotorFeed = new TalonFX(MOTOR_FEEDER_ID);  
  TalonFX MotorColumn = new TalonFX(MOTOR_COLUMN_ID); 
  TalonFX MotorIntakePivot = new TalonFX(MOTOR_INTAKE_PIVOT_ID); 
  TalonFX MotorIntakeSpin = new TalonFX(MOTOR_INTAKE_COLLECT_ID); 
  TalonFX MotorShooterLeft = new TalonFX(MOTOR_SHOOTER_FLYWHEEL_LEFT_ID);  
  TalonFX MotorShooterRight = new TalonFX(MOTOR_SHOOTER_FLYWHEEL_RIGHT_ID);  


  private final XboxController mControllerShooter = new XboxController(1);
    /* log and replay timestamp and joystick data */
    private final HootAutoReplay m_timeAndJoystickReplay = new HootAutoReplay()
        .withTimestampReplay()
        .withJoystickReplay();

    public Robot() {
        m_robotContainer = new RobotContainer();
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
    public void autonomousInit() {
        m_autonomousCommand = m_robotContainer.getAutonomousCommand();

        if (m_autonomousCommand != null) {
            CommandScheduler.getInstance().schedule(m_autonomousCommand);
        }
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
        if (mControllerShooter.getLeftBumperButton()) {
      Calibration-=CALIBRATION_INCREMENT;
      System.out.println("-Calibration: " + Calibration);
    }
    if (mControllerShooter.getRightBumperButton()) {
      Calibration+=CALIBRATION_INCREMENT;
      System.out.println("+Calibration: " + Calibration);
    }
        /*if (mControllerShooter.getLeftTriggerAxis()>0.5) {//set pivot position to DOWN position
      //Get Pivot to DOWN position
  
      MotorIntakePivot.setPosition(MOTOR_INTAKE_PIVOT_DOWN_POSITION+Calibration);
      StatusSignal<Angle> positionSignal = MotorIntakePivot.getPosition();
      Angle currentRotations = positionSignal.refresh().getValue();
      System.out.println("DOWN Motor Position: " + currentRotations + " rotations"); */
  {
    
    /*if (mControllerShooter.getRightTriggerAxis()>0.5) {//set pivot position to UP position
      //Get Pivot to UP position
      MotorIntakePivot.setPosition(MOTOR_INTAKE_PIVOT_UP_POSITION+Calibration);
      StatusSignal<Angle> positionSignal = MotorIntakePivot.getPosition();
      Angle currentRotations = positionSignal.refresh().getValue();
      System.out.println("UP Motor Position: " + currentRotations + " rotations"); */
    } 
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
      MotorShooterLeft.set(ShooterSpeed);
      MotorShooterRight.set(ShooterSpeed);
      MotorColumn.set(ShooterSpeed);
    }

   
    if (mControllerShooter.getXButton()) {// set Shooter Speed to low
      
      ShooterSpeed=MOTOR_SHOOTER_DEFAULT_SPEED_LOW-Calibration; //Calibration is negative because we set the shooter speed for low as negative
      MotorShooterLeft.set(ShooterSpeed);
      MotorShooterRight.set(ShooterSpeed);
      System.out.println("MotorIntakeSpin: "+ ShooterSpeed);
      MotorColumn.set(MOTOR_COLUMN_DEFAULT_SPEED-Calibration);
      MotorFeed.set(MOTOR_FEEDER_ID+Calibration);
    } 


     if (mControllerShooter.getBButton()) {// set Shooter Speed to Medium
      ShooterSpeed=MOTOR_SHOOTER_DEFAULT_SPEED_MEDIUM-Calibration;
      MotorShooterLeft.set(ShooterSpeed);
      MotorShooterRight.set(ShooterSpeed);
      System.out.println("MotorIntakeSpin: "+ ShooterSpeed);
      MotorColumn.set(MOTOR_COLUMN_DEFAULT_SPEED-Calibration);
      MotorFeed.set(MOTOR_FEEDER_ID+Calibration);
    }
     if (mControllerShooter.getYButton()) {// set Shooter Speed to High
      ShooterSpeed=MOTOR_SHOOTER_DEFAULT_SPEED_HIGH-Calibration;
      MotorShooterLeft.set(ShooterSpeed);
      MotorShooterRight.set(ShooterSpeed);
      System.out.println("MotorIntakeSpin: "+ ShooterSpeed);
      MotorColumn.set(MOTOR_COLUMN_DEFAULT_SPEED-Calibration);
      MotorFeed.set(MOTOR_FEEDER_ID+Calibration);
    }
    if (mControllerShooter.getLeftTriggerAxis()>0.1) {//set pivot position to DOWN position
      IntakeSpeed=MOTOR_INTAKE_PIVOT_DOWN_POSITION-Calibration; 
      
      MotorIntakePivot.setPosition(MOTOR_INTAKE_PIVOT_DOWN_POSITION+Calibration);
      StatusSignal<Angle> positionSignal = MotorIntakePivot.getPosition();
      Angle currentRotations = positionSignal.refresh().getValue();
      System.out.println("DOWN Motor Position: " + currentRotations); // added "+ currentRotations" to print the actual position of the motor in rotations
    }

    if (mControllerShooter.getRightTriggerAxis()>0.1) {//set pivot position to UP position
      IntakeSpeed=MOTOR_INTAKE_PIVOT_UP_POSITION-Calibration;
      
      MotorIntakePivot.setPosition(MOTOR_INTAKE_PIVOT_UP_POSITION+Calibration); 
      StatusSignal<Angle> positionSignal = MotorIntakePivot.getPosition(); 
      Angle currentRotations = positionSignal.refresh().getValue();
      System.out.println("UP Motor Position: " + currentRotations); 
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
