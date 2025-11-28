package com.mysite.test.toxicfood;

public enum ToxicityLevel {
    SAFE("안전"),
    WARNING("주의"),
    DANGEROUS("위험"),
    FATAL("치명적");

    private final String label;

    ToxicityLevel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
