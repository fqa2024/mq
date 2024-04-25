package com.example.demo;

public class test {
    private static final D D = test.D.S;

    private enum D {
        S, F, W
    }

    public static void main(String[] args) {
        System.out.println("");
        switch (test.D) {
            case S:
                System.out.println("1");
                break;
            case F:
                System.out.println("2");
                break;
            default:
                break;
        }
    }
}
