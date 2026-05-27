package com.royal.msmatch.patterns.singleton;

public class MatchingEngineClient {

    public MatchingEngine getSharedMatchingEngine() {
        return MatchingEngine.getInstance();
    }
}
