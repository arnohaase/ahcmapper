package com.ajjpj.ahcmapper;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.ajjpj.ahcmapper.core.diff.AhcMapperDiff;


public interface AhcMapper {
    <S, T> T map(S source, T target);
    <S, T> T map(S source, Class<T> targetClass);
    <S, T> T map(S source, Class<? extends S> sourceClass, Class<T> targetClass);
    <S, T> T map(S source, Class<? extends S> sourceClass, T target, Class<? extends T> targetClass);
    
    <S, T> T map(S source, Class<? extends S> sourceClass, Class<?> sourceElementClass, T target, Class<? extends T> targetClass, Class<?> targetElementClass);
    
    <S, T> List<T> mapList(Collection<S> source, Class<S> sourceElementClass, Class<T> targetElementClass);
    <S, T> Set <T> mapSet (Collection<S> source, Class<S> sourceElementClass, Class<T> targetElementClass);

    <S, T> AhcMapperDiff diff(S source1, S source2, Class<S> sourceClass, Class<T> targetClass);
    <S, T> AhcMapperDiff diff(S source1, S source2, Class<S> sourceClass, Class<?> sourceElementClass, Class<T> targetClass, Class<?> targetElementClass);
}
