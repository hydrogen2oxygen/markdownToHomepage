package net.hydrogen2oxygen.markdowntohomepage.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigurationObject {

    private Map<String,Website> websites = new HashMap<>();
}
