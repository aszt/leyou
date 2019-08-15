package com.leyou.order.enums;

public enum PayStateEnum {
    NOT_PAY(0),
    SUCCESS(1),
    FAIL(2);

    int value;

    public int getValue() {
        return value;
    }

    PayStateEnum(int value) {
        this.value = value;
    }

}
