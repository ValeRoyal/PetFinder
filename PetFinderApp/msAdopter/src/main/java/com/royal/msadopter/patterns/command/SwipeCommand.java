package com.royal.msadopter.patterns.command;

import com.royal.msadopter.model.SwipeAction;

public interface SwipeCommand {

    SwipeAction execute();

    void undo();
}
