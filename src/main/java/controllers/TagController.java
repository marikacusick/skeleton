package controllers;

import api.CreateReceiptRequest;
import api.ReceiptResponse;
import dao.ReceiptDao;
import dao.TagDao;
import generated.tables.records.ReceiptsRecord;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static java.util.stream.Collectors.toList;


@Path("/tags")
@Produces(MediaType.APPLICATION_JSON)
public class TagController {

    final TagDao tags;
    public TagController(TagDao tags){
        this.tags = tags;
    }

    @PUT
    @Path("/{tag}")
    public void toggleTag(@PathParam("tag") String tagName, @Valid @NotNull Integer tagID){
        tags.tagOrUntag(tagName,tagID);

    }
    @GET
    @Path("/{tag}")
    public List<ReceiptResponse> getTagReceipts(@PathParam("tag") String tagName) {
        List<ReceiptsRecord> tagReceiptRecords = tags.getListOfTags(tagName);
        System.out.println(tagReceiptRecords);
        return tagReceiptRecords.stream().map(ReceiptResponse::new).collect(toList());
    }




}
