package com.royal.msmatch.patterns.state;

import com.royal.msmatch.model.enums.MatchStatus;

public class MutualMatchState extends AbstractMatchState {

    @Override
    public MatchStatus getStatus() {
        return MatchStatus.MUTUAL;
    }
}
