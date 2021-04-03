import frc.robot.constants.ElevatorConstants;
import frc.robot.hardware.RandomObscureMotor;
import frc.robot.subsystems.Elevator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ElevatorTest {
    private Elevator elevator;
    private RandomObscureMotor mockMovingMotor;
    private RandomObscureMotor mockDoorMotor;

    private double movingMotorPower = 0;
    private double doorMotorPower = 0;

    @Before
    public void setUp() {
        mockMovingMotor = mock(RandomObscureMotor.class);
        mockDoorMotor = mock(RandomObscureMotor.class);
        elevator = new Elevator(mockMovingMotor, mockDoorMotor);

        // Basically replaces the .runMotor() method and .stopMotor()
        // for mock Motors with method that puts input
        // into variable for testing
        // Taken from https://github.com/iron-claw-972/FRC2020

        //Moving Motor
        doAnswer(invocation -> {
            Double power = invocation.getArgument(0, Double.class);
            movingMotorPower = power;
            return null;
        }).when(mockMovingMotor).runMotor(any(Double.class));

        doAnswer(invocation -> {
            movingMotorPower = 0;
            return null;
        }).when(mockMovingMotor).stopMotor();

        //Door Motor
        doAnswer(invocation -> {
            Double power = invocation.getArgument(0, Double.class);
            doorMotorPower = power;
            return null;
        }).when(mockDoorMotor).runMotor(any(Double.class));

        doAnswer(invocation -> {
            doorMotorPower = 0;
            return null;
        }).when(mockDoorMotor).stopMotor();
    }

    @Test
    public void TestGoUp() {
        elevator.setWantedState(Elevator.WantedElevatorState.WANTS_TO_GO_UP);
        elevator.update();
        elevator.update();
        assertEquals(ElevatorConstants.UP_SPEED, movingMotorPower, 0.1);
        assertTrue(ElevatorConstants.UP_SPEED > 0); // Make sure its going Positive
    }

    @Test
    public void TestGoDown() {
        elevator.setWantedState(Elevator.WantedElevatorState.WANTS_TO_GO_DOWN);
        elevator.update();
        elevator.update();
        assertEquals( ElevatorConstants.DOWN_SPEED,movingMotorPower, 0.1);
    }

    @Test
    public void TestOpenDoor() {
        elevator.setWantedState(Elevator.WantedElevatorState.WANTS_TO_OPEN_DOORS);
        elevator.update();
        elevator.update();
        assertEquals( ElevatorConstants.DOOR_SPEED, doorMotorPower, 0.1);
    }

    @Test
    public void TestIdle() {
        elevator.setWantedState(Elevator.WantedElevatorState.WANTS_TO_GO_UP);
        elevator.update();
        elevator.update();
        elevator.setWantedState(Elevator.WantedElevatorState.WANTS_TO_OPEN_DOORS);
        elevator.update();
        elevator.update();
        elevator.setWantedState(Elevator.WantedElevatorState.WANTS_TO_IDLE);
        elevator.update();
        elevator.update();
        assertEquals(0, doorMotorPower, 0.1);

        assertEquals(0, movingMotorPower, 0.1);
    }
}
