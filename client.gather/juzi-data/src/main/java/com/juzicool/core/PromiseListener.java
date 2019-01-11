package com.juzicool.core;

public interface PromiseListener {

    void onStart(Promise promise);

    void onEnd(Promise promise);
}
