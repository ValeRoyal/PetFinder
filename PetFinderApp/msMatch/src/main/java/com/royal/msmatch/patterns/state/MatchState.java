package com.royal.msmatch.patterns.state;

import com.royal.msmatch.model.enums.MatchStatus;

public interface MatchState {

    void setContext(MatchStateContext context);

    MatchStatus getStatus();

    void handleSwipeResult(boolean mutualApproval);

    void transitionTo(MatchStatus targetStatus);
}
