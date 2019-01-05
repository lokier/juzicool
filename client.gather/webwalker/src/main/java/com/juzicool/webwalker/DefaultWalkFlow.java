package com.juzicool.webwalker;


public class DefaultWalkFlow extends WalkFlow {

    private String name;

    @Override
    public DefaultWalkFlow addCase(WalkCase _case, long delay){
        return (DefaultWalkFlow) super.addCase(_case,delay);
    }

    public String getName() {
        return name;
    }

    public DefaultWalkFlow setName(String name) {
        this.name = name;
        return this;
    }

}
