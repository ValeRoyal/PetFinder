package com.royal.msmatch.patterns.state;

import com.royal.msmatch.model.enums.MatchStatus;

public final class MatchStateFactory {

    private MatchStateFactory() {
    }

    public static MatchState fromStatus(MatchStatus status) {
        if (status == null) {
            return new PendingMatchState();
        }
        return switch (status) {
            case PENDING -> new PendingMatchState();
            case MUTUAL -> new MutualMatchState();
            case ACTIVE -> new ActiveMatchState();
            case TRIAL_STARTED -> new TrialStartedMatchState();
            case ADOPTED -> new AdoptedMatchState();
        };
    }
}
