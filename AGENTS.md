# Agent Guidelines for Vectorz

Guidance for any coding agent working in this repository. `CLAUDE.md` simply
includes this file, so there is one source of truth.

## Project Overview

Vectorz is a fast double-precision vector and matrix maths library for Java,
built around the concept of N-dimensional numerical arrays. It targets games,
simulations, raytracers and machine learning — anywhere vector maths is on the
hot path.

Single-module Maven project, `net.mikera:vectorz`, LGPL, published to Maven
Central. ~295 main source files, ~103 test source files.

**Performance is the primary design constraint.** Most of the unusual structure
in this codebase (abstract classes instead of interfaces, dozens of specialised
array types, `final` concrete classes, hard-coded fast paths) exists to keep JIT
dispatch cheap. Do not "simplify" these away — see *Performance Principles*.

## Build System

Maven. Compiles to **Java 17 bytecode**; developed and tested on JDK 21+
(currently JDK 25 locally). Requires Maven 3.6.3+.

```bash
mvn -B clean test       # compile + full test suite (~11s, 460 tests)
mvn -B clean install    # install to local repository
mvn -B javadoc:javadoc  # Javadoc, with doclint enabled
```

The parent POM is `net.mikera:mikera-pom`; runtime dependencies are
`net.mikera:randomz`, `net.mikera:mathz` and `us.bpsm:edn-java` (for edn
input/output). JUnit 5 (Jupiter) and Caliper are test-scoped.

## Verifying Changes

Do not report work as complete on the strength of a compile. The full suite runs
in about ten seconds — there is no excuse for skipping it:

```bash
mvn -B clean test javadoc:javadoc
```

If tests fail, say so and quote the failure. Never describe a red build as
passing.

**Javadoc is part of the gate.** `doclint` is enabled as `all,-missing`: syntax,
HTML and reference errors fail the build, but a member without a doc comment
does not. The Javadoc jar ships to Maven Central on release, so a doclint
regression breaks the release rather than merely producing untidy docs. When
adding or editing a doc comment, keep `@param` names matching the actual
parameters, do not put `@return` on a `void` method, and wrap anything
containing `<`, `>` or generics in `{@code ...}`.

CI runs the same commands via GitHub Actions
(`.github/workflows/build.yml`) on pushes and pull requests to `master` and
`develop`, across JDK 17, 21 and 25. A local run on any one of those is a good
proxy, but the matrix is what gates a merge — a change that compiles only on a
newer JDK will fail the 17 job.

## Code Organization

All code lives under the `mikera` root package.

| Package | Purpose |
|---------|---------|
| `mikera.arrayz` | `INDArray` — the root N-dimensional array abstraction, plus `NDArray`, `Array` and the `Arrayz` factory |
| `mikera.vectorz` | `AVector` and the 1-D API; primitive vectors (`Vector1`–`Vector4`), `Scalar`, `BitVector`, `Op`/`Op2` operator system, `Vectorz` factory |
| `mikera.vectorz.impl` | ~50 specialised vector implementations (sparse, strided, joined, wrapped, computed, views) |
| `mikera.vectorz.ops` | Concrete `Op` implementations (`Logistic`, `Tanh`, `Exp`, `Clamp`, …) |
| `mikera.vectorz.util` | `ErrorMessages`, `DoubleArrays`, `IntArrays`, `VectorzException`, `Testing` |
| `mikera.matrixx` | `AMatrix` and the 2-D API; `Matrix`, `Matrix11/22/33`, `Matrixx` factory |
| `mikera.matrixx.impl` | ~50 specialised matrix implementations (diagonal, banded, triangular, sparse row/column, permutation, quadtree, views) |
| `mikera.matrixx.decompose` | LU/QR/SVD/Cholesky/Eigen/Hessenberg/Bidiagonal — public entry points return `I*Result` interfaces, algorithms live in `impl` |
| `mikera.matrixx.solve` | Linear solvers (LU- and QR-backed) |
| `mikera.matrixx.algo` | Determinant, Inverse, PseudoInverse, Rank, FFT, PLS, Multiplications |
| `mikera.indexz` | `AIndex`/`Index` — integer index vectors used by sparse types |
| `mikera.transformz` | Affine and linear transformations |

### Naming conventions

- `A*` — abstract base class (`AVector`, `AMatrix`, `ASparseVector`). This is
  where most behaviour lives.
- `I*` — interface (`INDArray`, `IMatrix`, `ISparse`, `IQRResult`).
- `*z` — static factory / utility class, normally with a private constructor
  (`Vectorz`, `Matrixx`, `Arrayz`, `Indexz`, `Transformz`, `Ops`). New
  construction helpers belong here, not as public constructors.
- Concrete implementations are normally `final` and constructed via static
  `create(...)` / `wrap(...)` / `of(...)` methods rather than public
  constructors.

## Core Contracts

Every array type must honour these. They are enforced by the generic test
harness, and breaking them causes subtle corruption rather than clean failures.

- **`exactClone()`** returns a deep copy *of the same concrete class*, fully
  independent of the original. Not `Object.clone()`, not a generalised copy.
- **`clone()`** returns a fully mutable copy which may be of a different class —
  it should pick the most efficient representation, preserving sparsity where
  practical.
- **`validate()`** checks internal structural invariants and throws
  `VectorzException` on failure. Always call `super.validate()`. Failure means a
  bug or data corruption, not bad user input.
- **`isMutable()` / `isFullyMutable()`** — `isFullyMutable()` guarantees *every*
  position can store *any* double. A type can be partially mutable (e.g.
  `BitVector`, sparse types with a fixed index) — `isMutable()` true,
  `isFullyMutable()` false.
- **`isView()`** is a *heuristic only*. Never let correctness depend on it.
- **`get`/`set` bounds-check; `unsafeGet`/`unsafeSet` do not.** Use the unsafe
  variants only inside loops where bounds are already proven.
- **Immutable types must stay immutable** — mutating methods throw. Shared
  cached instances (e.g. `ZeroVector`, `Vector0.INSTANCE`) depend on this.
- **Views share storage.** A view mutation must be visible through the backing
  array and vice versa.
- **Not thread safe, by design.** Do not add synchronisation.
- Types are `Serializable` with an explicit `serialVersionUID`; cached
  singletons implement `readResolve()`.
- Argument checks go through `AVector.checkIndex` / `checkLength` /
  `checkSameLength` and `mikera.vectorz.util.ErrorMessages`. Do not hand-roll
  exception strings — `ErrorMessages` keeps them consistent and informative.

## Testing

Tests are JUnit 5 under `src/test/java`, mirroring the main package structure.

The important pattern is the **generic test harness**: rather than writing
bespoke tests per type, every array implementation is fed through a shared
battery of contract tests.

- `mikera.vectorz.TestVectors.doGenericTests(AVector)` — ~50 contract checks,
  and delegates to `TestArrays.testArray(...)`
- `mikera.arrayz.TestArrays.testArray(INDArray)` — the `INDArray`-level battery
- `mikera.matrixx.TestMatrices` — the equivalent for matrices

**When you add a new vector, matrix or array implementation, register at least
one instance of it in the relevant generic test method** (in `TestVectors`,
grouped into `g_*` test methods by family). This is the single most important
testing rule in the repo — an unregistered type is effectively untested.

Prefer extending the generic harness over adding a one-off test: a check added
to `doGenericTests` immediately applies to every existing implementation.
`mikera.vectorz.util.Testing` provides randomised array construction helpers.

`src/test/java/mikera/*/performance/` holds Caliper benchmarks, not correctness
tests. `src/test/java/example/` holds runnable usage examples — keep them
working, as they double as documentation.

## Performance Principles

These are deliberate and long-standing. Respect them:

- **Abstract classes are preferred over interfaces** for the core hierarchy, to
  allow more efficient method dispatch.
- **Concrete classes are generally `final`** — helps inlining and devirtualisation.
- **Specialised types over generic ones.** Adding a new specialised
  vector/matrix type with an optimised implementation is the normal way to make
  something faster here, not adding branches to a general type.
- **Hard-coded fast paths** for common 2-D and 3-D operations are expected, not
  duplication to be refactored away.
- **Sparse types** must keep operations proportional to non-zero count, not
  length. Check `nonZeroCount()`, `visitNonZero(...)` and the `ISparse` /
  `ISparseVector` contracts before touching them.
- Avoid allocation in hot paths; reuse `double[]` buffers where the surrounding
  code already does.
- Do not add logging, synchronisation, or defensive copying to element-level
  operations.

If you believe a performance principle is wrong for a particular change, raise
it rather than quietly deviating.

## Code Style

- **Tabs for indentation** (the whole codebase uses tabs — match it).
- **British English** in comments, Javadoc and documentation.
- Javadoc on public API methods; concise, focused on contract and complexity.
- Match the surrounding code's naming, comment density and idiom. This codebase
  has a distinctive terse style — follow it rather than importing conventions
  from elsewhere.
- Prefer editing existing files to creating new ones, except when adding a new
  specialised array type (which correctly gets its own file in the relevant
  `impl` package).

## Development Workflow

### Branch Strategy

- `develop` — active development (current default working branch)
- `master` — release branch
- Feature branches as needed

### Commit Identity

This repository pins its own identity in `.git/config`: commits and pushes use
`mikera`, with the GitHub noreply address
`212007+mikera@users.noreply.github.com`, via a local credential helper that
requests that named account's token. `gh` operations (issues, PRs, releases) act as
`brittleboye`. See `../AGENTS.md` for how the two are kept separate, and do not
rely on the global git config — it defaults to `brittleboye` and cannot push
here.

### Releases

Releases go through the `maven-release-plugin` (see the `vectorz-*` git tags and
the `[maven-release-plugin]` commits). Do not hand-edit the version in `pom.xml`
to cut a release.

## Resources

- Wiki / documentation: https://github.com/mikera/vectorz/wiki
- Repository: https://github.com/mikera/vectorz
- Usage examples: `src/test/java/example/`
