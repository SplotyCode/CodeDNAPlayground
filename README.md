# CodeDNA Playground

A small Kotlin project that demonstrates computing and comparing fingerprints of code artifacts (ZIP files).

## Fingerprint implementation
- content-hash: Computes a SHA‑256 hash over the concatenated contents of all entries inside a ZIP.
- structure-minhash: Computes a MinHash signature based on simple structural features of the ZIP (file roles, paths, and size buckets) to estimate Jaccard similarity.

## Usage
Compute a fingerprint
```
./gradlew run --args="fingerprint input.zip content-hash"
```
Compare two archives using MinHash
```
./gradlew run --args="compare left.zip right.zip structure-minhash"
```

## License
This project is licensed under the MIT license
