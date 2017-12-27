package com.example.rpc;

public class RequestObj {

    private int operandA;
    private int operandB;

    public RequestObj(int a, int b) {
        operandA = a;
        operandB = b;
    }

    public void setOperandA(int a) {
        operandA = a;
    }

    public void setOperandB(int b) {
        operandB = b;
    }

    public int getOperandA() {
        return operandA;
    }

    public int getOperandB() {
        return operandB;
    }

    @Override
    public String toString() {
        return "(" + operandA + ", " + operandB + ")";
    }
}
