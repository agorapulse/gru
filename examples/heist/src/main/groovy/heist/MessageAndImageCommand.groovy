package heist

import grails.validation.Validateable
import org.springframework.web.multipart.MultipartFile

class MessageAndImageCommand implements Validateable {
    String message
    MultipartFile theFile

    static constraints = {
        message nullable: false
        theFile nullable: false
    }

}
