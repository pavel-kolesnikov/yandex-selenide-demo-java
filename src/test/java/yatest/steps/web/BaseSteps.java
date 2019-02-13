package yatest.steps.web;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import yatest.resolvers.glob.GlobParameter;

import static com.codeborne.selenide.Condition.visible;
import static org.assertj.core.api.Assertions.assertThat;

public class BaseSteps {
    @Given("открыта страница {}")
    public void openURL(String url) {
        Selenide.open(url);
    }

    @When("в поле {el} ввести {string}")
    public void typeInto(SelenideElement el, String query) {
        el.shouldBe(visible).sendKeys(query);
    }

    @When("нажать на {el}")
    public void click(SelenideElement el) {
        el.click();
    }

    @Then("появится {el}")
    public void checkVisible(SelenideElement el) {
        el.shouldBe(visible);
    }

    @Then("в поле {el} будет текст {glob}")
    public void checkText(SelenideElement el, GlobParameter answer) {
        assertThat(el.text())
                .as("проверяю, что текст в поле `%s` соответствует шаблону `%s`", el.toString(), answer.glob())
                .containsPattern(answer.pattern());
    }
}
