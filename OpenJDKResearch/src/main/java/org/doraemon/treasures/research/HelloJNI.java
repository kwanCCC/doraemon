package org.doraemon.treasures.research;

public class HelloJNI {
    static {
        System.loadLibrary("hello"); // Load native library at runtime
        // hello.dll (Windows) or libhello.so (Unixes)
    }

    // Declare a native method sayHello() that receives nothing and returns void
    private native void sayHello();

    // ForkJoinPool_WorkQueue_Memory_Leaks Driver
    public static void main(String[] args) {
        new HelloJNI().sayHello();  // invoke the native method
    }
}
