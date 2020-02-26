package com.moko.support.entity;

import java.io.Serializable;

public enum SlotFrameTypeEnum implements Serializable {
    PID("PID", "90"),
    ;
    private String frameType;
    private String showName;

    SlotFrameTypeEnum(String showName, String frameType) {
        this.frameType = frameType;
        this.showName = showName;
    }


    public String getFrameType() {
        return frameType;
    }

    public String getShowName() {
        return showName;
    }

    public static SlotFrameTypeEnum fromFrameType(int frameType) {
        for (SlotFrameTypeEnum frameTypeEnum : SlotFrameTypeEnum.values()) {
            if (Integer.parseInt(frameTypeEnum.getFrameType(), 16) == frameType) {
                return frameTypeEnum;
            }
        }
        return null;
    }

    public static SlotFrameTypeEnum fromEnumOrdinal(int ordinal) {
        for (SlotFrameTypeEnum frameTypeEnum : SlotFrameTypeEnum.values()) {
            if (frameTypeEnum.ordinal() == ordinal) {
                return frameTypeEnum;
            }
        }
        return null;
    }
}
