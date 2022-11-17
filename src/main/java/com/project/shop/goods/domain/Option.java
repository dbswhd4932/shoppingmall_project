package com.project.shop.goods.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@TypeDef(name = "json", typeClass = JsonStringType.class)
public class Option  {

    @OneToOne
    @Id
    @JsonIgnore
    private Long id;

    private Long goodsId;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    private List<Map<String, String>> options;
    
    private int addPrice;   // 추가금액
}
