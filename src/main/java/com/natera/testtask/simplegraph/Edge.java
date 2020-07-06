package com.natera.testtask.simplegraph;

import com.natera.testtask.simplegraph.enums.Direction;

public interface Edge<T> {
    T getFromVertex();

    T getToVertex();

    default int getWeight(){
        return 0;
    }

    default Direction getDirection(){
        return Direction.ONE_WAY;
    }
}
