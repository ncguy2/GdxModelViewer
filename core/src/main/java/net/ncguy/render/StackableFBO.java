package net.ncguy.render;

public interface StackableFBO {

    void BeginFBO();
    void EndFBO();

    String Name();
}