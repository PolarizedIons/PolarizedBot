package mocks;

import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.util.Localizer;
import net.polarizedions.polarizedbot.util.MessageUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SetupMocks {
    public static void botClient() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException {
        Class<Bot> clazz = Bot.class;
        Constructor<Bot> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        Bot bot = constructor.newInstance();
        Field field = clazz.getDeclaredField("client");
        field.setAccessible(true);
        field.set(bot, new MockDiscordClient());
    }

    public static void setupRatelimitMock() throws NoSuchFieldException, IllegalAccessException {
        Class<MessageUtil> clazz = MessageUtil.class;
        Field rateLimitEnable = clazz.getDeclaredField("ENABLE_RATELMIT_HANDLING");
        rateLimitEnable.setAccessible(true);
        rateLimitEnable.set(null, false);
    }

    public static void setupLocalization() throws IllegalAccessException, NoSuchFieldException {
        Class<Localizer> clazz = Localizer.class;
        Field field = clazz.getField("AVAILABLE_LANGUAGES");
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        List<String> newSupports = new ArrayList<>();
        Collections.addAll(newSupports, (String[])field.get(null));
        newSupports.add(0, "testlang");
        field.set(null, newSupports.toArray(new String[0]));

        Localizer.init();
    }
}
