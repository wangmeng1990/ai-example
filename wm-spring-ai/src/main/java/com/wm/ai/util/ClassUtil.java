package com.wm.ai.util;

import org.springframework.context.annotation.Description;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class ClassUtil {
    public static String[] getFunctions(Class<?> ...clazz) {
        List<Class<?>> classList = Arrays.stream(clazz).filter(aClass -> aClass.isAnnotationPresent(Description.class)
            &&Function.class.isAssignableFrom(aClass)).toList();
        String[] names = new String[classList.size()];
        classList.stream().map(aClass -> StringUtils.uncapitalize(aClass.getSimpleName())).toList().toArray(names);
        return names;
    }
}
