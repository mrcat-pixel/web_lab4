package com.cat.web_lab3.beans;

import com.cat.web_lab3.models.Point;
import com.cat.web_lab3.tools.DBCommunicator;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;

@Named
@SessionScoped
public class CalculatorBean implements Serializable {

    private double x;
    private int y;
    private double r;

    private ArrayList<Point> bigList;
    private DBCommunicator dbCommunicator;

    @PostConstruct
    public void init() {
        x = 0;
        y = 0;
        r = 3;
        dbCommunicator = new DBCommunicator();
        bigList = dbCommunicator.getAll();
        if (bigList == null) {
            bigList = new ArrayList<>();
        }
    }

    public void reset() {
        dbCommunicator.clearAll();
        bigList.clear();
    }

    public String calc() {
        Point point = new Point(x, y, r);
        point.calc();
        bigList.add(point);
        dbCommunicator.sendOne(point);
        return "update";
    }

    public String describeY() {
        return "Текущее значение: " + y;
    }

    public double getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public double getR() {
        return r;
    }

    public void setX(double x) {
        this.x = ((Long)Math.round(x * 1000)).doubleValue()/1000;
    }
    public void setY(int y) {
        this.y = y;
    }
    public void setR(double r) {
        this.r = r;
    }

    public ArrayList<Point> getBigList() {
        return bigList;
    }
    public void setBigList(ArrayList<Point> bigList) {
        this.bigList = bigList;
    }

    public void increment() {
        x++;
    }
}
