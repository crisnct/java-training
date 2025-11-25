package com.example.training;

@interface MyAdnnotation {
    String author();
    int version() default 1;
}