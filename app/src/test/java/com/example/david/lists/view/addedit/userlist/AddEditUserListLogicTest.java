package com.example.david.lists.view.addedit.userlist;

import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.view.addedit.common.IAddEditContract;
import com.example.david.lists.view.addedit.common.IAddEditContract.TaskType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.example.david.lists.view.addedit.common.IAddEditContract.TaskType.TASK_ADD;
import static com.example.david.lists.view.addedit.common.IAddEditContract.TaskType.TASK_EDIT;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddEditUserListLogicTest {

    @Mock
    private IAddEditContract.View view;

    @Mock
    private IAddEditContract.ViewModel viewModel;

    @Mock
    private IRepositoryContract.Repository repo;

    private String id = "id";

    private String title = "title";

    private int position = 0;


    private AddEditUserListLogic logic;


    private String errorMessage = "error";

    private String input = "input";


    @Before
    public void setUp() {
        logic = new AddEditUserListLogic(
                view, viewModel, repo, id, title, position
        );
    }


    /**
     * Normal behavior - {@link TaskType#TASK_ADD}
     * - Add the UserList to the repo.
     */
    @Test
    public void saveAdd() {
        ArgumentCaptor<UserList> captor = ArgumentCaptor.forClass(UserList.class);

        when(viewModel.getTaskType()).thenReturn(TASK_ADD);
        when(viewModel.getPosition()).thenReturn(position);

        logic.save(input);

        verify(repo).addUserList(captor.capture());
        assertThat(captor.getValue().getTitle(), is(input));
        assertThat(captor.getValue().getPosition(), is(position));
    }

    /**
     * Normal behavior - {@link TaskType#TASK_EDIT}
     * - Rename the UserList via the repo.
     */
    @Test
    public void saveEdit() {
        when(viewModel.getTaskType()).thenReturn(TASK_EDIT);
        when(viewModel.getId()).thenReturn(id);

        logic.save(input);

        verify(repo).renameUserList(id, input);
    }


    /**
     * Normal behavior
     * - Duh
     */
    @Test
    public void getCurrentTitle() {
        when(viewModel.getCurrentTitle()).thenReturn(title);

        assertThat(
                logic.getCurrentTitle(),
                is(title)
        );
    }


    /**
     * Normal behavior - {@link TaskType#TASK_ADD}
     * - Add the new UserList to the repo.
     * - Finish the View.
     */
    @Test
    public void validateInputAdd() {
        ArgumentCaptor<UserList> captor = ArgumentCaptor.forClass(UserList.class);

        when(viewModel.getTaskType()).thenReturn(TASK_ADD);
        when(viewModel.getPosition()).thenReturn(position);

        logic.validateInput(input);

        verify(repo).addUserList(captor.capture());
        assertThat(captor.getValue().getTitle(), is(input));
        assertThat(captor.getValue().getPosition(), is(position));
        verify(view).finishView();
    }

    /**
     * Normal behavior - {@link TaskType#TASK_EDIT}
     * - Rename the UserList via the repo.
     * - Finish the View.
     */
    @Test
    public void validateInputEdit() {
        when(viewModel.getTaskType()).thenReturn(TASK_EDIT);
        when(viewModel.getId()).thenReturn(id);

        logic.validateInput(input);

        verify(repo).renameUserList(id, input);
        verify(view).finishView();
    }

    /**
     * Error behavior - empty input
     * - Set View's state error.
     */
    @Test
    public void validateInputEmptyInput() {
        String emptyInput = "";

        when(viewModel.getMsgEmptyTitle()).thenReturn(errorMessage);

        logic.validateInput(emptyInput);

        verify(view).setStateError(errorMessage);
        verify(repo, never()).addUserList(any());
        verify(repo, never()).renameUserList(anyString(), anyString());
    }

    /**
     * Error behavior - unchanged input
     * - Set View's state error.
     */
    @Test
    public void validateInputUnchangedInput() {
        when(viewModel.getCurrentTitle()).thenReturn(input);
        when(viewModel.getMsgTitleUnchanged()).thenReturn(errorMessage);

        logic.validateInput(input);

        verify(view).setStateError(errorMessage);
        verify(repo, never()).addUserList(any());
        verify(repo, never()).renameUserList(anyString(), anyString());
    }
}