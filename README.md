# vectorz

Fast double-precision vector and matrix maths library for Java.

The intention of this library is for use in games, simulations, raytracers etc. 
where fast vector maths is important.

### Key features

 - Supports double vectors of arbitrary size
 - Primitive-backed small vectors (1,2,3 and 4 dimensions)
 - All vector values are mutable
 - Library of useful mathematical functions on vectors
 - Vectors have lots of utility functionality implemented - Cloneable, Serializable, Comparable etc.

### Focus on performance

 - Multiple types of vector are provided for optimised performance in different cases
 - Fast paths for most 2D and 3D operations
 - Vectors are generally not thread safe, by design
 
 If you have a common case that isn't well optimised yet then please post an issue - the aim is to make all common operations efficient.