package com.github.pesennik.util;

import com.github.pesennik.model.SocialNetworkType;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;

public class OauthLink extends Link {
    private SocialNetworkType network;

    public enum Action {
        LOGIN("login"),
        BIND_ACCOUNT("bind-account"),
        BIND_AVATAR("bind-avatar");

        private String action;

        Action(String action) {
            this.action = action;
        }

        public String toString() {
            return action;
        }
    }

    @NotNull
    private Action action;

    public OauthLink(@NotNull String id, @NotNull SocialNetworkType network) {
        this(id, network, Action.LOGIN);

    }

    public OauthLink(@NotNull String id, @NotNull SocialNetworkType network, @NotNull Action action) {
        super(id);
        this.network = network;
        this.action = action;
    }

    public void onClick() {
        HttpUtils.saveLastViewedPage((HttpServletRequest) getRequest().getContainerRequest());
        throw new RedirectToUrlException(OauthUtils.getOauthUrl(network, action));
    }
}
