package br.com.vieirateam.wyncef.interfaces

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

interface ViewModelScope<T> {

    fun getScope(): CoroutineScope

    fun select()

    fun insert(item: T): Job

    fun update(item: T): Job

    fun delete(item: T): Job
}