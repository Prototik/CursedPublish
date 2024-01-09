package rocks.aur.cursedpublish.internal

import org.gradle.workers.internal.*
import org.jetbrains.annotations.*
import rocks.aur.cursedpublish.*
import java.util.*

@ApiStatus.Internal
@CursedInternalApi
internal class DefaultCursedPublishWorkResult : DefaultWorkResult, CursedPublishWorkResult {
    constructor(exception: Throwable) : super(false, exception) {
        this.file = Optional.empty()
    }

    constructor(fileId: UInt, file: CursedFile) : super(true, null) {
        this.fileId = fileId
        this.file = Optional.of(file)
    }

    override var fileId: UInt = 0U
        get() {
            if (field == 0U) {
                throw IllegalStateException("No file was published")
            }
            return field
        }
        private set

    override val file: Optional<out CursedFile>

    private lateinit var _nested: MutableList<CursedPublishWorkResult>
    override val nested: Collection<CursedPublishWorkResult>
        get() {
            if (!::_nested.isInitialized) return emptyList()
            return Collections.unmodifiableCollection(_nested)
        }

    fun addNested(nested: CursedPublishWorkResult) {
        if (!::_nested.isInitialized) {
            _nested = mutableListOf(nested)
        } else {
            _nested += nested
        }
    }
}