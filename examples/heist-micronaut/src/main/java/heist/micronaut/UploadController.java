/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2022 Agorapulse.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
