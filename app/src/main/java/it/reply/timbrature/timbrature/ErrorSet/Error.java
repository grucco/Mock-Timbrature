package it.reply.timbrature.timbrature.ErrorSet;

/**
 //  Error
 //  Timbrature
 //
 //  Created by Raffaele Forgione @ Syskoplan Reply.
 //  Copyright © 2017 Acea. All rights reserved.
 */

public class Error {

    private String code;
    private Message message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

}
