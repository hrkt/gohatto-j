package io.hrkt.gohatto.rule;

public class SimpleResult implements Result {
    private ResultType resultType;
    private String description;
    private String appliedRuleName;

    public SimpleResult(ResultType resultType, String description, String appliedRuleName) {
        this.resultType = resultType;
        this.description = description;
        this.appliedRuleName = appliedRuleName;
    }

    @Override
    public ResultType getResultType() {
        return resultType;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getAppliedRuleName() {
        return appliedRuleName;
    }
}
