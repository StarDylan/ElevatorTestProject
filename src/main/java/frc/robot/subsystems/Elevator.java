package frc.robot.subsystems;

import frc.robot.constants.ElevatorConstants;
import frc.robot.hardware.RandomObscureMotor;

import java.util.logging.Logger;

public class Elevator {

    private static Logger logger = Logger.getLogger(Elevator.class.toString());

    private RandomObscureMotor doorMotor;
    private RandomObscureMotor movingMotor;


    private ElevatorState elevatorState;
    private WantedElevatorState wantedElevatorState;

    private ElevatorState prevElevatorState;
    private boolean stateChanged;

    private enum ElevatorState {
        GOING_UP,
        GOING_DOWN,
        DOORS_OPEN,
        IDLE
    }
    public enum WantedElevatorState {
        WANTS_TO_GO_UP,
        WANTS_TO_GO_DOWN,
        WANTS_TO_OPEN_DOORS,
        WANTS_TO_IDLE
    }

    public Elevator(){
        this.wantedElevatorState = WantedElevatorState.WANTS_TO_IDLE;
        this.elevatorState = ElevatorState.IDLE;

        //Can't be done on PC Hardware
        this.doorMotor = new RandomObscureMotor();
        this.movingMotor = new RandomObscureMotor();
    }

    //Dependency Injection
    public Elevator(RandomObscureMotor movingMotor, RandomObscureMotor doorMotor){
        this.wantedElevatorState = WantedElevatorState.WANTS_TO_IDLE;
        this.elevatorState = ElevatorState.IDLE;

        this.movingMotor = movingMotor;
        this.doorMotor = doorMotor;
    }

    public void setWantedState(WantedElevatorState wantedElevatorState){
        this.wantedElevatorState = wantedElevatorState;
    }

    public void update(){
        if (prevElevatorState != elevatorState){
            stateChanged = true;
            prevElevatorState = elevatorState;
        } else stateChanged = false;

        ElevatorState newState;

        switch(elevatorState){
            case GOING_UP:
                newState = handleGoUp();
                break;
            case GOING_DOWN:
                newState = handleGoDown();
                break;
            case DOORS_OPEN:
                newState = handleDoorOpen();
                break;
            case IDLE:
                newState = handleIdle();
                break;
            default:
                newState = handleIdle();
                logger.severe("Default Case Statement! STATE: " + elevatorState.toString());
        };

        elevatorState = newState;


    }

    //Handlers
    private ElevatorState handleGoUp(){
        if (stateChanged)
            movingMotor.runMotor(ElevatorConstants.UP_SPEED);
        logger.info("Going Up");
        return defaultStateTransfer();
    }
    private ElevatorState handleGoDown(){
        if (stateChanged)
            movingMotor.runMotor(ElevatorConstants.DOWN_SPEED);
        logger.info("Going Down");
        return defaultStateTransfer();
    }
    private ElevatorState handleDoorOpen(){
        if (stateChanged)
            doorMotor.runMotor(ElevatorConstants.DOOR_SPEED);
        logger.info("Door Open");
        return defaultStateTransfer();
    }
    private ElevatorState handleIdle(){
        if (stateChanged)
            movingMotor.stopMotor();
        doorMotor.stopMotor();
        logger.info("IDLE");
        return defaultStateTransfer();
    }


    private ElevatorState defaultStateTransfer(){
        switch (this.wantedElevatorState) {
            case WANTS_TO_GO_UP:
                return ElevatorState.GOING_UP;
            case WANTS_TO_GO_DOWN:
                return ElevatorState.GOING_DOWN;
            case WANTS_TO_OPEN_DOORS:
                return ElevatorState.DOORS_OPEN;
            case WANTS_TO_IDLE:
                return ElevatorState.IDLE;
            default:
                throw new IllegalArgumentException();
        }
    }
}
