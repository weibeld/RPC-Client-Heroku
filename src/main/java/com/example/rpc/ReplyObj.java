package com.example.rpc;

public class ReplyObj {

    private int result;

    public ReplyObj(int n) {
        result = n;
    }

    public void setResult(int n) {
        result = n;
    }


    public int getResult() {
        return result;
    }

    @Override
    public String toString() {
        return result + "";
    }

}
