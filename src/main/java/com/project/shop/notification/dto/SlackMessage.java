package com.project.shop.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Slack 메시지 DTO
 *
 * Slack Webhook API 형식에 맞춘 메시지 구조
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SlackMessage {

    /**
     * 간단한 텍스트 메시지 (fallback용)
     */
    private String text;

    /**
     * 블록 형태의 풍부한 메시지 (선택 사항)
     */
    private List<Block> blocks;

    /**
     * Slack Block
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Block {
        private String type;
        private Text text;
        private List<Field> fields;
    }

    /**
     * Slack Text
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Text {
        private String type;
        private String text;
    }

    /**
     * Slack Field (섹션 블록용)
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Field {
        private String type;
        private String text;
    }
}