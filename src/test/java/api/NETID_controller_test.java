package api;

import controllers.NETID_controller;
import org.junit.Test;
import org.junit.Assert;


public class NETID_controller_test {


    @Test
    public void testValid() {
        NETID_controller netID = new NETID_controller();

        Assert.assertEquals("mmc265", netID.getNETID());
    }


}
