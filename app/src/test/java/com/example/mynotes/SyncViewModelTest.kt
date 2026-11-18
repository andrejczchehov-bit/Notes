package com.example.mynotes

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Фейковый репозиторий для изоляции тестов SyncViewModel от реальной базы данных Room.
 */
class FakeTaskRepository : ITaskRepository {
    var tasksToReturn: List<Task> = listOf(
        Task(id = 1, title = "Test Task 1", description = "Desc 1", dueDate = System.currentTimeMillis(), priority = 2),
        Task(id = 2, title = "Test Task 2", description = "Desc 2", dueDate = System.currentTimeMillis(), priority = 3)
    )

    override fun getTasksByProfile(profileName: String, ownerLogin: String): LiveData<List<Task>> = MutableLiveData(tasksToReturn)
    override suspend fun insert(task: Task): Long = 1L
    override suspend fun update(task: Task) {}
    override suspend fun delete(task: Task) {}
    override suspend fun getTaskById(id: Long): Task? = tasksToReturn.find { it.id == id }
    override suspend fun getAllTasksList(): List<Task> = tasksToReturn
    override suspend fun deleteCompletedTasks() {}
}

/**
 * Unit-тесты для SyncViewModel (Лабораторная работа №7, Задание 2).
 * Проверяют корректность перехода состояний в Success и Error с помощью kotlinx-coroutines-test.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SyncViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: SyncViewModel
    private lateinit var fakeRepository: FakeTaskRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeTaskRepository()
        viewModel = SyncViewModel(Application(), fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testSyncTasksSuccess() = runTest {
        viewModel.syncTasks(forceSuccess = true)

        val initialState = viewModel.uiState.value
        assertTrue(initialState is SyncUiState.Loading || initialState is SyncUiState.Idle)

        advanceTimeBy(1200)
        runCurrent()

        val finalState = viewModel.uiState.value
        assertTrue("Состояние должно быть Success", finalState is SyncUiState.Success)
        assertEquals(2, (finalState as SyncUiState.Success).tasks.size)
        assertFalse(finalState.isBackgroundSync)
    }

    @Test
    fun testSyncTasksError() = runTest {
        viewModel.syncTasks(forceErrorForTest = NetworkErrorType.NO_INTERNET)

        advanceTimeBy(1200)
        runCurrent()

        val state = viewModel.uiState.value
        assertTrue("Состояние должно быть Error", state is SyncUiState.Error)
        assertEquals("Нет подключения к интернету", (state as SyncUiState.Error).message)
        assertEquals(NetworkErrorType.NO_INTERNET, state.errorType)
    }
}
