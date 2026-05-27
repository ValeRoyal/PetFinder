package com.royal.msadopter.patterns.command;

import com.royal.msadopter.model.AdopterProfile;
import com.royal.msadopter.model.SwipeAction;
import com.royal.msadopter.model.enums.SwipeDirection;

import java.time.LocalDateTime;

public class SwipeReceiver {

    public SwipeAction registerSwipe(AdopterProfile adopter, String swipeId, String petProfileId, SwipeDirection direction) {
        SwipeAction action = new SwipeAction();
        action.setId(swipeId);
        action.setAdopterId(adopter.getId());
        action.setPetProfileId(petProfileId);
        action.setDirection(direction);
        action.setCreatedAt(LocalDateTime.now());
        action.setUndone(false);

        adopter.registerSwipe(action);
        return action;
    }

    public void undoSwipe(SwipeAction action) {
        action.setUndone(true);
    }
}
