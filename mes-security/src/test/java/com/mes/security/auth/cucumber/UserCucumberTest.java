package com.mes.security.auth.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "com.mes.security.auth.cucumber",
        plugin = {"pretty", "html:target/cucumber-report.html"},
        tags = "not @ignore"
)
public class UserCucumberTest {
}
