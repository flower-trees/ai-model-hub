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

package org.salt.ai.hub.data.controller;

import org.salt.ai.hub.data.service.AgentService;
import org.salt.ai.hub.data.vo.AgentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/agent")
public class AgentController {

    @Autowired
    AgentService agentService;

    @GetMapping("/{id}")
    @ResponseBody
    public AgentVo load(@PathVariable String id) {
        return agentService.load(id);
    }

    @PostMapping("/list")
    @ResponseBody
    public List<AgentVo> list(@RequestBody AgentVo agentVo) {
        return agentService.list(agentVo);
    }
}
