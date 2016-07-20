package org.zametki.service;

import org.zametki.util.UDate;
import org.zametki.model.OnlineInfo;
import org.zametki.model.OnlineStatus;
import org.zametki.model.User;
import org.zametki.model.UserId;
import org.zametki.util.DateTimeConstants;
import org.zametki.Context;
import org.zametki.UserSession;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OnlineStatusManager extends AbstractService {
    private static final Logger log = LoggerFactory.getLogger(OnlineStatusManager.class);

    private static final long ONLINE_TIMEOUT_MILLIS = 15 * DateTimeConstants.MILLIS_PER_MINUTE;
    private final Map<UserId, OnlineInfo> onlineUsers = new ConcurrentHashMap<>();

    private UDate maxAwayDate = UDate.now().minusHours(24);


    public void touch() {
        UserSession session = UserSession.get();
        UserId userId = session.getUserId();
        if (userId == null) {
            return;
        }
        OnlineInfo info = onlineUsers.get(userId);
        if (info == null) {
            info = new OnlineInfo(userId, System.currentTimeMillis());
            onlineUsers.put(userId, info);
        }
        info.accessTimeMillis = System.currentTimeMillis();

        //TODO: touch tomcat session too
//        HttpServletRequest request = ((HttpServletRequest) RequestCycle.get().getRequest().getContainerRequest());
//        HttpSession httpSession = request.getSession();
    }

    //TODO: @Scheduled(cron = "*/10 * * * * *") //second, min, hour, day, month, day of the week
    public void checkOnlineInfos() {
        log.debug("checkOnlineInfos");
        maxAwayDate = UDate.now().minusHours(24);
        long minTime = System.currentTimeMillis() - ONLINE_TIMEOUT_MILLIS;
        for (OnlineInfo info : new ArrayList<>(onlineUsers.values())) {
            if (info.accessTimeMillis < minTime) {
                onlineUsers.remove(info.userId);
            }
        }
    }

    public OnlineStatus getOnlineStatus(@NotNull UserId userId) {
        if (onlineUsers.containsKey(userId)) {
            return OnlineStatus.ONLINE;
        }
        User user = Context.getUsersDbi().getUserById(userId);
        if (user != null && user.lastLoginDate.isAfter(maxAwayDate)) {
            return OnlineStatus.AWAY;
        }
        return OnlineStatus.OFFLINE;
    }

    public int getOnlineUsersCount() {
        return onlineUsers.size();
    }

    @NotNull
    public Collection<OnlineInfo> getOnlineUsers() {
        return onlineUsers.values();
    }

    public void removeFromOnline(@NotNull UserId userId) {
        onlineUsers.remove(userId);
    }
}
