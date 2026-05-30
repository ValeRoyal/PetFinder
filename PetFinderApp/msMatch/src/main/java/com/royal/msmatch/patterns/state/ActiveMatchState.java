package com.royal.msmatch.patterns.state;

import com.royal.msmatch.model.enums.MatchStatus;

public class ActiveMatchState extends AbstractMatchState {

    @Override
    public MatchStatus getStatus() {
        return MatchStatus.ACTIVE;
    }
}
