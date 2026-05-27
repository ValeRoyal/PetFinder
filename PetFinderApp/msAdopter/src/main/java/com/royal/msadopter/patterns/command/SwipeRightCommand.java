package com.royal.msadopter.patterns.command;

import com.royal.msadopter.model.AdopterProfile;
import com.royal.msadopter.model.SwipeAction;
import com.royal.msadopter.model.enums.SwipeDirection;

public class SwipeRightCommand implements SwipeCommand {

    private final SwipeReceiver receiver;
    private final AdopterProfile adopter;
    private final String swipeId;
    private final String petProfileId;
    private SwipeAction executedAction;

    public SwipeRightCommand(SwipeReceiver receiver, AdopterProfile adopter, String swipeId, String petProfileId) {
        this.receiver = receiver;
        this.adopter = adopter;
        this.swipeId = swipeId;
        this.petProfileId = petProfileId;
    }

    @Override
    public SwipeAction execute() {
        executedAction = receiver.registerSwipe(adopter, swipeId, petProfileId, SwipeDirection.RIGHT);
        return executedAction;
    }

    @Override
    public void undo() {
        if (executedAction != null) {
            receiver.undoSwipe(executedAction);
        }
    }
}
