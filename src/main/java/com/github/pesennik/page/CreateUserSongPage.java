package com.github.pesennik.page;

import com.github.pesennik.Context;
import com.github.pesennik.annotation.MountPath;
import com.github.pesennik.component.Feedback;
import com.github.pesennik.component.InputArea;
import com.github.pesennik.component.InputField;
import com.github.pesennik.model.UserSong;
import com.github.pesennik.util.Limits;
import com.github.pesennik.util.UDate;
import com.github.pesennik.util.UserSessionUtils;
import com.github.pesennik.util.ValidatorUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;

@MountPath("/new-song")
public class CreateUserSongPage extends BaseUserPage {

    public CreateUserSongPage() {

        Feedback feedback = new Feedback("feedback");
        add(feedback);

        Form form = new Form("form");
        form.setOutputMarkupId(true);
        add(form);

        //TODO: add JS validators

        InputField titleField = new InputField("title");
        form.add(titleField);

        InputField authorField = new InputField("author");
        form.add(authorField);

        InputArea textField = new InputArea("text");
        form.add(textField);

        form.add(new AjaxSubmitLink("create_link", form) {
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
                song.userId = UserSessionUtils.getUserIdOrRedirectHome();
                song.title = title;
                song.author = author;
                song.text = text;
                song.creationDate = UDate.now();
                Context.getUserSongsDbi().createSong(song);
                feedback.success("Песня добавлена!");
            }
        });
    }
}
