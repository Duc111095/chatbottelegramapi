package com.ducnh.chatbotapi.domain.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneralPriceInfo {
    private String refNo;
    private Integer typeMessage;
    private String message;

    @Override
    public String toString() {
        return this.refNo.isEmpty() ? this.message : this.refNo + ": " + this.message;
    }
}