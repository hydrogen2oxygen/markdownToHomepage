package net.hydrogen2oxygen.markdowntohomepage.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MarkDownDocument {

    private Map<String,String> metaData = new HashMap<>();
    private String content;
}
