package com.mchange.v2.c3p0.cfg;

interface Validator<T>
{
    public T validate( T value ) throws InvalidConfigException;
}
