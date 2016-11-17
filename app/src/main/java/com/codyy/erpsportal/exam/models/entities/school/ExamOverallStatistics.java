package com.codyy.erpsportal.exam.models.entities.school;

/**
 * Created by eachann on 2016/1/13.
 */
public class ExamOverallStatistics {
    private String className;
    private float real;
    private int total;
    private int type;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public float getReal() {
        return real;
    }

    public void setReal(float real) {
        this.real = real;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
