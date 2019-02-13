package yatest.resolvers;

import com.codeborne.selenide.SelenideElement;
import cucumber.api.TypeRegistry;
import cucumber.api.TypeRegistryConfigurer;
import yatest.resolvers.elements.ReflectedElementsRegistry;
import yatest.resolvers.glob.DeQuoter;
import yatest.resolvers.glob.GlobParameter;
import io.cucumber.cucumberexpressions.ParameterType;

import java.util.Locale;

@SuppressWarnings("unused")
public class TypeRegistryConfiguration implements TypeRegistryConfigurer {

    @Override
    public Locale locale() {
        return Locale.ENGLISH;
    }

    @Override
    public void configureTypeRegistry(TypeRegistry typeRegistry) {
        // когда параметров станет больше, надо вынести определения во внешние классы в этом же package

        typeRegistry.defineParameterType(new ParameterType<>(
                "glob",
                ".*",
                GlobParameter.class,
                (String s) -> GlobParameter.from(DeQuoter.stripBoundingQuotes(s))));

        typeRegistry.defineParameterType(new ParameterType<>(
                "el",
                ".*",
                SelenideElement.class,
                ReflectedElementsRegistry.get()::resolve));
    }
}
