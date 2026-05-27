package com.royal.msnotification.patterns.strategy;

import com.royal.msnotification.model.Notification;
import com.royal.msnotification.model.enums.NotificationChannel;

public class NotificationDeliveryClient {

    private final NotificationDeliveryContext context = new NotificationDeliveryContext();
    private final EmailNotificationStrategy emailStrategy;
    private final InAppNotificationStrategy inAppStrategy;

    public NotificationDeliveryClient(
            EmailNotificationStrategy emailStrategy,
            InAppNotificationStrategy inAppStrategy
    ) {
        this.emailStrategy = emailStrategy;
        this.inAppStrategy = inAppStrategy;
    }

    public boolean deliver(Notification notification) {
        if (notification.getChannel() == NotificationChannel.EMAIL) {
            context.setStrategy(emailStrategy);
        } else {
            context.setStrategy(inAppStrategy);
        }
        return context.deliver(notification);
    }
}
