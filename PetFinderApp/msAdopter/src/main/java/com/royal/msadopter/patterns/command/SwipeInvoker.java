package com.royal.msadopter.patterns.command;

import com.royal.msadopter.model.SwipeAction;

import java.util.ArrayDeque;
import java.util.Deque;

public class SwipeInvoker {

    private final Deque<SwipeCommand> history = new ArrayDeque<>();

    public SwipeAction execute(SwipeCommand command) {
        SwipeAction action = command.execute();
        history.push(command);
        return action;
    }

    public void undoLast() {
        if (!history.isEmpty()) {
            history.pop().undo();
        }
    }
}
