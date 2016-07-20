define("site", ["locale/" + __locale], function ($locale) {

    /**
     * Clicks on element #id2 after enter is pressed on element #id1
     * @param id1 {string}
     * @param id2 {string}
     */
    function clickOnEnter(id1, id2) {
        $("#" + id1).keypress(function (event) {
            var keyCode = (event.which ? event.which : event.keyCode);
            if ((keyCode === 10 || keyCode == 13) && !event.ctrlKey) {
                $("#" + id2).click();
                event.preventDefault();
            }
        });
    }

    /** Change focus to  element #id2 after enter is pressed on element #id1 */
    function focusOnEnter(id1, id2) {
        $("#" + id1).keypress(function (event) {
            var keyCode = (event.which ? event.which : event.keyCode);
            if (keyCode === 10 || keyCode == 13) {
                $("#" + id2).focus();
                event.preventDefault();
            }
        });
    }

    /** Scrolls to top */
    function enableScrollTop() {
        $(document).ready(function () {
            var $backTop = $("#back-top");
            if (!$backTop) {
                return;
            }
            $backTop.hide(); // hide #back-top first
            $(function () { // fade in #back-top
                $(window).scroll(function () {
                    if ($(this).scrollTop() > 100) {
                        $('#back-top').fadeIn();
                    } else {
                        $('#back-top').fadeOut();
                    }
                });
                $('#back-top').find('a').click(function () { // scroll body to 0px on click
                    $('body,html').animate({
                        scrollTop: 0
                    }, 500);
                    return false;
                });
            });
        });
    }

    /**
     *  Translates word using active locale
     *  @param {string} word - a word to translate
     *  @returns {string} translated word or the original word if no translation was found.
     */
    function tr(word) {
        var res = $locale.dictionary[word];
        return res ? res : word;
    }

    /**
     * Translates word using active locale & plural form for the given value 'n'
     *  @param {number} n
     *  @param {string} word
     */
    function pl(n, word) {
        var res = tr(word);
        var pluralForm = $locale.pluralForm[res];
        if (!pluralForm) {
            return res;
        }
        return pluralForm[$locale.pluralIndex(n)];
    }

    // public module interface
    return {
        clickOnEnter: clickOnEnter,
        focusOnEnter: focusOnEnter,

        //scroll top
        enableScrollTop: enableScrollTop,

        // i18n
        pl: pl,
        tr: tr
    };
});