package controllers;

import javax.ws.rs.*;


@Path("/netid")

public class NETID_controller {

    public NETID_controller(){
    }

    @GET
    public String getNETID() {
        return "mmc265";
    }
}