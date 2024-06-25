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

package org.salt.ai.hub.frame.chat.structs.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum AiChatCode {

    MESSAGE("message", "success"),
    ERROR("error", "fail"),
    COMPLETE("complete", "stop")
    ;

    private final String code;
    private final String message;

    AiChatCode(String type, String message) {
        this.code = type;
        this.message = message;
    }

    public static AiChatCode fromCode(String type) {
        for (AiChatCode it : AiChatCode.values()) {
            if (it.getCode().equalsIgnoreCase(type)) {
                return it;
            }
        }
        throw new IllegalArgumentException(AiChatCode.class.getName() + " Invalid code: " + type);
    }

    public boolean equalsV(String code) {
        return StringUtils.equalsIgnoreCase(this.code, code);
    }
}
