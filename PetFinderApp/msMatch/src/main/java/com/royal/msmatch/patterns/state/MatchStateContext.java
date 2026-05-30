package com.royal.msmatch.patterns.state;

import com.royal.msmatch.model.Match;
import com.royal.msmatch.model.enums.MatchStatus;

public class MatchStateContext {

    private final Match match;
    private MatchState state;

    public MatchStateContext(Match match) {
        if (match == null) {
            throw new IllegalArgumentException("Match is required.");
        }
        this.match = match;
        MatchStatus status = match.getStatus();
        if (status == null) {
            status = MatchStatus.PENDING;
            match.setStatus(status);
        }
        this.state = MatchStateFactory.fromStatus(status);
        this.state.setContext(this);
    }

    public void handleSwipeResult(boolean mutualApproval) {
        state.handleSwipeResult(mutualApproval);
    }

    public void transitionTo(MatchStatus targetStatus) {
        state.transitionTo(targetStatus);
    }

    public MatchStatus getStatus() {
        return state.getStatus();
    }

    public Match getMatch() {
        return match;
    }

    void changeState(MatchState newState) {
        this.state = newState;
        this.state.setContext(this);
        match.setStatus(newState.getStatus());
    }
}
