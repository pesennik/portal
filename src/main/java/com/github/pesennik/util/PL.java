package com.github.pesennik.util;

import com.github.plural4j.Plural;
import org.jetbrains.annotations.NotNull;

public class PL {

    public static final Plural PL = new Plural(Plural.RUSSIAN,
            Plural.parse("секунда,секунды,секунд\n" +
                    "символ,символа,символов\n"
            ));


    public static String pl(int n, @NotNull String word) {
        return PL.pl(n, word);
    }

    public static String npl(int n, @NotNull String word) {
        return PL.npl(n, word);
    }

}
