package com.nghianguyen.intheneighborhood.ui.tasklist;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.nghianguyen.intheneighborhood.data.model.Task;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TaskListPresenterTest {

    private TaskListContract.Presenter presenter;

    @Mock
    TaskListContract.View view;

    @Mock
    TaskListModel model;

    @Before
    public void setup(){
        presenter = new TaskListPresenter(view, model);

        ArrayList<Task> tasks = new ArrayList<>();
        when(model.getTasks()).thenReturn(tasks);
    }

    @Test
    public void presenter_onAttach(){
        presenter.onAttach();

        verify(model).getTasks();
        verify(view).showTasks(any(ArrayList.class));
        verify(model).startLocationUpdates(any(LocationRequest.class), any(LocationCallback.class));
    }

    @Test
    public void presenter_secondOnAttach(){
        presenter.onAttach();
        presenter.onAttach();

        verify(model, times(1)).getTasks();
        verify(view, times(1)).showTasks(any(ArrayList.class));
        verify(model, times(2)).startLocationUpdates(any(LocationRequest.class), any(LocationCallback.class));
    }

    @Test
    public void presenter_refresh(){
        presenter.refreshTasks();

        verify(model).getTasks();
        verify(view).updateAdapter(any(ArrayList.class));
    }

    @Test
    public void presenter_setTaskDone(){
        Task task = mock(Task.class);

        presenter.setTaskDone(task, false);

        verify(model).updateTask(any(Task.class));
        verify(model).getTasks();
        verify(view).updateAdapter(any(ArrayList.class));
    }

    @Test
    public void presenter_deleteTask() {
        Task task = mock(Task.class);
        presenter.deleteTask(task);

        verify(model).deleteTask(any(Task.class));

        verify(model).getTasks();
        verify(view).updateAdapter(any(ArrayList.class));

        verify(view).displayMessage(anyString());
    }

    @Test
    public void presenter_setProximityAlertsOn(){
        presenter.setProximityAlertsOn(true);

        verify(model).setProximityAlarmOn(true);
    }

    @Test
    public void presenter_startLocationUpdates(){
        presenter.startLocationUpdates();

        verify(model).startLocationUpdates(any(LocationRequest.class), any(LocationCallback.class));
    }

    @Test
    public void presenter_onDetach(){
        presenter.onDetach();

        verify(model).stopLocationUpdates(any(LocationCallback.class));
    }

    @Test
    public void presenter_stopLocationUpdates(){
        presenter.stopLocationUpdates();

        verify(model).stopLocationUpdates(any(LocationCallback.class));
    }

}
