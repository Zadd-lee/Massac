package com.mink.massac.common.entity;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public enum Status {
    ENABLE("enable")
    ,DELETED("deleted")
    ,NOT_USED("notUsed");
    private final String name;

    public static Status of(String value){
        if(value == null){
            return null;
        }
        if(value.equals("Y")){
            return ENABLE;
        }else if(value.equals("N")){
            return DELETED;
        }
        return null;
    }
}
