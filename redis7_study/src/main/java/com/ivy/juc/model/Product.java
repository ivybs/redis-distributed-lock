package com.ivy.juc.model;


import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "聚划算活动Product信息")
@Data
public class Product {

    private Long id;

    private String name;

    private Integer price;

    private String detail;


}
