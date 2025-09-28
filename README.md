# CodeDNA Playground

A small Kotlin project that demonstrates computing and comparing fingerprints of code artifacts (ZIP files).

## Fingerprint implementation
- content-hash: Computes a SHA‑256 hash over the concatenated contents of all entries inside a ZIP.
- structure-minhash: Computes a MinHash signature based on simple structural features of the ZIP (paths, and size buckets) to estimate Jaccard similarity.
- api-minhash: Computes a MinHash signature over API usages via bytecode to estimate API-level similarity.

## Usage
Compute a fingerprint
```
./gradlew run --args="fingerprint input.zip content-hash"
```
Compute a fingerprint (API MinHash)
```
./gradlew run --args="fingerprint input.zip api-minhash"
```
Compare two archives using MinHash (structure)
```
./gradlew run --args="compare left.zip right.zip structure-minhash"
```

## License
This project is licensed under the MIT license
