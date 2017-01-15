package com.github.pesennik.component.song;

import com.github.pesennik.Context;
import com.github.pesennik.component.form.Feedback;
import com.github.pesennik.component.form.InputArea;
import com.github.pesennik.component.form.InputField;
import com.github.pesennik.component.help.AboutPage;
import com.github.pesennik.component.util.AjaxCallback;
import com.github.pesennik.component.util.AnchoredBookmarkablePageLink;
import com.github.pesennik.event.UserSongChangedEvent;
import com.github.pesennik.event.UserSongChangedEvent.ChangeType;
import com.github.pesennik.model.UserSong;
import com.github.pesennik.model.UserSongId;
import com.github.pesennik.util.UDate;
import com.github.pesennik.util.UserSessionUtils;
import com.github.pesennik.util.ValidatorUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SongEditPanel extends Panel {

    public SongEditPanel(String id, @Nullable UserSongId songId, @NotNull AjaxCallback closeCallback) {
        super(id);

        add(new WebMarkupContainer("new_song_header").setVisible(songId == null));
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

        InputField textAuthorField = new InputField("text_author", s.textAuthor);
        form.add(textAuthorField);

        InputField musicAuthorField = new InputField("music_author", s.musicAuthor);
        form.add(musicAuthorField);

        InputField singerField = new InputField("singer", s.singer);
        form.add(singerField);

        InputField bandField = new InputField("band", s.band);
        form.add(bandField);

        InputArea textField = new InputArea("text", s.text);
        textField.setAutofocus(s.id != null);
        form.add(textField);

        InputArea linksField = new InputArea("links", s.extra.links);
        form.add(linksField);

        form.add(new AjaxSubmitLink("save_link", form) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                feedback.reset(target);

                String title = titleField.getInputString();
                if (!ValidatorUtils.isValidSongTitle(title)) {
                    feedback.error("Недопустимое название песни!");
                    return;
                }

                String textAuthor = textAuthorField.getInputString();
                if (!ValidatorUtils.isValidSongAuthor(textAuthor)) {
                    feedback.error("Недопустимое имя автора слов!");
                    return;
                }
                String musicAuthor = musicAuthorField.getInputString();
                if (!ValidatorUtils.isValidSongAuthor(musicAuthor)) {
                    feedback.error("Недопустимое имя автора музыки!");
                    return;
                }
                String singer = singerField.getInputString();
                if (!ValidatorUtils.isValidSongAuthor(singer)) {
                    feedback.error("Недопустимое имя исполнителя!");
                    return;
                }
                String band = bandField.getInputString();
                if (!ValidatorUtils.isValidSongAuthor(band)) {
                    feedback.error("Недопустимое имя группы!");
                    return;
                }

                String text = textField.getInputString();
                if (text.length() < UserSong.MIN_SONG_TEXT_LENGTH) {
                    feedback.error("Слишком короткий текст песни!");
                    return;
                }
                if (!ValidatorUtils.isValidSongText(text)) {
                    feedback.error("Слишком большой текст песни!");
                    return;
                }

                String links = linksField.getInputString();
                if (links.length() > UserSong.MAX_URLS_TEXT_LENGTH) {
                    feedback.error("Слишком большая длина ссылок!");
                    return;
                }

                UserSong song = new UserSong();
                song.id = songId;
                song.userId = UserSessionUtils.getUserIdOrRedirectHome();
                song.title = title;
                song.textAuthor = textAuthor;
                song.musicAuthor = musicAuthor;
                song.singer = singer;
                song.band = band;
                song.text = text;
                song.creationDate = UDate.now();
                song.extra.links = links;
                if (songId == null) {
                    Context.getUserSongsDbi().createSong(song);
                    feedback.success("Песня добавлена.");
                } else {
                    Context.getUserSongsDbi().updateSong(song);
                    feedback.success("Изменения сохранены.");
                }
                send(getPage(), Broadcast.BREADTH, new UserSongChangedEvent(target, song.id, songId == null ? ChangeType.Created : ChangeType.Updated));
                closeCallback.callback(target);
            }
        });

        form.add(new AjaxLink<Void>("cancel_link") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                closeCallback.callback(target);
            }
        });

        form.add(new AjaxLink<Void>("delete_link") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                assert songId != null;
                Context.getUserSongsDbi().delete(songId);
                closeCallback.callback(target);
                send(getPage(), Broadcast.BREADTH, new UserSongChangedEvent(target, songId, ChangeType.Deleted));
            }
        }.setVisible(songId != null));

        form.add(new AnchoredBookmarkablePageLink("about_link", AboutPage.class, AboutPage.FORMAT_ANCHOR));
    }
}
