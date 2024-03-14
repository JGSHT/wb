package org.example.security.exception;

import org.springframework.security.core.AuthenticationException;
import org.springframework.util.ObjectUtils;


public class SmsTokenException extends AuthenticationException {

    public SmsTokenException(String msg){
        super(msg);
    }

    public static void expectNotNull(Object object, String msg){
        if (object == null){
            throw new SmsTokenException(msg);
        }
    }

    public static void expectHasText(Object object, String msg){
        expectNotNull(object, msg);
        if (object instanceof String str && ObjectUtils.isEmpty(str)){
            throw new SmsTokenException(msg);
        }
    }

    public static void expectTrue(boolean test, String msg){
        if (!test){
            throw new SmsTokenException(msg);
        }
    }
}
