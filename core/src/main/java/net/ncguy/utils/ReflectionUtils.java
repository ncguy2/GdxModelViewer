package net.ncguy.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ReflectionUtils {

    public static List<Method> GetAllMethods(Class cls) {
        List<Method> methods = new ArrayList<>();
        GetAllMethods(cls, methods);
        return methods;
    }

    public static void GetAllMethods(Class cls, List<Method> methods) {
        Collections.addAll(methods, cls.getDeclaredMethods());
        if(!cls.equals(Object.class))
            GetAllMethods(cls.getSuperclass(), methods);
    }

    public static <T extends Annotation> List<AnnotatedObject<Field, T>> GetAnnotatedFields(Object owner, Class<T> annotationClass) {
        List<AnnotatedObject<Field, T>> fields = new ArrayList<>();
        GetAnnotatedFieldsImpl(owner.getClass(), annotationClass, fields);
        return fields;
    }

    public static <T extends Annotation> void GetAnnotatedFieldsImpl(Class cls, Class<T> annotationClass, List<AnnotatedObject<Field, T>> fields) {
        Field[] declaredFields = cls.getDeclaredFields();

        for (Field declaredField: declaredFields) {
            if (declaredField.isAnnotationPresent(annotationClass))
                fields.add(new AnnotatedObject<>(declaredField, declaredField.getAnnotation(annotationClass)));
        }

        if(cls.getSuperclass().equals(Object.class))
            return;

        GetAnnotatedFieldsImpl(cls.getSuperclass(), annotationClass, fields);
    }

    public static <T extends Annotation> List<AnnotatedObject<Method, T>> GetAnnotatedMethods(Object owner, Class<T> annotationClass) {
        List<AnnotatedObject<Method, T>> methods = new ArrayList<>();
        GetAnnotatedMethodsImpl(owner.getClass(), annotationClass, methods);
        return methods;
    }

    public static <T extends Annotation> void GetAnnotatedMethodsImpl(Class cls, Class<T> annotationClass, List<AnnotatedObject<Method, T>> methods) {
        Method[] declaredMethods = cls.getDeclaredMethods();

        for (Method declaredMethod: declaredMethods) {
            if (declaredMethod.isAnnotationPresent(annotationClass))
                methods.add(new AnnotatedObject<>(declaredMethod, declaredMethod.getAnnotation(annotationClass)));
        }

        if(cls.getSuperclass().equals(Object.class))
            return;

        GetAnnotatedMethodsImpl(cls.getSuperclass(), annotationClass, methods);
    }

    public static Method GetMethod(Class cls, String methodName, Class<?>... paramTypes) {
        try {
            Method method = cls.getDeclaredMethod(methodName, paramTypes);
            if (method != null)
                return method;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        Class superclass = cls.getSuperclass();
        if (superclass == null)
            return null;

        return GetMethod(superclass, methodName, paramTypes);
    }

    public static void InvokeRunnable(Object owner, String... methodName) {
        for (String s: methodName)
            InvokeRunnable(owner, s);
    }

    public static void InvokeRunnable(Object owner, String methodName) {
        try {
            InvokeRunnableImpl(owner, methodName);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void InvokeRunnableImpl(Object owner, String methodName) throws InvocationTargetException, IllegalAccessException {
        Method method = GetMethod(owner.getClass(), methodName);
        if (method == null) return;
        method.invoke(owner);
    }

    public static void SetPrivateField(Object owner, String fieldName, Object value) {
        try {
            SetPrivateFieldImpl(owner, fieldName, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void SetPrivateFieldImpl(Object owner, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field f = owner.getClass()
                .getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(owner, value);
    }

    public static Field GetDeclaredField(Object owner, String fieldName) {
        return GetDeclaredFieldImpl(owner.getClass(), fieldName);
    }

    public static Field GetDeclaredFieldImpl(Class cls, String fieldName) {
        while(cls != Object.class) {
            try {
                return cls.getDeclaredField(fieldName);
            }catch(NoSuchFieldException e) {
                cls = cls.getSuperclass();
            }
        }
        return null;
    }

    public static Optional<Object> GetPrivateField(Object owner, String fieldName) {
        Field field = GetDeclaredField(owner, fieldName);
        if(field == null)
            return Optional.empty();
        return GetPrivateField(owner, field);
    }

    public static Optional<Object> GetPrivateField(Object owner, Class cls, String fieldName) {
        Field field = GetDeclaredFieldImpl(cls, fieldName);
        if(field == null)
            return Optional.empty();
        return GetPrivateField(owner, field);
    }

    public static Optional<Object> GetPrivateField(Object owner, Field field) {
        try {
            return Optional.ofNullable(GetPrivateFieldImpl(owner, field));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static Object GetPrivateFieldImpl(Object owner, Field field) throws IllegalAccessException {
        field.setAccessible(true);
        return field.get(owner);
    }

    public static <T> T Build(String cls, Class<T> baseClass, Object... params) {
        try {
            return BuildImpl(cls, baseClass, params);
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T BuildImpl(String cp, @SuppressWarnings("unused") Class<T> ignored, Object... params) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Class<?> cls = Class.forName(cp);
        return BuildImpl((Class<T>) cls, params);
    }

    public static <T> T Build(Class<T> cls, Object... params) {
        try {
            return BuildImpl(cls, params);
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T BuildImpl(Class<T> cls, Object... params) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Class<?>[] paramTypes = new Class[params.length];
        for (int i = 0; i < params.length; i++)
            paramTypes[i] = params[i].getClass();

        for (int i = 0; i < paramTypes.length; i++) {
            if(paramTypes[i] == Long.class)
                paramTypes[i] = Long.TYPE;
        }

        Constructor<?> ctor = cls.getConstructor(paramTypes);
        Object o = ctor.newInstance(params);
        return (T) o;
    }

    public static class AnnotatedObject<O, A extends Annotation> {
        public O object;
        public A annotation;

        public AnnotatedObject(O object, A annotation) {
            this.object = object;
            this.annotation = annotation;
        }
    }

}