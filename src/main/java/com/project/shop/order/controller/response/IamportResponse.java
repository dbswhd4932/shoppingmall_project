package com.project.shop.order.controller.response;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class IamportResponse<T> {

    int code;
    String message;
    T response;
}
