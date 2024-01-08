package rocks.aur.cursed.publish

@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "Interfaces in this library are stable to use but unstable to implement"
)
annotation class CursedInternalApi
