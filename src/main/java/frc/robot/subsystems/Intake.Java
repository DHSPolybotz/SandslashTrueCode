package frc.robot.subsystems;

import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

import com.ctre.phoenix6.hardware.TalonFX; //From summer java training video

public class Intake extends SubsystemBase {
    private final MotorController motor;
    private final double inSpeed;
    private final double outSpeed;

    //The hardware 
    TalonFX IntakePivot = new TalonFX(0); //Kraken with ID: 0 is named "ID0" (from summer java training video)
    XboxController exampleXbox = new XboxController(port:0); // 0 is the USB Port to be used as indicated on the Driver Station (code to add Controller)


    public Intake(MotorController motor) {
        this.motor = motor;
        this.inSpeed = Constants.IntakeConstants.kIntakeInSpeed;
        this.outSpeed = Constants.IntakeConstants.kIntakeOutSpeed;
    }

    /** Set motor percent output (-1 .. 1). */
    public void set(double percent) {
        // clamp defensively
        if (percent > 1.0) percent = 1.0;
        if (percent < -1.0) percent = -1.0;
        motor.set(percent);
    }

    /** Run the intake inward at configured speed. */
    public void runIn() {
        set(inSpeed);
    }

    /** Run the intake outward (expel) at configured speed. */
    public void runOut() {
        set(-outSpeed);
    }

    /** Stop the intake motor. */
    public void stop() {
        motor.stopMotor();
    }

    /** Optional: allow caller to invert motor direction. */
    public void setInverted(boolean isInverted) {
        motor.setInverted(isInverted);
    }

    @Override
    public void periodic() {
        // Put useful telemetry so you can tune/verify on the driver station
        SmartDashboard.putNumber("Intake/Percent", motor.get());
    }

    //______________________________________________________________________________________
}
