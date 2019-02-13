package yatest.blocks.yandex;

import com.codeborne.selenide.SelenideElement;
import yatest.resolvers.elements.CucumberName;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

@CucumberName("Простой поиск")
public class SearchSimpleBlock {

    @CucumberName("Поиск")
    public static SelenideElement searchInput = $("input[aria-label='Запрос']");

    @CucumberName("Быстрый ответ")
    public static SelenideElement quickReply = $x("//span[.='Быстрый ответ: ']/..");

    @CucumberName("Найти")
    public static SelenideElement searchButton = $x("//button[.='Найти']");

    @CucumberName("Текущая температура")
    public static SelenideElement temperature = $("div.weather-forecast__current-temp");
}
