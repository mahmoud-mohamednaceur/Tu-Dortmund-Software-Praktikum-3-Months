package service

import view.IRefreshable

/**
 * Abstract refreshing service class, tracking a set of [IRefreshable]s to notify upon updates.
 */
abstract class ARefreshingService {
    /** List of all registered [IRefreshable]s. */
    private val refreshables: MutableList<IRefreshable> = mutableListOf()

    /**
     * Registers given [IRefreshable] to this service.
     * The specified object will be notified about changes in this service.
     */
    fun addRefreshable(refreshable: IRefreshable) = refreshables.add(refreshable)

    /**
     * Calls specified method on all registered [IRefreshable]s.
     */
    fun onAllRefreshables(method: IRefreshable.() -> Unit) {
        refreshables.forEach { it.method() }
    }
}