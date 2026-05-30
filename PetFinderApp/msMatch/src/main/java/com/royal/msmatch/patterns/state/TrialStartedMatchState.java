package com.royal.msmatch.patterns.state;

import com.royal.msmatch.model.enums.MatchStatus;

public class TrialStartedMatchState extends AbstractMatchState {

    @Override
    public MatchStatus getStatus() {
        return MatchStatus.TRIAL_STARTED;
    }
}
