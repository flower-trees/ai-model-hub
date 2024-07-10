/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.salt.ai.hub.frame.chat.client.stream;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSource;
import org.apache.commons.lang3.StringUtils;
import org.salt.ai.hub.frame.chat.client.AiException;
import org.salt.ai.hub.frame.chat.model.ListenerStrategy;
import org.salt.ai.hub.frame.utils.JsonUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Data
public class HttpStreamClient implements InitializingBean {

    private OkHttpClient okHttpClient;

    private long sleepTime = 10;
    private int maxConnections = 200;
    private int maxConnectionsPerHost = 200;
    private int maxIdleConnections = 200;
    private int keepAliveDuration = 10;
    private TimeUnit keepAliveTimeUnit = TimeUnit.MINUTES;
    private long connectTimeout = 30000;
    private long readTimeout = 30000;
    private long writeTimeout = 30000;
    private TimeUnit connectTimeUnit = TimeUnit.SECONDS;
    private String proxyHost;
    private int proxyPort;

    @Override
    public void afterPropertiesSet() {
        ConnectionPool connectionPool = new ConnectionPool(maxIdleConnections, keepAliveDuration, keepAliveTimeUnit);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectionPool(connectionPool)
                .connectTimeout(connectTimeout, connectTimeUnit)
                .readTimeout(readTimeout, connectTimeUnit)
                .writeTimeout(writeTimeout, connectTimeUnit);
        if (StringUtils.isNotBlank(proxyHost)) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            builder.proxy(proxy);
        }
        okHttpClient = builder.build();
        okHttpClient.dispatcher().setMaxRequests(maxConnections);
        okHttpClient.dispatcher().setMaxRequestsPerHost(maxConnectionsPerHost);
    }

    @Async
    public <T> void call(String url, T body, Map<String, String> headers, List<ListenerStrategy> strategyList) {

        log.info("http stream call start");
        strategyList.forEach(ListenerStrategy::onInit);

        String headersJson = JsonUtil.toJson(headers);
        String bodyJson;
        if (body instanceof String) {
            bodyJson = (String) body;
        } else {
            bodyJson = JsonUtil.toJson(body);
        }

        assert bodyJson != null;
        Request request = new Request.Builder()
                .url(url)
                //.headers(Headers.of(headers))
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), bodyJson))
                .build();

        log.info("http stream call, url:{}, headers:{}, body:{}", url, headersJson, JsonUtil.toJson(body));

        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {

                log.info("http stream call open");
                strategyList.forEach(ListenerStrategy::onOpen);

                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    BufferedSource source = responseBody.source();
                    while (!source.exhausted()) {
                        String lineComplete = source.readUtf8LineStrict();
                        if (StringUtils.isNotBlank(lineComplete.trim()) && lineComplete.startsWith("data:")) {
                            log.info("http stream call read, data:{}", lineComplete);
                            int index = "data:".length();
                            if (lineComplete.startsWith("data: ")) {
                                index += 1;
                            }
                            String content = lineComplete.substring(index);
                            if (!StringUtils.equalsIgnoreCase(content, "[DONE]")) {
                                strategyList.forEach(strategy -> strategy.onMessage(content));
                                pause();
                            } else {
                                strategyList.forEach(ListenerStrategy::onClosed);
                            }
                        } else if (StringUtils.isNotBlank(lineComplete.trim()) && lineComplete.startsWith("{")) {
                            log.info("http stream call read, data:{}", lineComplete);
                            if (!StringUtils.equalsIgnoreCase(lineComplete, "[DONE]")) {
                                strategyList.forEach(strategy -> strategy.onMessage(lineComplete));
                                pause();
                            } else {
                                strategyList.forEach(ListenerStrategy::onClosed);
                            }
                        }
                    }
                    responseBody.close();
                }
            } else {
                log.error("http stream call fail, e:response code is {}", response.code());
                strategyList.forEach(strategy -> strategy.onError(new AiException(response.code(), JsonUtil.toJson(response))));
            }
        } catch (IOException e) {
            log.error("http stream call io error, e:{}", e.getMessage());
            strategyList.forEach(strategy -> strategy.onError(e));
        } finally {
            strategyList.forEach(ListenerStrategy::onComplete);
        }
    }

    private void pause() {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            log.warn("http stream call pause, e:{}", e.getMessage());
        }
    }
}
