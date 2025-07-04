<%--

    SPDX-License-Identifier: Apache-2.0

    Copyright 2018-2025 Agorapulse.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
<!doctype html>
<html>
    <head>
        <title>Page Not Found</title>
        <meta name="layout" content="main">
        <g:if env="development"><asset:stylesheet src="errors.css"/></g:if>
    </head>
    <body>
    <div id="content" role="main">
        <div class="container">
            <section class="row">
                <ul class="col-12 errors">
                    <li>Error: Page Not Found (404)</li>
                    <li>Path: ${request.forwardURI}</li>
                </ul>
            </section>
        </div>
    </div>
    </body>
</html>
