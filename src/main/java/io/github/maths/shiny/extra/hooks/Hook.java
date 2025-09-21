package io.github.maths.shiny.extra.hooks;

public interface Hook {
    String getName();
    String getType();
    boolean isEnabled();
    void enable();
    void disable();
}