package com.example.marinecrm;

public interface Query<I, O> {
    O execute(I input);
}
