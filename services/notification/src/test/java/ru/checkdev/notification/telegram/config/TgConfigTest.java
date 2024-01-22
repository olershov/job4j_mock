package ru.checkdev.notification.telegram.config;

import org.junit.jupiter.api.Test;
import ru.checkdev.notification.domain.PersonDTO;

import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testing TgConfig;
 *
 * @author Dmitry Stepanov, user Dmitry
 * @author Oleg Ershov
 * @since 22.01.2024
 */
class TgConfigTest {
    private final String prefix = "pr/";
    private final int passSize = 10;
    private final TgConfig tgConfig = new TgConfig(prefix, passSize);

    @Test
    void whenValidDataThenReturnTrue() {
        var data = "username#mail@mail.ru";
        var parsed = tgConfig.checkFormat(data);
        assertThat(parsed.get("email")).isEqualTo("mail@mail.ru");
        assertThat(parsed.get("username")).isEqualTo("username");
    }

    @Test
    void whenNameIsEmptyThenReturnFalse() {
        var data = "#mail@mail.ru";
        var parsed = tgConfig.checkFormat(data);
        assertThat(parsed).isEmpty();
    }

    @Test
    void whenEmailIsEmptyThenReturnFalse() {
        var data = "username#";
        var parsed = tgConfig.checkFormat(data);
        assertThat(parsed).isEmpty();
    }

    @Test
    void whenNotContentSeparatorThenReturnFalse() {
        var data = "username/mail@mail.ru";
        var parsed = tgConfig.checkFormat(data);
        assertThat(parsed).isEmpty();
    }

    @Test
    void whenInvalidDataThenReturnFalse() {
        var data = "username##mail@mail.ru";
        var parsed = tgConfig.checkFormat(data);
        assertThat(parsed).isEmpty();
    }

    @Test
    void whenIsEmailThenReturnTrue() {
        var email = "mail@mail.ru";
        var actual = tgConfig.isEmail(email);
        assertThat(actual).isTrue();
    }

    @Test
    void whenIsEmailThenReturnFalse() {
        var email = "mail.ru";
        var actual = tgConfig.isEmail(email);
        assertThat(actual).isFalse();
    }

    @Test
    void whenGetPasswordThenLengthPassSize() {
        var pass = tgConfig.getPassword();
        assertThat(pass.length()).isEqualTo(passSize);
    }

    @Test
    void whenGetPasswordThenStartWishPrefix() {
        var pass = tgConfig.getPassword();
        assertThat(pass.startsWith(prefix)).isTrue();
    }

    @Test
    void whenGetObjectToMapThenReturnObjectMap() {
        var personDto = new PersonDTO("mail", "pass", "name", true, null, Calendar.getInstance());
        var map = tgConfig.getObjectToMap(personDto);
        assertThat(map.get("email")).isEqualTo(personDto.getEmail());
        assertThat(map.get("password")).isEqualTo(personDto.getPassword());
        assertThat(map.get("username")).isEqualTo(personDto.getUsername());
        assertThat(String.valueOf(map.get("privacy"))).isEqualTo(String.valueOf(true));
    }
}