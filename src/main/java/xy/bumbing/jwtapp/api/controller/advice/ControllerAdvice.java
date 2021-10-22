package xy.bumbing.jwtapp.api.controller.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import xy.bumbing.jwtapp.api.security.dto.ErrorResponse;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;


@RestControllerAdvice
@Slf4j
public class ControllerAdvice {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse invalidArgument(HttpServletRequest req, MethodArgumentNotValidException e) {
        return ErrorResponse.builder().status(HttpStatus.BAD_REQUEST.value()).message(validation(e)).timestamp(Instant.now()).error(e.getClass().getName()).path(req.getRequestURI()).build();
    }

    //예상 못한 에러처리
    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse serverError(HttpServletRequest req, Exception e) {
        log.error("예상치 못한 에러", e);
        return ErrorResponse.builder().status(HttpStatus.INTERNAL_SERVER_ERROR.value()).message(e.getMessage()).timestamp(Instant.now()).error(e.getClass().getName()).path(req.getRequestURI()).build();
    }

    /**
     * validation error 체크 method
     */
    private String validation(MethodArgumentNotValidException errors) {

        StringBuilder sb = new StringBuilder();
        if (!ObjectUtils.isEmpty(errors) && errors.hasErrors()) {
            for (FieldError fieldError : errors.getFieldErrors()) {
                sb.append(fieldError.getDefaultMessage());
                sb.append("\n");
            }
        }
        return sb.toString();
    }


}