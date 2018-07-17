package net.hydrogen2oxygen.markdowntohomepage.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigurationObject {

    private List<Website> websites = new ArrayList<>();
}
