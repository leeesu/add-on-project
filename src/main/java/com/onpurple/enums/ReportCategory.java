package com.onpurple.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReportCategory {
    FAKE("거짓,허위"),
    SPAM("광고 및 홍보"),
    HATE("혐오, 비방"),
    ABUSE("폭력이나 성적인 내용"),
    COPY("도용 및 저작권, 초상권 침해");

    private final String description;
    public static boolean isValidCategory(ReportCategory category) {
        for (ReportCategory validCategory : values()) {
            if (validCategory.equals(category)) {
                return true;
            }
        }
        return false;
    }

}
