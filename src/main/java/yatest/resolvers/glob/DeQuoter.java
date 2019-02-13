package yatest.resolvers.glob;

/**
 * DeQuoter умеет убирать обрамляющие кавычки.
 * Иногда для читаемости Cucumber-шага кавычки нужны, а вот при обработке аргументов - надо обрезать.
 * <p>
 * лежит тут потому что больше пока нигде не нужен. Надо перенести, если понадобится еще где-нибудь.
 */
public class DeQuoter {
    /**
     * Убирает одни парные внешние одинарные или двойные кавычки если такие есть.
     * Например:
     * "123"   => 123
     * '123'   => 123
     * "'123'" => '123'
     * '"'     => "
     * '"      => '"
     * 123     => 123
     */
    public static String stripBoundingQuotes(String s) {
        if (s.length() >= 2 && (anyCharIsBounding(s, "\"'"))) {
            return s.substring(1, s.length() - 1);
        }

        return s;
    }

    /**
     * Вернет true, если хотя бы один из символов в potentialBoundingChars есть и в первом,
     * и в последнем символе строки.
     */
    private static boolean anyCharIsBounding(String s, CharSequence potentialBoundingChars) {
        for (int i = 0; i < potentialBoundingChars.length(); i++) {
            final char c = potentialBoundingChars.charAt(i);
            if (s.charAt(0) == c && s.charAt(s.length() - 1) == c) {
                return true;
            }
        }

        return false;
    }
}
