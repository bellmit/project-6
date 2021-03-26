package com.miguan.ballvideo.common.dingtalk;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class TextMessage implements Message {

    private String text;
    private List<String> atMobiles;
    private boolean isAtAll;

    public TextMessage(String text) {
        this.text = text;
    }
    @Override
    public String toJsonString() {
        Map<String, Object> items = new HashMap<>();
        items.put("msgtype", "text");

        Map<String, String> textContent = new HashMap<>();
        if (StringUtils.isBlank(text)) {
            throw new IllegalArgumentException("text should not be blank");
        }
        textContent.put("content", text);
        items.put("text", textContent);

        Map<String, Object> atItems = new HashMap<>();
        if (atMobiles != null && !atMobiles.isEmpty()) {
            atItems.put("atMobiles", atMobiles);
        }
        if (isAtAll) {
            atItems.put("isAtAll", isAtAll);
        }
        items.put("at", atItems);

        return JSON.toJSONString(items);
    }
}
