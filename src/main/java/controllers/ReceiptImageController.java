package controllers;

import api.ReceiptSuggestionResponse;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.Collections;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.ArrayList;


import org.hibernate.validator.constraints.NotEmpty;

import static java.lang.System.out;

@Path("/images")
@Consumes(MediaType.TEXT_PLAIN)
@Produces(MediaType.APPLICATION_JSON)
public class ReceiptImageController {
    private final AnnotateImageRequest.Builder requestBuilder;

    public ReceiptImageController() {
        // DOCUMENT_TEXT_DETECTION is not the best or only OCR method available
        Feature ocrFeature = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        this.requestBuilder = AnnotateImageRequest.newBuilder().addFeatures(ocrFeature);

    }

    /**
     * This borrows heavily from the Google Vision API Docs.  See:
     * https://cloud.google.com/vision/docs/detecting-fulltext
     *
     * YOU SHOULD MODIFY THIS METHOD TO RETURN A ReceiptSuggestionResponse:
     *
     * public class ReceiptSuggestionResponse {
     *     String merchantName;
     *     String amount;
     * }
     */
    @POST
    public ReceiptSuggestionResponse parseReceipt(@NotEmpty String base64EncodedImage) throws Exception {
        Image img = Image.newBuilder().setContent(ByteString.copyFrom(Base64.getDecoder().decode(base64EncodedImage))).build();
        AnnotateImageRequest request = this.requestBuilder.setImage(img).build();

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse responses = client.batchAnnotateImages(Collections.singletonList(request));
            AnnotateImageResponse res = responses.getResponses(0);

            String merchantName = null;
            BigDecimal amount = null;

            // Your Algo Here!!
            // Sort text annotations by bounding polygon.  Top-most non-decimal text is the merchant
            // bottom-most decimal text is the total amount

            if (res.getTextAnnotationsList().size()==0){
                return new ReceiptSuggestionResponse(merchantName, amount);
            }


            for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                out.printf("Position : %s\n", annotation.getBoundingPoly());
                out.printf("Text: %s\n", annotation.getDescription());
            }

            //merchant name

            EntityAnnotation full = res.getTextAnnotationsList().get(0);
            String text = full.getDescription();
            String lines[] = text.split("\\r?\\n");

            merchantName = lines[0];


            List <BigDecimal> numbers = new ArrayList<>();
            List <EntityAnnotation> list_annotations = res.getTextAnnotationsList();
            BigDecimal test;

            for (int i=1; i <res.getTextAnnotationsList().size(); i++){
                String txt = list_annotations.get(i).getDescription();
                System.out.println(txt);
                try{
                    test = new BigDecimal(txt);
                    numbers.add(test);
                    System.out.println("list of numbers" + numbers);
                }
                catch(Exception e){
                    System.out.println("not a number");
                }
            }

            amount = numbers.get(numbers.size()-1);


            return new ReceiptSuggestionResponse(merchantName, amount);
        }
    }
}
