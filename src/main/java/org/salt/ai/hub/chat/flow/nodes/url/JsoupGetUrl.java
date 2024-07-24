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

import jakarta.annotation.Resource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.salt.ai.hub.frame.chat.structs.dto.AiChatDto;
import org.salt.ai.hub.frame.chat.structs.enums.RoleType;
import org.salt.ai.hub.frame.chat.structs.vo.AiChatRequest;
import org.salt.ai.hub.frame.utils.EncipherUtil;
import org.salt.function.flow.context.IContextBus;
import org.salt.function.flow.node.FlowNodeWithReturn;
import org.salt.function.flow.node.register.NodeIdentity;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.util.ArrayList;

@NodeIdentity(nodeId = "jsoupGetUrl")
public class JsoupGetUrl extends FlowNodeWithReturn<AiChatDto> {

    @Resource
    RedisTemplate redisTemplate;

    @Override
    public AiChatDto doProcess(IContextBus iContextBus) {

        //Init param
        AiChatRequest aiChatRequest = (AiChatRequest) iContextBus.getParam();
        AiChatDto aiChatDto = (AiChatDto) iContextBus.getTransmitInfo(AiChatDto.class.getName());

        String prompt = "Answer the question based on this article:%s";

        String pageContent;
        try {
            String key = EncipherUtil.MD5(aiChatRequest.getUrl());
            assert key != null;
            pageContent = (String)redisTemplate.opsForValue().get(key);
            if (pageContent == null) {
                pageContent = extractTextFromUrl(aiChatRequest.getUrl());
                redisTemplate.opsForValue().set(key, pageContent);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        AiChatDto.Message message = new AiChatDto.Message();
        message.setRole(RoleType.SYSTEM.getCode());
        message.setContent(String.format(prompt, pageContent));
        if (aiChatDto.getMessages() == null) aiChatDto.setMessages(new ArrayList<>());
        aiChatDto.getMessages().add(message);

        return aiChatDto;
    }

    public static String extractTextFromUrl(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        return extractTextFromHtml(doc);
    }

    private static String extractTextFromHtml(Document htmlContent) {
        StringBuilder textContent = new StringBuilder();
        Elements paragraphs = htmlContent.select("p");
        for (Element paragraph : paragraphs) {
            textContent.append(paragraph.text()).append("\n");
        }
        return textContent.toString();
    }
}
