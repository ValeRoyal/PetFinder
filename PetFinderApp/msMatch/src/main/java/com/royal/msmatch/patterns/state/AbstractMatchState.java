package com.royal.msmatch.patterns.state;

import com.royal.msmatch.model.enums.MatchStatus;

public abstract class AbstractMatchState implements MatchState {

    protected MatchStateContext context;

    @Override
    public void setContext(MatchStateContext context) {
        this.context = context;
    }

    @Override
    public void handleSwipeResult(boolean mutualApproval) {
    }

    @Override
    public void transitionTo(MatchStatus targetStatus) {
        if (targetStatus == null) {
            throw new IllegalArgumentException("Target status is required.");
        }

        MatchStatus currentStatus = getStatus();
        if (targetStatus == currentStatus) {
            return;
        }
        if (targetStatus.ordinal() < currentStatus.ordinal()) {
            throw new IllegalArgumentException(
                    "Cannot move match from " + currentStatus + " to " + targetStatus + "."
            );
        }

        changeState(MatchStateFactory.fromStatus(targetStatus));
    }

    protected void changeState(MatchState state) {
        context.changeState(state);
    }
}
