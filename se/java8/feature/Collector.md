## Collector

### Introduce

1. function
   - reducing and summarizing stream elements to a single value
   - grouping element
   - partition element

2) source code

```java
// T: stream type, parameter type
// A: accumulator
// R: return type
public interface Collector<T, A, R> {
    // A function that creates and returns a new mutable result container.
    Supplier<A> supplier();

    // A function that folds a value into a mutable result container.
    BiConsumer<A, T> accumulator();

    // A function that accepts two partial results and merges them.  The
    BinaryOperator<A> combiner();

    // Perform the final transformation from the intermediate accumulation type  {@code A} to the final result type {@code R}.
    Function<A, R> finisher();

    // Returns a {@code Set} of {@code Collector.Characteristics} indicating the characteristics of this Collector.  This set should be immutable.
    Set<Characteristics> characteristics();
}
```
