package io.github.maths.shiny.hooks;

public interface Hook {
    String getName();
    String getType();
    boolean isEnabled();
    void enable();
    void disable();
}