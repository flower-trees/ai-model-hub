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

package org.salt.ai.hub.frame.chat.model;

import org.salt.ai.hub.frame.chat.structs.enums.AiChatCode;
import org.salt.ai.hub.frame.chat.structs.vo.AiChatResponse;

import java.util.List;
import java.util.function.Consumer;

public abstract class DoListener implements ListenerStrategy {

    protected AiChatResponse r;

    protected Consumer<AiChatResponse> responder;
    protected Consumer<AiChatResponse> callback;

    protected boolean result = true;
    protected Throwable throwable;

    public DoListener(Consumer<AiChatResponse> responder, Consumer<AiChatResponse> callback) {
        this.responder = responder;
        this.callback = callback;
        this.r = new AiChatResponse();
        this.r.setMessages(List.of(new AiChatResponse.Message()));
    }

    public void onError(Throwable throwable) {

        this.result = false;
        this.throwable = throwable;

        r.setCode(AiChatCode.ERROR.getCode());
        r.setMessage(AiChatCode.ERROR.getMessage());

        this.responder.accept(r);
        this.callback.accept(r);
    }

    public void onComplete() {

        r.setCode(AiChatCode.COMPLETE.getCode());
        r.setMessage(AiChatCode.COMPLETE.getMessage());

        this.responder.accept(r);
        this.callback.accept(r);
    }
}
