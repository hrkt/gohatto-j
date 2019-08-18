package io.hrkt.gohatto;

import io.hrkt.gohatto.rule.Result;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class GohattoTest {
    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "DEBUG");
    }

    //@Test
    public void forbid_util_and_com_sun_success() {
        String resDir = "src/test/resources/";
        Gohatto gohatto = new Gohatto();
        gohatto.init(resDir + "testproject.jar", resDir + "rule_forbid_jav-util_and_com-sun.txt");
        List<Result> ret = gohatto.executeRules();
        Assert.assertTrue(ret.size() > 0);
    }

    @Test
    public void inspect_util_package_success() {
        File resourcesDirectory = new File("src/test/resources");
        String resDir = "src/test/resources/";
        Gohatto gohatto = new Gohatto();
        gohatto.init(resDir + "testproject.jar", resDir + "rule_only_permit_java-lang.txt");
        List<Result> ret = gohatto.executeRules();
        Assert.assertTrue(ret.size() > 0);
    }
}
