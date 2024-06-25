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

package org.salt.ai.hub.frame.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;

@Slf4j
public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Long.class, new ToStringSerializer());
        objectMapper.registerModule(module);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static String toJson(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.warn("toJson error:{}", e.getMessage());
        }
        return null;
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        try {
            return objectMapper.readValue(json, classOfT);
        } catch (IOException e) {
            log.warn("fromJson error:{}", e.getMessage());
        }
        return null;
    }

    public static <T> List<T> fromJson(String json, TypeReference<List<T>> typeRef) {
        try {
            return objectMapper.readValue(json, typeRef);
        } catch (IOException e) {
            log.warn("fromJson error:{}", e.getMessage());
        }
        return null;
    }

    public static boolean isJson(String json) {
        if (StringUtils.isEmpty(json)) {
            return false;
        }
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            return jsonNode.isObject();
        } catch (IOException e) {
            log.warn("isJson error:{}", e.getMessage());
        }
        return false;
    }
}