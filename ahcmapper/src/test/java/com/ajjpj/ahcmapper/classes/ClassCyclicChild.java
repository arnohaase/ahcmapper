package com.ajjpj.ahcmapper.classes;


public class ClassCyclicChild {
    private String name;
    private String s;
    
    private ClassCyclicParent parent;

    public ClassCyclicParent getParent() {
        return parent;
    }
    public void setParent(ClassCyclicParent parent) {
        this.parent = parent;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public String getReadOnly() {
        return s;
    }
    public void setWriteOnly(String s) {
        this.s = s;
    }
}
