package com.github.pesennik.page;

import com.github.pesennik.annotation.MountPath;
import com.github.pesennik.component.UserSongEditPanel;

@MountPath("/new-song")
public class CreateUserSongPage extends BaseUserPage {

    public CreateUserSongPage() {
        add(new UserSongEditPanel("create_song_panel", null));
    }
}
