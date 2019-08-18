package io.hrkt.gohatto.rule;

public interface Result {
    enum ResultType {
        ERROR, WARN, INFO, OK
    }

    ResultType getResultType();
    String getDescription();
    String getAppliedRuleName();
}
