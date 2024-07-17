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

package org.salt.ai.hub.frame.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.salt.ai.hub.frame.utils.JsonUtil;
import org.salt.ai.hub.frame.vo.Rsp;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Slf4j
public class UserInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = getTokenFromAuthorization(request);;
//        if (StringUtils.isEmpty(token)) {
//            log.warn("nologin, token is null, req:{} ", request.getRequestURI());
//            nologin(response);
//            return false;
//        }

        log.info("preHandle, token:{}", token);

        //Handle tokens here

        UserUtil.setUserId(1L);

        return true;
    }

    private static String getTokenFromAuthorization(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (StringUtils.isNotBlank(auth)) {
            String[] auths = auth.split(" ");
            if (auths.length > 1 && auths[0].equals("Bearer")) {
                return auths[1];
            }
        }
        return null;
    }

    private void nologin(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader("Content-Type", "application/json;charset=utf-8");
        response.getWriter().write(JsonUtil.toJson(Rsp.builder().code(401).msg("login fail")));
    }
}
