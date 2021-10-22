package xy.bumbing.jwtapp.api.controller.dto;

import lombok.Data;

@Data
public class Response<T> {

    private T data;
    private int status;
    private String code;
}
