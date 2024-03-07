package org.example.core;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class R<T> {

    Integer code;

    String message;

    T data;

    public static <T> R<T> success(){
        return new R(200, "success", null);
    }

    public static <T> R<T> success(T data){
        return new R(200, "success", data);
    }

    public static <T> R<T> fail(String message){
        return new R(500, message, null);
    }

    public static <T> R<T> fail(String message, T data){
        return new R(500, message, data);
    }
}
