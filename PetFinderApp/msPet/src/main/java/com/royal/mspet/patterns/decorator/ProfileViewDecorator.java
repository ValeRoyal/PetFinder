package com.royal.mspet.patterns.decorator;

import java.util.List;

public abstract class ProfileViewDecorator implements ProfileView {

    protected final ProfileView wrapped;

    protected ProfileViewDecorator(ProfileView wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String getDisplayInfo() {
        return wrapped.getDisplayInfo();
    }

    @Override
    public List<String> getPhotos() {
        return wrapped.getPhotos();
    }
}
