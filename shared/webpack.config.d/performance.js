// Compose Multiplatform web apps embed the Skiko rendering engine (an ~8 MiB WebAssembly module),
// so webpack's default budgets (244 KiB, sized for classic JavaScript sites) do not apply.
config.performance = {
    maxAssetSize: 12 * 1024 * 1024,
    maxEntrypointSize: 6 * 1024 * 1024,
};

// Only report errors and warnings: the per-module listing would otherwise show the source map
// warnings of third-party Kotlin libraries, which the Kotlin/JS configuration already ignores.
config.stats = "errors-warnings";
