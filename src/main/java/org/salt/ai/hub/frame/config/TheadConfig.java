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

package org.salt.ai.hub.frame.config;

import lombok.extern.slf4j.Slf4j;
import org.salt.ai.hub.frame.utils.ThreadUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Configuration
public class TheadConfig {

    @Value("${global.threadpool.coreSize:100}")
    private int coreSize;
    @Value("${global.threadpool.maxSize:300}")
    private int maxSize;
    @Value("${global.threadpool.queueCapacity:1024}")
    private int queueCapacity;
    @Value("${global.threadpool.keepAlive:60}")
    private int keepAlive;

    @Bean
    @Primary
    @ConditionalOnMissingBean(name = "eventThreadPool")
    public ThreadPoolTaskExecutor eventThreadPool() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(coreSize);
        threadPoolTaskExecutor.setMaxPoolSize(maxSize);
        threadPoolTaskExecutor.setQueueCapacity(queueCapacity);
        threadPoolTaskExecutor.setKeepAliveSeconds(keepAlive);
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.setThreadNamePrefix("eventThreadPool-");
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        threadPoolTaskExecutor.setTaskDecorator(runnable -> {
            Map<String, Object> map = new HashMap<>(ThreadUtil.getAll());
            return () -> {
                try {
                    ThreadUtil.putAll(map);
                    runnable.run();
                } catch (Exception e) {
                    log.error("threadPoolTaskExecutor run Exception:{}", e.getMessage());
                } finally {
                    ThreadUtil.clean();
                }
            };
        });
        Runtime.getRuntime().addShutdownHook(new Thread(threadPoolTaskExecutor::shutdown));
        return threadPoolTaskExecutor;
    }
}
