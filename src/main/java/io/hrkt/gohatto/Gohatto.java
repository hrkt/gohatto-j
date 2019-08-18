package io.hrkt.gohatto;

import io.hrkt.gohatto.exception.GohattoApplicationException;
import io.hrkt.gohatto.rule.DoNotUseForbiddenPackage;
import io.hrkt.gohatto.rule.Result;
import io.hrkt.gohatto.rule.Rule;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Gohatto {
    static final Logger LOG = LoggerFactory.getLogger(Gohatto.class);

    ArrayList<Rule> rules;

    public Gohatto() {
        rules= new ArrayList<Rule>();
    }

    public void init(String jarFilePath, String forbiddenPackageListPath) {
        val file = Paths.get(forbiddenPackageListPath);
        try {
            val text = Files.readAllLines(file);
            val forbiddenPackages = text.toArray(new String[0]);
            rules.add(new DoNotUseForbiddenPackage(jarFilePath, forbiddenPackages));
        } catch (IOException e) {
            throw new GohattoApplicationException(e);
        }
    }

    public List<Result> executeRules() {
        val results = rules.stream()
                .flatMap(r -> r.apply().stream())
                .collect(Collectors.toList());
        results.stream().forEach(r -> {
            LOG.info(r.getResultType() + "\t" + r.getAppliedRuleName() + "\t" + r.getDescription());
        });
        LOG.info("rules applied to: " + results.size());
        return results;
    }
}
