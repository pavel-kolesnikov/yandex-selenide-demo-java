package yatest.resolvers.glob;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Neil Traft
 * @author Pavel Kolesnikov
 */
public class GlobParameterTest {

    @Test
    public void star_becomes_dot_star() {
        assertThat(GlobParameter.from("gl*b").pattern()).isEqualTo("gl.*b");
    }

    @Test
    public void escaped_star_is_unchanged() {
        assertThat(GlobParameter.from("gl\\*b").pattern()).isEqualTo("gl\\*b");
    }

    @Test
    public void question_mark_becomes_dot() {
        assertThat(GlobParameter.from("gl?b").pattern()).isEqualTo("gl.b");
    }

    @Test
    public void escaped_question_mark_is_unchanged() {
        assertThat(GlobParameter.from("gl\\?b").pattern()).isEqualTo("gl\\?b");
    }

    @Test
    public void character_classes_dont_need_conversion() {
        assertThat(GlobParameter.from("gl[-o]b").pattern()).isEqualTo("gl[-o]b");
    }

    @Test
    public void escaped_classes_are_unchanged() {
        assertThat(GlobParameter.from("gl\\[-o\\]b").pattern()).isEqualTo("gl\\[-o\\]b");
    }

    @Test
    public void negation_in_character_classes() {
        assertThat(GlobParameter.from("gl[!a-n!p-z]b").pattern()).isEqualTo("gl[^a-n!p-z]b");
    }

    @Test
    public void nested_negation_in_character_classes() {
        assertThat(GlobParameter.from("gl[[!a-n]!p-z]b").pattern()).isEqualTo("gl[[^a-n]!p-z]b");
    }

    @Test
    public void escape_carat_if_it_is_the_first_char_in_a_character_class() {
        assertThat(GlobParameter.from("gl[^o]b").pattern()).isEqualTo("gl[\\^o]b");
    }

    @Test
    public void metachars_are_escaped() {
        assertThat(GlobParameter.from("gl?*.()+|^$@%b").pattern()).isEqualTo("gl..*\\.\\(\\)\\+\\|\\^\\$\\@\\%b");
    }

    @Test
    public void metachars_in_character_classes_dont_need_escaping() {
        assertThat(GlobParameter.from("gl[?*.()+|^$@%]b").pattern()).isEqualTo("gl[?*.()+|^$@%]b");
    }

    @Test
    public void escaped_backslash_is_unchanged() {
        assertThat(GlobParameter.from("gl\\\\b").pattern()).isEqualTo("gl\\\\b");
    }

    @Test
    public void slashQ_and_slashE_are_escaped() {
        assertThat(GlobParameter.from("\\Qglob\\E").pattern()).isEqualTo("\\\\Qglob\\\\E");
    }

    @Test
    public void escaped_braces_are_unchanged() {
        assertThat(GlobParameter.from("\\{glob\\}").pattern()).isEqualTo("\\{glob\\}");
    }

    @Test
    public void braces_are_turned_into_groups() {
        assertThat(GlobParameter.from("{glob,regex}").pattern()).isEqualTo("(glob|regex)");
    }

    @Test
    public void commas_dont_need_escaping() {
        assertThat(GlobParameter.from("{glob\\,regex},").pattern()).isEqualTo("(glob,regex),");
    }
}


