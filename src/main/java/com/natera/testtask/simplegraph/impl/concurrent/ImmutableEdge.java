package com.natera.testtask.simplegraph.impl.concurrent;

import com.natera.testtask.simplegraph.enums.Direction;
import com.natera.testtask.simplegraph.Edge;
import org.apache.commons.lang.SerializationUtils;

import java.io.Serializable;
import java.util.Objects;

public final class ImmutableEdge<T extends Serializable> implements Edge<T> {
    private final T fromVertex;
    private final T toVertex;
    private final int weight;
    private final Direction direction;

    public ImmutableEdge(T fromVertex, T toVertex, int weight, Direction direction) {
        this.fromVertex = (T) SerializationUtils.clone(fromVertex);
        this.toVertex = (T) SerializationUtils.clone(toVertex);
        this.weight = weight;
        this.direction = direction;
    }

    public ImmutableEdge(T fromVertex, T toVertex) {
         this(fromVertex, toVertex, 0, Direction.ONE_WAY);
    }

    @Override
    public T getFromVertex() {
        return (T) SerializationUtils.clone(fromVertex);
    }

    @Override
    public T getToVertex() {
        return (T) SerializationUtils.clone(toVertex);
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return "ImmutableEdge{" +
                "fromVertex=" + fromVertex +
                ", toVertex=" + toVertex +
                ", weight=" + weight +
                ", direction=" + direction +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImmutableEdge<?> that = (ImmutableEdge<?>) o;
        return weight == that.weight &&
                Objects.equals(fromVertex, that.fromVertex) &&
                Objects.equals(toVertex, that.toVertex) &&
                direction == that.direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromVertex, toVertex, weight, direction);
    }
}
