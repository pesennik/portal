package com.github.pesennik.component.song;

import com.github.pesennik.annotation.MountPath;
import com.github.pesennik.component.user.BaseUserPage;

@MountPath("/new-song")
public class CreateUserSongPage extends BaseUserPage {

    public CreateUserSongPage() {
        add(new UserSongEditPanel("create_song_panel", null, null));
    }
}
