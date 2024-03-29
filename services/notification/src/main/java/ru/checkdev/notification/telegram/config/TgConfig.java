package ru.checkdev.notification.telegram.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 3. Мидл
 * Класс дополнительных функций телеграм бота, проверка почты, генерация пароля.
 *
 * @author Dmitry Stepanov, user Dmitry
 * @author Oleg Ershov
 * @since 23.01.2024
 */
public class TgConfig {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Pattern EMAIL_PATTERN = Pattern.compile("\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*\\.\\w{2,4}");
    public static final String DELIMITER = "#";
    private final String prefix;
    private final int passSize;

    public TgConfig(String prefix, int passSize) {
        this.prefix = prefix;
        this.passSize = passSize;
    }

    public TgConfig() {
        this.prefix = "tg/";
        this.passSize = 8;
    }

    /**
     * Метод проверяет входящую строку на соответствие формату email
     *
     * @param email String
     * @return boolean
     */
    public boolean isEmail(String email) {
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    /**
     * Метод проверяет входящую строку на соответствие формату ввода данных "username#email"
     *
     * @param data String
     * @return Map<String, String>
     */
    public Map<String, String> parseUsernameAndEmail(String data) {
        Map<String, String> result = checkFormat(data);
        if (!result.isEmpty()) {
            result.put("username", result.get("first"));
            result.put("email", result.get("second"));
        }
        return result;
    }

    /**
     * Метод проверяет входящую строку на соответствие формату ввода данных "email#password"
     *
     * @param data String
     * @return Map<String, String>
     */
    public Map<String, String> parseEmailAndPassword(String data) {
        Map<String, String> result = checkFormat(data);
        if (!result.isEmpty()) {
            result.put("email", result.get("first"));
            result.put("password", result.get("second"));
        }
        return result;
    }

    private Map<String, String> checkFormat(String data) {
        Map<String, String> result = new HashMap<>();
        int index = data.indexOf(DELIMITER);
        if (index != -1 && index != 0 && index != data.length() - 1) {
            String first = data.substring(0, index);
            String second = data.substring(index + 1);
            if (!first.contains(DELIMITER) && !second.contains(DELIMITER)) {
                result.put("first", first);
                result.put("second", second);
            }
        }
        return result;
    }

    /**
     * метод генерирует пароль для пользователя
     *
     * @return String
     */
    public String getPassword() {
        String password = prefix + UUID.randomUUID();
        return password.substring(0, passSize);
    }

    /**
     * Метод преобразовывает Object в карту Map<String,String>
     *
     * @param object Object or Person(Auth)
     * @return Map
     */
    public Map<String, String> getObjectToMap(Object object) {
        return MAPPER.convertValue(object, Map.class);
    }
}
