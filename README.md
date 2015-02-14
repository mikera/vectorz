![Vectorz Logo](https://raw.github.com/wiki/mikera/vectorz/images/vectorz.png)

Fast double-precision vector and matrix maths library for Java, based around the concept of N-dimensional arrays.

This library is designed for use in games, simulations, raytracers, machine learning etc. 
where fast vector maths is important. 

Some highlights:
 
 - Vectorz can do over *1 billion* 3D vector operations per second on a single thread.
 - Specialised matrix types for efficient optimised operations (identity, diagonal, sparse etc.).
 - Support for arbitrary `n-dimensional` numerical arrays.
 
### Status

Vectorz is reasonably mature, battle tested and being used in production applications. The API is still evolving however as new features get added so you can expect a few minor changes, at least until version 1.0.0

[![Build Status](https://secure.travis-ci.org/mikera/vectorz.png)](http://travis-ci.org/mikera/vectorz) [![Dependency Status](https://www.versioneye.com/user/projects/54deed14271c93aa1200002c/badge.svg?style=flat)](https://www.versioneye.com/user/projects/54deed14271c93aa1200002c)

### Documentation

See the [Vectorz Wiki](https://github.com/mikera/vectorz/wiki)

### Example usage

```Java
    Vector3 v=Vector3.of(1.0,2.0,3.0);		
    v.normalise();                       // normalise v to a unit vector
    		
    Vector3 d=Vector3.of(10.0,0.0,0.0);		
    d.addMultiple(v, 5.0);               // d = d + (v * 5)
    
	Matrix33 m=Matrixx.createXAxisRotationMatrix(Math.PI);
	Vector3 rotated=m.transform(d);      // rotate 180 degrees around x axis	    
```

### Key features

 - Supports **double** typed vectors of arbitrary size
 - Both **mutable** and **immutable** vectors are supported, enabling high performance algorithms
 - Support for **any size** matrices, including **higher dimensional** (NDArray) matrices
 - Ability to create lightweight **view** vectors (e.g. to access subranges of other vectors)
 - Library of useful **mathematical functions** on vectors
 - Vectors have lots of **utility functionality** implemented - Cloneable, Serializable, Comparable etc.
 - Various **specialised types** of vectors/matrices types (e.g. identity matrices, diagonal matrices)
 - Support for **affine** and other matrix **transformations**
 - **sparse arrays** for space efficient large vectors and matrices where most elements are zero
 - Operator system provides **composable operators** that can be applied to array elements
 - **Input / output** of vectors and matrices - in various formats including readable edn format

Vectorz is designed to allow the maximum performance possible for vector maths on the JVM.

This focus has driven a number of important design decisions:

 - Support for sparse vectors and other specialised array types
 - Specialised primitive-backed small vectors (1,2,3 and 4 dimensions) and matrices (1x1, 2x2, 3x3 and M*3)
 - Abstract base classes preferred over interfaces to allow more efficient method dispatch
 - Multiple types of vector are provided for optimised performance in special cases
 - Hard-coded fast paths for most common 2D and 3D operations
 - Vector operations are generally not thread safe, by design
 - Concrete classes are generally final
 
If you have a use case that isn't yet well optimised then please post an issue - the aim is to make all common operations as efficient as possible.
