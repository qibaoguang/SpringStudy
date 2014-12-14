package org.simple.spring.beans;

import java.util.LinkedHashSet;
import java.util.Set;

public class PropertyValues {

    private final Set<PropertyValue> values;

    public PropertyValues() {
        this.values = new LinkedHashSet<PropertyValue>();
    }

    public Set<PropertyValue> getValues() {
        return values;
    }

    public void add(PropertyValue value) {
        if (!values.add(value)) {
            throw new IllegalStateException("dumplcation value ! value = "
                    + value);
        }
    }
}
