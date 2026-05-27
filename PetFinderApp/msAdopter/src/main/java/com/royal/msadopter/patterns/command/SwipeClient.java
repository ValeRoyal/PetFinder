package com.royal.msadopter.patterns.command;

import com.royal.msadopter.model.AdopterProfile;
import com.royal.msadopter.model.SwipeAction;

public class SwipeClient {

    private final SwipeReceiver receiver = new SwipeReceiver();
    private final SwipeInvoker invoker = new SwipeInvoker();

    public SwipeAction swipeRight(AdopterProfile adopter, String swipeId, String petProfileId) {
        return invoker.execute(new SwipeRightCommand(receiver, adopter, swipeId, petProfileId));
    }

    public SwipeAction swipeLeft(AdopterProfile adopter, String swipeId, String petProfileId) {
        return invoker.execute(new SwipeLeftCommand(receiver, adopter, swipeId, petProfileId));
    }

    public void undoLastSwipe() {
        invoker.undoLast();
    }
}
