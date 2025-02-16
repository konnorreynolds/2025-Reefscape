package frc.robot.subsystems;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.AddressableLEDBufferView;
import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

import static edu.wpi.first.units.Units.MetersPerSecond;

public class AddressableLEDSubsystem extends SubsystemBase {

    private final AddressableLED led = new AddressableLED(9);
    private final AddressableLEDBuffer buffer = new AddressableLEDBuffer(320);

    AddressableLEDBufferView underGlow = buffer.createView(0, 100);
    AddressableLEDBufferView elevatorGlow = buffer.createView(101, 319);

    private final LEDPattern rainbow = LEDPattern.rainbow(255, 128);

    private LEDPattern elevatorPattern = LEDPattern.solid(editColor(Color.kPurple));
    private LEDPattern underPattern = LEDPattern.solid(editColor(Color.kFirstBlue));
  
      public AddressableLEDSubsystem() {
  
          led.setLength(buffer.getLength());
  
          led.start();

          scrollingRainbow(5);
      }
  
      @Override
      public void periodic() {
          elevatorPattern.applyTo(elevatorGlow);
          underPattern.applyTo(underGlow);
          led.setData(buffer);
      }
  
      public Color editColor(Color color) {
          double red = color.green;
          double green = color.red;
          double blue = color.blue;
          return new Color(red, green, blue);
      }

    public void setProgressMask(Color progressColor, Color defaultColor, double progress, double maxProgress) {
        elevatorPattern = LEDPattern.progressMaskLayer(() -> progress / maxProgress);
    }

    public void scrollingRainbow(double scrollMetersPerSecond) {
        elevatorPattern = rainbow.scrollAtAbsoluteSpeed(MetersPerSecond.of(scrollMetersPerSecond),
                Constants.AddressableConstants.kLedSpacing);
        underPattern = rainbow.scrollAtAbsoluteSpeed(MetersPerSecond.of(scrollMetersPerSecond),
                Constants.AddressableConstants.kLedSpacing);
    }

    public void setPatternElevator(LEDPattern pattern) {
        elevatorPattern = pattern;
    }

    public void setPatternUnder(LEDPattern pattern) {
        underPattern = pattern;
    }

    public Command runPatternElevator(LEDPattern pattern) {
        return runEnd(() -> setPatternElevator(pattern), 
        () -> {
            scrollingRainbow(5);
        });
    }

    public Command runPatternUnder(LEDPattern pattern) {
        return runEnd(() -> setPatternUnder(pattern), 
        () -> {
            scrollingRainbow(5);
        });
    }

    public Command runPatternBoth(LEDPattern underPattern, LEDPattern elevatorPattern) {
        return runEnd(() -> {
            setPatternUnder(underPattern);
            setPatternElevator(elevatorPattern);
        }, () -> scrollingRainbow(5));

    }

    // Will add as I decide how exactly to set up the LEDs

}
