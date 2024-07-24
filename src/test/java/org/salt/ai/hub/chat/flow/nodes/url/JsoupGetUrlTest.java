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

package org.salt.ai.hub.chat.flow.nodes.url;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.salt.ai.hub.TestApplication;
import org.salt.ai.hub.frame.chat.structs.dto.AiChatDto;
import org.salt.ai.hub.frame.chat.structs.vo.AiChatRequest;
import org.salt.ai.hub.frame.chat.structs.vo.AiChatResponse;
import org.salt.ai.hub.frame.utils.JsonUtil;
import org.salt.function.flow.context.ContextBus;
import org.salt.function.flow.context.IContextBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
@SpringBootConfiguration
public class JsoupGetUrlTest {

    @Autowired
    JsoupGetUrl jsoupGetUrl;

    @Test
    public void doProcess() {

        AiChatRequest aiChatRequest = new AiChatRequest();
        aiChatRequest.setUrl("https://mp.weixin.qq.com/s/zPQTX_5qG8nMEkQCzYP4ow");
        ConcurrentMap<String, Object> concurrentMap = new ConcurrentHashMap<>();
        concurrentMap.put(AiChatDto.class.getName(), new AiChatDto());

        IContextBus<AiChatRequest, AiChatResponse> iContextBus = ContextBus.<AiChatRequest, AiChatResponse>builder().param(aiChatRequest).transmitMap(concurrentMap).build();

        AiChatDto aiChatDto = jsoupGetUrl.doProcess(iContextBus);
        System.out.println(JsonUtil.toJson(aiChatDto));
    }
}