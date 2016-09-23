package de.viadee.bpmnAnalytics;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public abstract class InitialProcessVariablesBase {

    public Map<String, Object> createVariableMap() throws IllegalArgumentException, IllegalAccessException {
        final Map<String, Object> map = new HashMap<String, Object>();
        final Field[] fields = this.getClass().getDeclaredFields();
        for (final Field field : fields) {
            if (!field.getName().startsWith("this")) {
                final Object fieldValue = field.get(this);
                if (fieldValue != null) {
                    map.put(field.getName(), fieldValue);
                }
            }
        }
        return map;
    }
}
