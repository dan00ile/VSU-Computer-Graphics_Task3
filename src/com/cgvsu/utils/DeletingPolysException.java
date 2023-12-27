package com.cgvsu.utils;

public class DeletingPolysException extends RuntimeException {

    public DeletingPolysException(String errorMessage) {
        super("Can't delete polygon(unknown index):" + errorMessage);
    }

}
