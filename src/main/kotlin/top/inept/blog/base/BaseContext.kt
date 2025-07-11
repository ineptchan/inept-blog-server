package top.inept.blog.base

object BaseContext {
    var threadLocal: ThreadLocal<Long> = ThreadLocal<Long>()

    fun getCurrentId(): Long {
        return threadLocal.get()
    }

    fun setCurrentId(id: Long) {
        threadLocal.set(id)
    }

    fun removeCurrentId() {
        threadLocal.remove()
    }
}