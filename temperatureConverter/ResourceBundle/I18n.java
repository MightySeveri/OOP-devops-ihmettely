import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public final class I18n {

    private static final String BASE_NAME = "i18n.messages";

    private static Locale locale = Locale.JAPANESE;
    private static ResourceBundle bundle = load(locale);

    private I18n() {}

    private static ResourceBundle load(Locale target) {
        return ResourceBundle.getBundle(
            BASE_NAME,
            target,
            ResourceBundle.Control.getNoFallbackControl(
                ResourceBundle.Control.FORMAT_DEFAULT
            )
        );
    }

    public static void setLocale(Locale newLocale) {
        bundle = load(newLocale);
        locale = newLocale;
    }

    public static Locale getLocale() {
        return locale;
    }

    public static String get(String key) {
        return bundle.getString(key);
    }

    public static String format(String key, Object... args) {
        MessageFormat formatter =
            new MessageFormat(get(key), locale);

        return formatter.format(args);
    }
}
