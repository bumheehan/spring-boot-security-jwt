package xy.bumbing.jwtapp.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import xy.bumbing.jwtapp.api.security.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;

/**
 * 스프링 시큐리티 403 핸들러
 */
@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper om;

    public CustomAccessDeniedHandler(ObjectMapper om) {
        this.om = om;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        log.debug("CustomAccessDeniedHandler - start");

        //403
        //401 응답
        ErrorResponse er = ErrorResponse.builder()
                .path(request.getRequestURI())
                .status(HttpStatus.FORBIDDEN.value())
                .message("Access Denied")
                .timestamp(Instant.now())
                .build();

        // 실패코드 403
        response.setStatus(HttpStatus.FORBIDDEN.value());

        // json 리턴 및 한글깨짐 수정.
        response.setContentType("application/json;charset=utf-8");

        response.getOutputStream().println(this.om.writeValueAsString(er));

        log.debug("CustomAccessDeniedHandler - end");
    }

}