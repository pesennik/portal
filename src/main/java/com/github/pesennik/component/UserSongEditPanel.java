package com.github.pesennik.component;

import com.github.pesennik.Context;
import com.github.pesennik.event.UserSongModifiedEvent;
import com.github.pesennik.model.UserSong;
import com.github.pesennik.model.UserSongId;
import com.github.pesennik.util.Limits;
import com.github.pesennik.util.UDate;
import com.github.pesennik.util.UserSessionUtils;
import com.github.pesennik.util.ValidatorUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.jetbrains.annotations.Nullable;

public class UserSongEditPanel extends Panel {

    public UserSongEditPanel(String id, @Nullable UserSongId songId) {
        super(id);

        Feedback feedback = new Feedback("feedback");
        add(feedback);

        Form form = new Form("form");
        form.setOutputMarkupId(true);
        add(form);

        //TODO: add JS validators

        UserSong s = songId == null ? null : Context.getUserSongsDbi().getSong(songId);
        if (s == null) {
            s = new UserSong();
        }

        InputField titleField = new InputField("title", s.title);
        form.add(titleField);

        InputField authorField = new InputField("author", s.author);
        form.add(authorField);

        InputArea textField = new InputArea("text", s.text);
        form.add(textField);

        form.add(new AjaxSubmitLink("save_link", form) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                feedback.reset(target);

                String title = titleField.getInputString();
                if (!ValidatorUtils.isValidSongTitle(title)) {
                    feedback.error("Недопустимое название песни!");
                    return;
                }

                String author = authorField.getInputString();
                if (!ValidatorUtils.isValidSongAuthor(author)) {
                    feedback.error("Недопустимое имя автора песни!");
                    return;
                }

                String text = textField.getInputString();
                if (text.length() < Limits.MIN_SONG_TEXT_LENGTH) {
                    feedback.error("Слишком короткий текст песни!");
                    return;
                }
                if (!ValidatorUtils.isValidSongText(text)) {
                    feedback.error("Слишком большой текст песни!");
                    return;
                }

                UserSong song = new UserSong();
                song.id = songId;
                song.userId = UserSessionUtils.getUserIdOrRedirectHome();
                song.title = title;
                song.author = author;
                song.text = text;
                song.creationDate = UDate.now();
                if (songId == null) {
                    Context.getUserSongsDbi().createSong(song);
                    feedback.success("Песня добавлена!");
                } else {
                    Context.getUserSongsDbi().updateSong(song);
                    feedback.success("Изменения сохранены");
                }
                send(getPage(), Broadcast.BREADTH, new UserSongModifiedEvent(target, song.id));
            }
        });

    }


}
