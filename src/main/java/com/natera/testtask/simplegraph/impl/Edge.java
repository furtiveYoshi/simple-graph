package com.natera.testtask.simplegraph.impl;

import com.natera.testtask.simplegraph.enums.Direction;

import java.util.Objects;

public class Edge<T> implements com.natera.testtask.simplegraph.Edge<T> {
    private T fromVertex;
    private T toVertex;
    private int weight;
    private Direction direction;

    public Edge(T fromVertex, T toVertex, int weight, Direction direction) {
        this.fromVertex = fromVertex;
        this.toVertex = toVertex;
        this.weight = weight;
        this.direction = direction;
    }

    public Edge(T fromVertex, T toVertex) {
         this(fromVertex, toVertex, 0, Direction.ONE_WAY);
    }

    @Override
    public T getFromVertex() {
        return fromVertex;
    }

    @Override
    public T getToVertex() {
        return toVertex;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge<?> edge = (Edge<?>) o;
        return weight == edge.weight &&
                Objects.equals(fromVertex, edge.fromVertex) &&
                Objects.equals(toVertex, edge.toVertex) &&
                direction == edge.direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromVertex, toVertex, weight, direction);
    }

    @Override
    public String toString() {
        return "Edge{" +
                "fromVertex=" + fromVertex +
                ", toVertex=" + toVertex +
                ", weight=" + weight +
                ", direction=" + direction +
                '}';
    }
}
