package com.example.marinecrm;

public interface Command<I, O> {
    O execute(I input);
}
