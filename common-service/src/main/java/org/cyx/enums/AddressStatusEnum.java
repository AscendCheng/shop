package org.cyx.enums;

import lombok.Getter;

@Getter
public enum AddressStatusEnum {
    DEFAULT_ADDRESS(1),
    NOT_DEFAULT_ADDRESS(0);

    private int code;

    AddressStatusEnum(int code) {
        this.code = code;
    }
}
