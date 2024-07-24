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

package org.salt.ai.hub.chat.flow;

import org.salt.function.flow.FlowEngine;
import org.salt.function.flow.config.IFlowInit;
import org.springframework.stereotype.Component;

@Component
public class FlowInit implements IFlowInit {

    @Override
    public void configure(FlowEngine flowEngine) {

        flowEngine.builder()
                .id("analyze_webpage_jsoup")
                .next("paramBuilder")
                .next("jsoupGetUrl")
                .next("contextBuilder")
                .next("chatGPTLLM")
                .next("chatSaver")
                .build();
    }
}
