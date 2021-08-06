package heist.micronaut;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.PartData;
import io.micronaut.http.multipart.StreamingFileUpload;
import io.reactivex.Flowable;

@Controller("/upload")
public class UploadController {

    @Post(value = "/", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.TEXT_PLAIN)
    Integer countBytes(StreamingFileUpload theFile) {
        return Flowable.fromPublisher(theFile)
            .map(PartData::getBytes)
            .map(bytes -> bytes.length)
            .reduce(Integer::sum)
            .blockingGet();
    }

}
