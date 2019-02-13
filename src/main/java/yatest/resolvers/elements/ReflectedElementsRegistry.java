package yatest.resolvers.elements;

import com.codeborne.selenide.SelenideElement;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import org.openqa.selenium.InvalidArgumentException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * ReflectedElementsRegistry --- хранилище всех статичных именованных UI-элементов.
 * Использует аннотацию @CucumberName на классах и статических членах классов.
 */
public class ReflectedElementsRegistry {
    private static final String ROOT_PACKAGE = "yatest";
    private static final Class<CucumberName> ANNOTATION = CucumberName.class;

    private final Logger logger = Logger.getLogger(ReflectedElementsRegistry.class.getName());
    private final Map<String, SelenideElement> registry = new HashMap<>();

    private ReflectedElementsRegistry() {
        logger.info("Создаю реестр именованных элементов");

        try {
            registerSelenideElementsNames();
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Не могу собрать все объявления элементов'", e);
        }

        logger.info("Завершил создание реестра именованных элементов");
    }

    public static ReflectedElementsRegistry get() {
        return Singleton.INSTANCE;
    }

    public SelenideElement resolve(String id) {
        if (!registry.containsKey(id)) {
            throw new NoSuchElementException(format("Не могу найти объявление элемента `%s`", id));
        }

        return registry.get(id);
    }

    private void registerSelenideElementsNames() throws IllegalAccessException {
        Set<? extends Class<?>> annotatedClasses = getTypesAnnotatedWith(ANNOTATION, ROOT_PACKAGE);

        logger.info(format("В пакете `%s` нашел %d классов с аннотацией @%s",
                ROOT_PACKAGE, annotatedClasses.size(), ANNOTATION.getSimpleName()));

        if (annotatedClasses.isEmpty()) {
            throw new IllegalStateException(format("Не нашел в пакете %s ни одного класса с аннотацией @%s",
                    ROOT_PACKAGE, ANNOTATION.getSimpleName()));
        }

        logger.info("Начинаю регистрацию именованных элементов");

        for (Class<?> containerClass : annotatedClasses) {
            final String containerName = containerClass.getAnnotation(ANNOTATION).value();
            logger.info(format("Сканирую класс `%s` %s", containerName, containerClass.getName()));

            for (Field field : containerClass.getFields()) {
                if (!field.isAnnotationPresent(ANNOTATION)) {
                    logger.fine(format("Пропускаю поле `%s`: нет аннотации @%s", field.getName(), ANNOTATION.getSimpleName()));
                    continue;
                }

                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }

                final String fieldName = field.getAnnotation(ANNOTATION).value();
                final SelenideElement element = (SelenideElement) field.get(null);

                if (element == null) {
                    throw new IllegalStateException(format("Не могу получить значение поля `%s` в классе `%s`; " +
                            "поле обязано быть объявлено как static.", field, containerClass.getName()));
                }

                registerElement(containerName + "/" + fieldName, element);
                registerElement(fieldName, element);
            }

            logger.info(format("Закончил с классом %s", containerClass.getName()));
        }

        logger.info("Закончил регистрацию именованных элементов");
    }

    private void registerElement(String fieldName, SelenideElement element) {
        logger.info(format("Регистрирую элемент `%s`", fieldName));

        if (registry.containsKey(fieldName)) {
            throw new InvalidArgumentException(format("Элемент с именем `%s` уже зарегистрирован", fieldName));
        }

        registry.putIfAbsent(fieldName, element);
    }

    @SuppressWarnings("SameParameterValue")
    private Set<? extends Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> annotation, String mainPackage) {
        final ClassGraph classGraph = new ClassGraph().whitelistPackages(mainPackage);
        classGraph.enableAllInfo();

        try (ScanResult result = classGraph.scan()) {
            return result.getClassesWithAnnotation(annotation.getName())
                    .stream()
                    .map(ClassInfo::loadClass)
                    .collect(Collectors.toSet());
        }
    }

    private static class Singleton {
        private static final ReflectedElementsRegistry INSTANCE = new ReflectedElementsRegistry();
    }
}
