package com.royal.msmatch.patterns.state;

import com.royal.msmatch.model.enums.MatchStatus;

public class PendingMatchState extends AbstractMatchState {

    @Override
    public MatchStatus getStatus() {
        return MatchStatus.PENDING;
    }

    @Override
    public void handleSwipeResult(boolean mutualApproval) {
        if (mutualApproval) {
            changeState(new MutualMatchState());
        }
    }
}
