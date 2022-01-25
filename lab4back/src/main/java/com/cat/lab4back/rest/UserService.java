package com.cat.lab4back.rest;

import com.cat.lab4back.models.Point;
import com.cat.lab4back.tools.DBCommunicator;
import com.google.gson.Gson;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ws.rs.*;
import java.util.ArrayList;

@Singleton
@Lock(LockType.WRITE)
@Path("/user")
public class UserService {

    private DBCommunicator dbCommunicator;

    @PostConstruct
    public void init() {
        dbCommunicator = new DBCommunicator();
    }

    @POST
    @Path("/send")
    @Produces("text/plain")
    public String sendPoint(
            @QueryParam("x") @DefaultValue("0") String x,
            @QueryParam("y") @DefaultValue("0") String y,
            @QueryParam("r") @DefaultValue("0") String r) {

        double dx, dy, dr;

        try {
            dx = Double.parseDouble(x);
            dy = Double.parseDouble(y);
            dr = Double.parseDouble(r);
        }
        catch (NumberFormatException e) {
            return new Gson().toJson(null);
        }

        Point point = new Point(dx, dy, dr);
        point.calc();
        dbCommunicator.sendOne(point);

        return new Gson().toJson(point);
    }

    @DELETE
    @Path("/clear")
    public void delete() {
        dbCommunicator.clearAll();
    }

    @GET
    @Path("/fetch")
    @Produces("text/plain")
    public String fetchAll() {
        ArrayList<Point> list = dbCommunicator.getAll();
        return new Gson().toJson(list);
    }
}
