package com.royal.msnotification.patterns.iterator;

import com.royal.msnotification.model.Notification;

public interface NotificationIterator {

    boolean hasNext();

    Notification next();
}
